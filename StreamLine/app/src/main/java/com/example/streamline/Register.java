package com.example.streamline;

import android.content.Intent;
import android.os.Bundle;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.example.streamline.model.UserDetail;
import com.example.streamline.util.CustomToast;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.FirebaseDatabase;

public class Register extends AppCompatActivity {

    EditText emailId, firstName, lastName, password;
    Button btnSignUp;
    TextView tvSignIn;

    FirebaseAuth mFirebaseAuth;
    FirebaseDatabase firebaseDatabase;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        emailId = findViewById(R.id.editTextEmail);
        firstName = findViewById(R.id.editTextFirstName);
        lastName = findViewById(R.id.editTextLastName);
        password = findViewById(R.id.editTextPassword);
        btnSignUp = findViewById(R.id.signUpButton);
        tvSignIn = findViewById(R.id.textViewSignIn);

        mFirebaseAuth = FirebaseAuth.getInstance();
        firebaseDatabase = FirebaseDatabase.getInstance();

        btnSignUp.setOnClickListener(view -> {
            final String email = emailId.getText().toString();
            final String fName = firstName.getText().toString();
            final String lName = lastName.getText().toString();
            final String pwd = password.getText().toString();

            if (email.isEmpty()) {
                emailId.setError("Please provide email id");
                emailId.requestFocus();
            } else if (fName.isEmpty()) {
                firstName.setError("Please provide your first name");
                firstName.requestFocus();
            } else if (lName.isEmpty()) {
                lastName.setError("Please provide your last name");
                lastName.requestFocus();
            } else if (pwd.isEmpty()) {
                password.setError("Please provide password");
                password.requestFocus();
            } else if (!(email.isEmpty() && pwd.isEmpty())) {
                mFirebaseAuth.createUserWithEmailAndPassword(email, pwd)
                        .addOnCompleteListener(Register.this, task -> {

                            if (!task.isSuccessful()) {
                                CustomToast.createToast(Register.this,
                                        "SignUp Unsuccessful, Please Try Again!"
                                                + task.getException().getMessage(), true);
                            } else {
                                UserDetail userDetail = new UserDetail(fName, lName);
                                String uid = task.getResult().getUser().getUid();
                                firebaseDatabase.getReference(uid).setValue(userDetail)
                                        .addOnSuccessListener(aVoid -> {
                                            Intent intent = new Intent(Register.this,
                                                    HomePage.class);
                                            intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK |
                                                    Intent.FLAG_ACTIVITY_CLEAR_TASK);
                                            intent.putExtra("name", fName + " " + lName);
                                            startActivity(intent);
                                        });
                            }

                        });
            } else {
                CustomToast.createToast(Register.this, "Error Occurred !", true);
            }
        });

        tvSignIn.setOnClickListener(view -> {
            Intent i = new Intent(Register.this, Login.class);
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK|Intent.FLAG_ACTIVITY_CLEAR_TASK);
            startActivity(i);
        });
    }
}