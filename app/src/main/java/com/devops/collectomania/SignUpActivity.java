package com.devops.collectomania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class SignUpActivity extends AppCompatActivity {

    Button signUpBtn;
    EditText SignUpEmail,SignUpPassword;

    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        SignUpEmail = findViewById(R.id.etEmailSignup);
        SignUpPassword = findViewById(R.id.etPasswordSignUp);
        signUpBtn = findViewById(R.id.btnSignUp);

        mAuth = FirebaseAuth.getInstance();

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String  Email = SignUpEmail.getText().toString();
                String  Password = SignUpPassword.getText().toString();

                mAuth.createUserWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(SignUpActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)){
                                    Toast.makeText(SignUpActivity.this, "Enter details missing", Toast.LENGTH_SHORT).show();
                                }
                                else {
                                    if (task.isSuccessful()) {
                                        // Sign in success, update UI with the signed-in user's information
                                        Toast.makeText(SignUpActivity.this, "Welcome " + Email, Toast.LENGTH_SHORT).show();
                                        CurrentUser = mAuth.getCurrentUser();

                                        TempUser.Email =Email;

                                        Intent i = new Intent(SignUpActivity.this,MainMenu.class);
                                        startActivity(i);
                                        finish();

                                    } else {
                                        // If sign in fails, display a message to the user.
                                        Toast.makeText(SignUpActivity.this, "email used or password cannot contain part of name ", Toast.LENGTH_SHORT).show();
                                        CurrentUser = mAuth.getCurrentUser();

                                    }
                                }
                            }
                        });
            }
        });
    }
}