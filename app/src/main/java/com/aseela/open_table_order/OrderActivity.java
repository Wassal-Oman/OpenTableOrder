package com.aseela.open_table_order;

import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.annotation.NonNull;
import android.support.v7.app.AlertDialog;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

import com.aseela.open_table_order.models.Meal;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;
import com.squareup.picasso.Picasso;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class OrderActivity extends AppCompatActivity {

    // widgets
    TextView tvName, tvDescription, tvPrice;
    ImageView ivMeal;
    EditText etCount;

    // attributes
    Meal meal;
    String customerName = "", customerEmail = "", customerPhone = "";
    int count = 1;
    String deliveryType = "";

    // firebase
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_order);

        // initialize
        tvName = findViewById(R.id.tv_meal_name);
        tvDescription = findViewById(R.id.tv_meal_description);
        tvPrice = findViewById(R.id.tv_meal_price);
        ivMeal = findViewById(R.id.iv_meal);
        etCount = findViewById(R.id.et_count);
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();

        // get passed data
        if(getIntent() != null) {
            meal = (Meal) getIntent().getSerializableExtra("meal");
        }

        // set TextViews
        tvName.setText(meal.getName());
        tvDescription.setText(meal.getDescription());
        tvPrice.setText(meal.getPrice() + " OMR");

        // load meal image
        Picasso.get()
                .load(meal.getImage())
                .resize(100, 100)
                .centerCrop()
                .into(ivMeal);

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

        // get user data
        SharedPreferences sp = getSharedPreferences("USER", MODE_PRIVATE);
        customerName = sp.getString("name", "");
        customerEmail = sp.getString("email", "");
        customerPhone = sp.getString("phone", "");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    public void chooseDeliveryType(View view) {
        boolean checked = ((RadioButton) view).isChecked();

        switch(view.getId()) {
            case R.id.rb_receive_at_restaurant:
                if (checked)
                    deliveryType = "Receive At Restaurant";
                    break;
            case R.id.rb_home_delivery:
                if (checked)
                    deliveryType = "Home Delivery";
                    break;
        }
    }

    public void subtract(View view) {
        if(count > 1) {
            count--;
        } else {
            count = 1;
        }

        // set count text
        etCount.setText(String.valueOf(count));
    }

    public void add(View view) {
        count++;

        // set count text
        etCount.setText(String.valueOf(count));
    }

    public void makeOrder(View view) {
        // get order data
        float totalPrice = Float.parseFloat(meal.getPrice()) * count;

        // get current date
        Date date = Calendar.getInstance().getTime();
        SimpleDateFormat df = new SimpleDateFormat("dd-MMM-yyyy", Locale.US);
        String currentDate = df.format(date);

        // prepare order data
        final Map<String, Object> orderData = new HashMap<>();
        orderData.put("customer_id", auth.getCurrentUser().getUid());
        orderData.put("customer_name", customerName);
        orderData.put("customer_email", customerEmail);
        orderData.put("customer_phone", customerPhone);
        orderData.put("meal_id", meal.getId());
        orderData.put("meal_name", meal.getName());
        orderData.put("meal_price", meal.getPrice());
        orderData.put("total_price", String.format(Locale.US,"%.03f", totalPrice));
        orderData.put("order_date", currentDate);
        orderData.put("order_count", String.valueOf(count));
        orderData.put("delivery_type", deliveryType);

        // display an alert dialog to confirm order
        new AlertDialog.Builder(this)
            .setTitle("Confirm Order")
            .setMessage("Are you sure you want to order this meal?")
            .setPositiveButton("Confirm", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    final ProgressDialog progressDialog = ProgressDialog.show(OrderActivity.this, "Sending Order", "Please wait...", false, false);
                    db.collection("orders").document().set(orderData).addOnCompleteListener(new OnCompleteListener<Void>() {
                        @Override
                        public void onComplete(@NonNull Task<Void> task) {
                            progressDialog.dismiss();
                            if(task.isSuccessful()) {
                                Toast.makeText(OrderActivity.this, "Order has been confirmed and sent successfully", Toast.LENGTH_SHORT).show();
                            } else {
                                Toast.makeText(OrderActivity.this, "Cannot process your order!", Toast.LENGTH_SHORT).show();
                            }
                        }
                    });
                }
            })
            .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.cancel();
                }
            })
            .setIcon(android.R.drawable.ic_dialog_alert)
            .show();
    }
}
