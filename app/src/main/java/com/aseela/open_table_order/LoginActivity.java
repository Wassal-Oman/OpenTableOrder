package com.aseela.open_table_order;

import android.app.ProgressDialog;
import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.util.Patterns;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.aseela.open_table_order.models.User;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.QuerySnapshot;

public class LoginActivity extends AppCompatActivity {

    // widgets
    EditText etEmail, etPassword;

    // firebase
    FirebaseAuth auth;
    FirebaseFirestore db;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        // initialize widgets
        etEmail = findViewById(R.id.et_email);
        etPassword = findViewById(R.id.et_password);

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

    // go to home activity
    public void skipToHome(View view) {
        startActivity(new Intent(this, HomeActivity.class));
    }

    // go to register activity
    public void goToRegister(View view) {
        startActivity(new Intent(this, RegisterActivity.class));
    }

    // method to trigger login button click
    public void login(View view) {
        // get user input
        String email = etEmail.getText().toString().trim();
        String password = etPassword.getText().toString().trim();

        // validation
        if(email.isEmpty()) {
            Toast.makeText(this, "Please enter email", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.isEmpty()) {
            Toast.makeText(this, "Please enter password", Toast.LENGTH_SHORT).show();
            return;
        }

        if(!isValidEmail(email)) {
            Toast.makeText(this, "Please enter a valid email address", Toast.LENGTH_SHORT).show();
            return;
        }

        if(password.length() < 6) {
            Toast.makeText(this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
            return;
        }

        signInUser(email, password);
    }

    // method to sign in user through firebase authentication
    private void signInUser(String email, String password) {
        final ProgressDialog dialog = ProgressDialog.show(this, "Signing in...", "Please wait", false, false);
        auth.signInWithEmailAndPassword(email, password).addOnSuccessListener(new OnSuccessListener<AuthResult>() {
            @Override
            public void onSuccess(AuthResult authResult) {
                // check user type in database
                db.collection("users").document(authResult.getUser().getUid()).get().addOnSuccessListener(new OnSuccessListener<DocumentSnapshot>() {
                    @Override
                    public void onSuccess(DocumentSnapshot documentSnapshot) {
                        dialog.dismiss();
                        User user = documentSnapshot.toObject(User.class);

                        // check user type
                        if(user.getType().equals("Customer")) {
                            startActivity(new Intent(LoginActivity.this, HomeActivity.class));
                        } else {
                            Toast.makeText(LoginActivity.this, "User is not a customer!", Toast.LENGTH_SHORT).show();
                            auth.signOut();
                        }
                    }
                }).addOnFailureListener(new OnFailureListener() {
                    @Override
                    public void onFailure(@NonNull Exception e) {
                        dialog.dismiss();
                        Log.d("USER REGISTRATION", e.getMessage());
                        Toast.makeText(LoginActivity.this, "User does not exist!", Toast.LENGTH_SHORT).show();
                    }
                });
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                dialog.dismiss();
                Toast.makeText(LoginActivity.this, "Wrong email or password", Toast.LENGTH_SHORT).show();
            }
        });
    }

    // method to validate email address
    public static boolean isValidEmail(CharSequence target) {
        return (!TextUtils.isEmpty(target) && Patterns.EMAIL_ADDRESS.matcher(target).matches());
    }
}
