package com.aseela.open_table_order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.net.Uri;
import android.support.annotation.NonNull;
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

import com.aseela.open_table_order.models.Meal;
import com.aseela.open_table_order.models.Restaurant;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.List;

public class MealsActivity extends AppCompatActivity {

    // widgets
    TextView tvName, tvEmail, tvPhone, tvLocation, tvWebsite, tvDelivery, tvNoMealsText;
    ImageView ivRestaurant;
    ListView lvMeals;

    // attributes
    List<Meal> meals;
    Restaurant restaurant;

    // firebase
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_meals);

        // initialize
        tvName = findViewById(R.id.tv_restaurant_name);
        tvEmail = findViewById(R.id.tv_restaurant_email);
        tvPhone = findViewById(R.id.tv_restaurant_phone);
        tvLocation = findViewById(R.id.tv_restaurant_location);
        tvWebsite = findViewById(R.id.tv_restaurant_website);
        tvDelivery = findViewById(R.id.tv_restaurant_delivery);
        tvNoMealsText = findViewById(R.id.tv_no_meals_text);
        ivRestaurant = findViewById(R.id.iv_restaurant);
        lvMeals = findViewById(R.id.lv_meals);
        meals = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // get passed data
        if(getIntent() != null) {
            restaurant = (Restaurant) getIntent().getSerializableExtra("restaurant");
        }

        // set TextViews
        tvName.setText(restaurant.getName());
        tvEmail.setText(restaurant.getEmail());
        tvPhone.setText(restaurant.getPhone());
        tvLocation.setText(restaurant.getLocation());
        tvWebsite.setText(restaurant.getWebsite());
        tvDelivery.setText(restaurant.getDelivery());

        // display image
        Picasso.get()
                .load(restaurant.getImage())
                .resize(350, 300)
                .centerCrop()
                .into(ivRestaurant);

        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // clear list
        meals.clear();

        // get list of meals from database
        final ProgressDialog dialog = ProgressDialog.show(this, "Loading Restaurant Details", "Please wait...", false, false);
        db.collection("meals").whereEqualTo("restaurant_id", restaurant.getId()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dialog.dismiss();
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        Log.d("Meal Data", document.getId() + " => " + document.getData());

                        // create a meal object
                        Meal meal = document.toObject(Meal.class);

                        // add meal to meal list
                        meals.add(meal);
                    }

                    // view meals
                    if(meals.size() > 0) {
                        tvNoMealsText.setVisibility(View.GONE);
                        viewMeals(meals);
                    } else {
                        tvNoMealsText.setVisibility(View.VISIBLE);
                    }
                } else {
                    Toast.makeText(MealsActivity.this, "No Meals Available", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    private void viewMeals(final List<Meal> meals) {
        MyAdapter adapter = new MyAdapter(this, null, meals);
        lvMeals.setAdapter(adapter);

        // go to order page once a user click on an item
        lvMeals.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(auth.getCurrentUser() != null) {
                    Meal meal = meals.get(position);
                    Intent intent = new Intent(MealsActivity.this, OrderActivity.class);
                    intent.putExtra("meal", meal);
                    startActivity(intent);
                } else {
                    Toast.makeText(MealsActivity.this, "You must log in first", Toast.LENGTH_SHORT).show();
                }
            }
        });
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void visitLink(View view) {
        Intent intent = new Intent(Intent.ACTION_VIEW);
        intent.setData(Uri.parse(restaurant.getWebsite()));
        startActivity(intent);
    }

    public void viewMap(View view) {
        Intent intent = new Intent(this, MapsActivity.class);
        intent.putExtra("name", restaurant.getName());
        intent.putExtra("location", restaurant.getLocation());
        intent.putExtra("latitude", restaurant.getLatitude());
        intent.putExtra("longitude", restaurant.getLongitude());
        startActivity(intent);
    }

    // private class to view data in ListView
    class MyAdapter extends BaseAdapter {

        private List<Meal> data;
        private LayoutInflater inflater = null;
        Meal meal = null;

        public MyAdapter(Activity activity, Resources resources, List<Meal> list) {
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
            public TextView description;
            public TextView price;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint("ViewHolder") View v = inflater.inflate(R.layout.custom_meal_layout, null);
            MyAdapter.ViewHolder holder= new MyAdapter.ViewHolder();

            if(v != null){
                holder.image = v.findViewById(R.id.iv_meal);
                holder.name = v.findViewById(R.id.tv_meal_name);
                holder.description = v.findViewById(R.id.tv_meal_description);
                holder.price = v.findViewById(R.id.tv_meal_price);
            }

            if(data.size() > 0){
                meal = data.get(i);
                holder.name.setText(meal.getName());
                holder.description.setText(meal.getDescription());
                holder.price.setText(meal.getPrice() + " OMR");
                Picasso.get()
                        .load(meal.getImage())
                        .resize(100, 100)
                        .centerCrop()
                        .into(holder.image);
            }

            return v;
        }
    }
}
