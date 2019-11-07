package com.vitalik.myfirstapp;

import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.Intent;
import android.os.Bundle;

import com.google.android.material.textfield.TextInputLayout;
import com.google.firebase.auth.FirebaseAuth;

import java.util.Objects;

import static android.util.Patterns.EMAIL_ADDRESS;

public class LoginActivity extends AppCompatActivity {

    private TextInputLayout emailField;
    private TextInputLayout passField;

    private FirebaseAuth auth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.login_activity);

        auth = FirebaseAuth.getInstance();

        emailField = findViewById(R.id.email_wrapper);
        passField = findViewById(R.id.pass_wrapper);

        findViewById(R.id.btn_login).setOnClickListener(v ->
                signIn(Objects.requireNonNull(emailField.getEditText()).getText().toString(),
                        Objects.requireNonNull(passField.getEditText()).getText().toString()));

        findViewById(R.id.link_signup).setOnClickListener(
                v -> startActivity(new Intent(this, SignUpActivity.class)));
    }

    private void signIn(final String email, final String pass) {
        if (!isValid(email, pass))
            return;

        auth.signInWithEmailAndPassword(email, pass)
                .addOnCompleteListener(this, task -> {
                    if (task.isSuccessful())
                        onSignInSuccess();
                    else
                        onSignInError();
                });
    }

    private void onSignInSuccess() {
        startActivity(new Intent(this, MainActivity.class));
    }

    private void onSignInError() {
        final AlertDialog alertDialog = new AlertDialog.Builder(this).create();
        alertDialog.setTitle("Authentifiacal invalid");
        alertDialog.setMessage("Check your email or password");
        alertDialog.setButton(AlertDialog.BUTTON_NEUTRAL, "OK",
                (dialog, which) -> dialog.dismiss());
        alertDialog.show();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    public boolean isValid(final String email, final String password) {
        boolean valid = true;

        if (email.isEmpty() || !EMAIL_ADDRESS.matcher(email).matches()) {
            emailField.setError("Write correct email");
            valid = false;
        } else {
            emailField.setError(null);
        }

        if (password.isEmpty() || password.length() < 8) {
            passField.setError("At least 8 characters");
            valid = false;
        } else {
            passField.setError(null);
        }

        return valid;
    }
}

