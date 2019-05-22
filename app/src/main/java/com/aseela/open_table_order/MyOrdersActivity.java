package com.aseela.open_table_order;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.aseela.open_table_order.models.Order;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QueryDocumentSnapshot;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;

public class MyOrdersActivity extends AppCompatActivity {

    // widgets
    ListView lvOrders;
    TextView tvOrderAvailability;

    // attributes
    List<Order> orders;

    // firebase
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_my_orders);

        // initialize
        lvOrders = findViewById(R.id.lv_orders);
        tvOrderAvailability = findViewById(R.id.tv_order_availability);
        orders = new ArrayList<>();
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // toolbar
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    protected void onStart() {
        super.onStart();

        // check if user has logged in
        if(auth.getCurrentUser() == null) {
            startActivity(new Intent(this, LoginActivity.class));
            finish();
        }

        // load list of user's orders
        final ProgressDialog dialog = ProgressDialog.show(this, "Loading Orders", "Please wait...", false, false);
        db.collection("orders").whereEqualTo("customer_id", auth.getCurrentUser().getUid()).get().addOnCompleteListener(new OnCompleteListener<QuerySnapshot>() {
            @Override
            public void onComplete(@NonNull Task<QuerySnapshot> task) {
                dialog.dismiss();
                if(task.isSuccessful()) {
                    for(QueryDocumentSnapshot document : task.getResult()) {

                        Log.d("Order Data", document.getId() + " => " + document.getData());

                        // create an order object
                        Order order = document.toObject(Order.class);

                        // add order to order list
                        orders.add(order);
                    }

                    // view meals
                    if(orders.size() > 0) {
                        tvOrderAvailability.setVisibility(View.GONE);
                        viewOrders(orders);
                    } else {
                        tvOrderAvailability.setVisibility(View.VISIBLE);
                    }
                } else {
                    tvOrderAvailability.setVisibility(View.VISIBLE);
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

    private void viewOrders(List<Order> orders) {
        MyAdapter adapter = new MyAdapter(this, null, orders);
        lvOrders.setAdapter(adapter);
    }

    // private class to view data in ListView
    class MyAdapter extends BaseAdapter {

        private List<Order> data;
        private LayoutInflater inflater = null;
        Order order = null;

        public MyAdapter(Activity activity, Resources resources, List<Order> list) {
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

            public TextView name;
            public TextView date;
            public TextView count;
            public TextView price;
        }

        @Override
        public View getView(int i, View view, ViewGroup viewGroup) {
            @SuppressLint("ViewHolder") View v = inflater.inflate(R.layout.custom_order_layout, null);
            MyAdapter.ViewHolder holder= new MyAdapter.ViewHolder();

            if(v != null){
                holder.name = v.findViewById(R.id.tv_order_name);
                holder.date = v.findViewById(R.id.tv_order_date);
                holder.count = v.findViewById(R.id.tv_order_count);
                holder.price = v.findViewById(R.id.tv_order_price);
            }

            if(data.size() > 0){
                order = data.get(i);
                holder.name.setText(order.getMeal_name());
                holder.date.setText(order.getOrder_date());
                holder.count.setText(order.getOrder_count());
                holder.price.setText(order.getTotal_price());
            }

            return v;
        }
    }
}
