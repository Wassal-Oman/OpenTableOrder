package com.aseela.open_table_order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import com.aseela.open_table_order.models.Restaurant;
import com.aseela.open_table_order.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class HomeActivity extends AppCompatActivity implements NavigationView.OnNavigationItemSelectedListener {

    // widgets
    TextView drawer_user_name;
    TextView drawer_user_email;
    DrawerLayout drawer;
    NavigationView navigation;
    ListView lvRestaurants;

    // firebase authentication and database
    private FirebaseAuth auth;
    private FirebaseFirestore db;

    // attributes
    String name = "", phone = "", email = "";
    List<Restaurant> restaurants;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);

        // initialize
        drawer = findViewById(R.id.drawer_layout);
        navigation = findViewById(R.id.nav_view);
        lvRestaurants = findViewById(R.id.lv_restaurants);
        restaurants = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // show home button with title
        if(getSupportActionBar() != null){
            getSupportActionBar().setHomeAsUpIndicator(R.drawable.ic_menu);
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }

        // initialize header widgets
        View header = navigation.getHeaderView(0);
        drawer_user_name = header.findViewById(R.id.drawer_user_name);
        drawer_user_email= header.findViewById(R.id.drawer_user_email);

        // set navigation listener
        navigation.setNavigationItemSelectedListener(this);
    }

    @Override
    protected void onStart() {
        super.onStart();

        // get logged user
        FirebaseUser loggedUser = auth.getCurrentUser();

        if(loggedUser != null) {
            // get user details
            db.collection("users").document(loggedUser.getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                @Override
                public void onSuccess(DocumentSnapshot documentSnapshot) {
                    // retrieve user data
                    User user = documentSnapshot.toObject(User.class);

                    // check if user exists
                    if(user != null) {
                        drawer_user_name.setText(user.getName());
                        drawer_user_email.setText(user.getEmail());

                        // set parameters
                        name = user.getName();
                        email = user.getEmail();
                        phone = user.getPhone();

                        // store data inside shared preferences
                        storeUserData(user.getName(), user.getEmail(), user.getPhone());
                    }
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Toast.makeText(HomeActivity.this, "Welcome Guest", Toast.LENGTH_SHORT).show();
                }
            });
        }

        loadListOfRestaurants();
    }

    private void loadListOfRestaurants() {
        // clear list
        restaurants.clear();

        // get list of restaurants from database
        final ProgressDialog dialog = ProgressDialog.show(this, "Loading Restaurants", "Please wait...", false, false);
        db.collection("restaurants").get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dialog.dismiss();
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        Log.d("Restaurant Data", document.getId() + " => " + document.getData());

                        // create a restaurant object
                        Restaurant restaurant = document.toObject(Restaurant.class);

                        // add restaurant to restaurant list
                        restaurants.add(restaurant);
                    }

                    // view products
                    if(restaurants.size() > 0) {
                        viewRestaurants(restaurants);
                    }
                } else {
                    Toast.makeText(HomeActivity.this, "No Restaurants Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void viewRestaurants(final List<Restaurant> restaurants) {
        MyAdapter adapter = new MyAdapter(this, null, restaurants);
        lvRestaurants.setAdapter(adapter);

        // go to meals activity once user clicks on an item
        lvRestaurants.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Restaurant restaurant = restaurants.get(position);
                Intent intent = new Intent(HomeActivity.this, MealsActivity.class);
                intent.putExtra("restaurant", restaurant);
                startActivity(intent);
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        if(id == android.R.id.home) {
            if (drawer.isDrawerOpen(GravityCompat.START)) {
                drawer.closeDrawer(GravityCompat.START);
            } else {
                drawer.openDrawer(GravityCompat.START);
            }
        }

        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onNavigationItemSelected(@NonNull MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        if (id == R.id.nav_profile) {
            // check if user logged in
            if(auth.getCurrentUser() != null) {
                // go to profile page
                Intent intent = new Intent(this, ProfileActivity.class);
                intent.putExtra("name", name);
                intent.putExtra("email", email);
                intent.putExtra("phone", phone);
                startActivity(intent);
            } else {
                Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            }

        } else if(id == R.id.nav_orders) {
            if(auth.getCurrentUser() != null) {
                // go to orders page
                startActivity(new Intent(this, MyOrdersActivity.class));
            } else {
                Toast.makeText(this, "You must log in first", Toast.LENGTH_SHORT).show();
            }
        } else if(id == R.id.nav_logout) {
            // sign out
            auth.signOut();
            finish();
        }

        drawer = findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    private void storeUserData(String name, String email, String phone) {
        // store data into a Shared Preferences
        SharedPreferences sp = getSharedPreferences("USER", MODE_PRIVATE);
        SharedPreferences.Editor editor = sp.edit();
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("phone", phone);
        editor.apply();
    }

    // private class to view data in ListView
    class MyAdapter extends BaseAdapter {

        private List<Restaurant> data;
        private LayoutInflater inflater = null;
        Restaurant restaurant = null;

        public MyAdapter(Activity activity, Resources resources, List<Restaurant> list) {
            this.data = list;
            inflater = ( LayoutInflater )activity.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        }

        @Override
        public int getCount() {
            if(data.size() <= 0){
                return 1;
            }

            return data.size();
        }

        @Override
        public Object getItem(int i) {
            return i;
        }

        @Override
        public long getItemId(int i) {
            return 0;
        }

        public class ViewHolder{

            public ImageView image;
            public TextView name;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint("ViewHolder") View v = inflater.inflate(R.layout.custom_restaurant_layout, null);
            MyAdapter.ViewHolder holder= new MyAdapter.ViewHolder();

            if(v != null){
                holder.image = v.findViewById(R.id.iv_restaurant);
                holder.name = v.findViewById(R.id.tv_restaurant_name);
            }

            if(data.size() > 0){
                restaurant = data.get(i);
                holder.name.setText(restaurant.getName());
                Picasso.get()
                        .load(restaurant.getImage())
                        .resize(350, 300)
                        .centerCrop()
                        .into(holder.image);
            }

            return v;
        }
    }
}
