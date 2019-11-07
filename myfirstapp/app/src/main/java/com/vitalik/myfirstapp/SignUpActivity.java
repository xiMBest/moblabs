package com.vitalik.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.Objects;

public class SignUpActivity extends AppCompatActivity {

    private static final String REG_EXP = "^[A-Za-z]+$";
    private static final int PASS_LENGTH = 8;

    private TextInputLayout emailField;
    private TextInputLayout nameField;
    private TextInputLayout phoneField;
    private TextInputLayout passField;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.signup_activity);

        auth = FirebaseAuth.getInstance();

        initFields();

        findViewById(R.id.btn_signup).setOnClickListener(v ->
                signUp(Objects.requireNonNull(emailField.getEditText()).getText().toString(),
                        Objects.requireNonNull(nameField.getEditText()).getText().toString(),
                        Objects.requireNonNull(phoneField.getEditText()).getText().toString(),
                        Objects.requireNonNull(passField.getEditText()).getText().toString()));

        findViewById(R.id.link_login).setOnClickListener(
                v -> startActivity(new Intent(this, LoginActivity.class)));
    }

    private void signUp(final String email, final String name,
                        final String phone, final String pass) {
        if (!isValid(email, name, phone, pass))
            return;

        auth.createUserWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful()) {
                        onCreateSuccess(name);
                    } else {
                        onCreateError();
                    }
                });
    }

    private void onCreateSuccess(final String name) {
        FirebaseUser user = auth.getCurrentUser();

        UserProfileChangeRequest profileUpdates = new UserProfileChangeRequest.Builder()
                .setDisplayName(name).build();

        if (user != null) {
            user.updateProfile(profileUpdates)
                    .addOnCompleteListener(task -> {
                        if (task.isSuccessful()) {
                            startActivity(new Intent(this, MainActivity.class));
                        }
                    });
        }
    }

    private void onCreateError() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Sign up fail");
        alertDialog.setMessage("Email or phone already registered");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    private void initFields() {
        emailField = findViewById(R.id.email_wrapper);
        nameField = findViewById(R.id.name_wrapper);
        phoneField = findViewById(R.id.phone_wrapper);
        passField = findViewById(R.id.pass_wrapper);
    }

    public boolean isValid(final String email, final String name,
                           final String phone, final String password) {
        boolean valid = true;

        if (email.isEmpty() || !android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Write a correct email");
            valid = false;
        } else {
            emailField.setError(null);
        }

        if (phone.isEmpty() || !android.util.Patterns.PHONE.matcher(phone).matches()) {
            phoneField.setError("Write a correct phone");
            valid = false;
        } else {
            phoneField.setError(null);
        }

        if (!name.matches(REG_EXP)) {
            nameField.setError("Write correct name pls");
            valid = false;
        } else {
            nameField.setError(null);
        }

        if (password.isEmpty() || password.length() < PASS_LENGTH) {
            passField.setError("At least 8 characters");
            valid = false;
        } else {
            passField.setError(null);
        }

        return valid;
    }
}
