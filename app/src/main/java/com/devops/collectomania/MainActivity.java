package com.devops.collectomania;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.View;
import android.view.Window;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    Button login;
    EditText email,password;
    TextView signUp,forgotTextLink;
    SharedPreferences sp;
    private FirebaseAuth mAuth;
    private FirebaseUser CurrentUser;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getSupportActionBar().hide();

        setContentView(R.layout.activity_main);

        email = findViewById(R.id.etEmailSignup);
        password = findViewById(R.id.etPasswordSignUp);
        login = findViewById(R.id.btnSignIn);
        signUp = findViewById(R.id.txtSignup);
        forgotTextLink = findViewById(R.id.forgotpasswordClickHere);

        mAuth = FirebaseAuth.getInstance();


        login.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                String  Email = email.getText().toString();
                String  Password = password.getText().toString();

                mAuth.signInWithEmailAndPassword(Email, Password)
                        .addOnCompleteListener(MainActivity.this, new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {
                                if (task.isSuccessful()) {

                                    if(TextUtils.isEmpty(Email) || TextUtils.isEmpty(Password)){
                                        Toast.makeText(MainActivity.this, "Enter all required fiels", Toast.LENGTH_SHORT).show();
                                    }
                                    else{
                                        // Sign in success, update UI with the signed-in user's information
                                        TempUser.Email =Email;

                                        Toast.makeText(MainActivity.this, "Welcome " + TempUser.Email, Toast.LENGTH_SHORT).show();
                                        CurrentUser = mAuth.getCurrentUser();

                                        Intent i = new Intent(MainActivity.this,MainMenu.class);
                                        startActivity(i);
                                        finish();
                                    }

                                } else {
                                    // If sign in fails, display a message to the user.
                                    Toast.makeText(MainActivity.this, "Incorrect Credentials", Toast.LENGTH_SHORT).show();
                                    CurrentUser = mAuth.getCurrentUser();

                                }
                            }
                        });

            }
        });


        signUp.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                Intent i = new Intent(MainActivity.this,SignUpActivity.class);
                startActivity(i);
                finish();
            }
        });

        forgotTextLink.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                EditText resetMail=new EditText(view.getContext());
                AlertDialog.Builder passwordRestDialog = new AlertDialog.Builder(view.getContext());
                passwordRestDialog.setTitle("Reset Password");
                passwordRestDialog.setMessage("Enter Your Email To Receive Reset Link.");
                passwordRestDialog.setView(resetMail);

                passwordRestDialog.setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //extract the email and  send reset link

                        String mail = resetMail.getText().toString();
                        mAuth.sendPasswordResetEmail(mail).addOnSuccessListener(new OnSuccessListener<Void>() {
                            @Override
                            public void onSuccess(Void aVoid) {
                                Toast.makeText(MainActivity.this, "Reset Link Sent to your Email.", Toast.LENGTH_SHORT).show();

                            }
                        }).addOnFailureListener(new OnFailureListener() {
                            @Override
                            public void onFailure(@NonNull Exception e) {
                                Toast.makeText(MainActivity.this, "Error ! Reset Link Not Sent " + e.getMessage(), Toast.LENGTH_SHORT).show();
                            }
                        });
                    }
                });

                passwordRestDialog.setNegativeButton("Back", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        //close the dialog
                    }
                });

                passwordRestDialog.create().show();

            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();

    }
}