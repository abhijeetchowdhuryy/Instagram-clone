package com.example.fragments;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.HashMap;
import java.util.Objects;

public class RegisterActivity extends AppCompatActivity {
    EditText email, password, userName, fullName;
    MaterialCardView login;
    FirebaseAuth auth;
    ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        email = findViewById(R.id.email);
        password = findViewById(R.id.password);
        userName = findViewById(R.id.userName);
        fullName = findViewById(R.id.fullName);
        login = findViewById(R.id.login);
        auth = FirebaseAuth.getInstance();
        login.setOnClickListener(v ->{
            String emailText = email.getText().toString();
            String passwordText = password.getText().toString();
            String userNameText = userName.getText().toString();
            String fullNameText = fullName.getText().toString();
            if (TextUtils.isEmpty(emailText) || TextUtils.isEmpty(passwordText) || TextUtils.isEmpty(userNameText) || TextUtils.isEmpty(fullNameText)) {
                Toast.makeText(RegisterActivity.this, "Please fill all the fields", Toast.LENGTH_SHORT).show();
            }
                else if (passwordText.length() < 6) {
                    Toast.makeText(RegisterActivity.this, "Password must be at least 6 characters", Toast.LENGTH_SHORT).show();
                }
            else {
                progressDialog = new ProgressDialog(RegisterActivity.this);
                progressDialog.setMessage("Please wait...");
                progressDialog.setCancelable(false);
                progressDialog.show();
                login.setEnabled(false);
                registerUser(emailText, passwordText, userNameText, fullNameText);
            }
        });
    }

    private void registerUser(String emailText, String passwordText, String userNameText, String fullNameText) {
        auth.createUserWithEmailAndPassword(emailText, passwordText).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
            @Override
            public void onComplete(@NonNull Task<AuthResult> task) {
            if (task.isSuccessful()) {
                String uid = Objects.requireNonNull(auth.getCurrentUser()).getUid();
                DatabaseReference reference = FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(uid);//creating a path for our new user data to get saved on
                HashMap<String, String> hashMap = new HashMap<>();//creating a hashmap to store our data
                hashMap.put("id", auth.getCurrentUser().getUid());//putting the id of the user in the hashmap
                hashMap.put("userName", userNameText);
                hashMap.put("fullName", fullNameText);
                hashMap.put("bio", "");
                hashMap.put("imageurl", "default");
                hashMap.put("email", emailText);
                reference.setValue(hashMap).addOnCompleteListener(task1 -> {
                    if (task1.isSuccessful()) {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration Successful", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(RegisterActivity.this, MainActivity.class));
                        finish();
                    }
                    else {
                        progressDialog.dismiss();
                        Toast.makeText(RegisterActivity.this, "Registration Failed", Toast.LENGTH_SHORT).show();
                        auth.signOut();
                    }
                });
            }
            else {
                progressDialog.dismiss();
                login.setEnabled(true);
            }
        }
        });
    }
}