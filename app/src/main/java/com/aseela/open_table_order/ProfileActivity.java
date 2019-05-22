package com.aseela.open_table_order;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.EditText;

public class ProfileActivity extends AppCompatActivity {

    // widgets
    EditText etName, etEmail, etPhone;

    // attributes
    String name, email, phone;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile);

        // initialize
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);

        // get passed data
        if(getIntent() != null) {
            name = getIntent().getStringExtra("name");
            email = getIntent().getStringExtra("email");
            phone = getIntent().getStringExtra("phone");
        }

        // set EditText values
        etName.setText(name);
        etEmail.setText(email);
        etPhone.setText(phone);

        // get default toolbar
        if(getSupportActionBar() != null) {
            getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }
}
