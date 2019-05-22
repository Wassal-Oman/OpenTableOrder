package com.aseela.open_table_order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class RegisterActivity extends AppCompatActivity {

    // widgets
    EditText etName, etEmail, etPhone, etPassword, etConfirmPassword;

    // firebase
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        // initialize widgets
        etName = findViewById(R.id.et_name);
        etEmail = findViewById(R.id.et_email);
        etPhone = findViewById(R.id.et_phone);
        etPassword = findViewById(R.id.et_password);
        etConfirmPassword = findViewById(R.id.et_confirm_password);

        // initialize firebase
        auth = FirebaseAuth.getInstance();
        db = FirebaseFirestore.getInstance();
    }

    @Override
    protected void onStart() {
        super.onStart();

        if(auth.getCurrentUser() != null) {
            startActivity(new Intent(this, HomeActivity.class));
        }
    }

    // go back to login activity
    public void backToLogin(View view) {
        finish();
    }

    // method to trigger register button click
    public void register(View view) {
        // get user input
        String name = etName.getText().toString().trim();
        String email = etEmail.getText().toString().trim();
        String phone = etPhone.getText().toString().trim();
        String password = etPassword.getText().toString().trim();
        String confirmPassword = etConfirmPassword.getText().toString().trim();

        // validation
        if(name.isEmpty()) {
            Toast.makeText(this, "Please enter name", Toast.LENGTH_SHORT).show();
            return;
        }

        if(email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(phone.isEmpty()) {
            Toast.makeText(this, "Please enter phone", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(confirmPassword.isEmpty()) {
            Toast.makeText(this, "Please confirm password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if(phone.length() != 8) {
            Toast.makeText(this, "Phone number must be 8 digits", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!phone.substring(0, 1).equals("9") && !phone.substring(0, 1).equals("7")) {
            Toast.makeText(this, "Phone should start with 9 or 7", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!password.equals(confirmPassword)) {
            Toast.makeText(this, "Passwords don't match", Toast.LENGTH_SHORT).show();
            return;
        }

        createNewUser(name, email, phone, password);
    }

    // method to create new user through firebase authentication and database
    private void createNewUser(final String name, final String email, final String phone, String password) {
        final ProgressDialog dialog = ProgressDialog.show(this, "Creating New User", "Please wait...", false, false);
        auth.createUserWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {

                // create user data
                Map<String, Object> userData = new HashMap<>();
                userData.put("id", auth.getCurrentUser().getUid());
                userData.put("name", name);
                userData.put("email", email);
                userData.put("phone", phone);
                userData.put("type", "Customer");

                // insert to firebase database (cloud forestore)
                db.collection("users").document(auth.getCurrentUser().getUid()).set(userData).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        dialog.dismiss();
                        if(task.isSuccessful()) {
                            Toast.makeText(RegisterActivity.this, "User created successfully", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                            finish();
                        } else {
                            Toast.makeText(RegisterActivity.this, "Cannot insert user data to database", Toast.LENGTH_SHORT).show();
                        }
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(RegisterActivity.this, "Cannot create user", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // method to validate email address
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
