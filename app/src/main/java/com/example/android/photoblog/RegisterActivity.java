package com.example.android.photoblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import android.widget.Toolbar;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import org.w3c.dom.Text;

public class RegisterActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;

    private EditText reg_email_field;
    private EditText reg_pass_field;
    private EditText reg_confirm_pass_field;
    private Button reg_btn;
    private Button reg_login_btn;
    private ProgressBar reg_progress;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        reg_email_field = findViewById(R.id.reg_email);
        reg_pass_field = findViewById(R.id.reg_pass);
        reg_confirm_pass_field = findViewById(R.id.reg_confirm_pass);
        reg_btn = findViewById(R.id.reg_btn);
        reg_login_btn = findViewById(R.id.reg_login_btn);
        reg_progress = findViewById(R.id.reg_progress);
        mAuth = FirebaseAuth.getInstance();
        Log.i("Created","RegisterActivity");
//Creates a user
        reg_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String email=reg_email_field.getText().toString();
                String pass = reg_pass_field.getText().toString();
                String confirm_pass = reg_confirm_pass_field.getText().toString();
                if (!TextUtils.isEmpty(email)&&!TextUtils.isEmpty(pass)&&!TextUtils.isEmpty(confirm_pass)){
                    if (pass.equals(confirm_pass)){
                        reg_progress.setVisibility(View.VISIBLE);
                        mAuth.createUserWithEmailAndPassword(email, pass)
                                .addOnCompleteListener( new OnCompleteListener<AuthResult>() {
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        if (task.isSuccessful()) {
                                          Intent intent=new Intent(RegisterActivity.this,SetupActivity.class);
                                          startActivity(intent);
                                          finish();
                                        } else {
                                            // If sign in fails, display a message to the user.

                                            String error=task.getException().toString();
                                            Toast.makeText(RegisterActivity.this,error,Toast.LENGTH_SHORT).show();
                                        }

                                 reg_progress.setVisibility(View.INVISIBLE);
                                    }
                                });
                    }
                    else{
                        Toast.makeText(RegisterActivity.this,"Confirm Password does not match",Toast.LENGTH_LONG).show();
                    }
                }
            }
        });

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.        Log.i("Created","RegisterActivity");
        Log.i("started","RegisterActivity");

        FirebaseUser currentUser = mAuth.getCurrentUser();

        if(currentUser != null){

            sendToMain();

        }
    }

    private void sendToMain() {
        Intent intent=new Intent(RegisterActivity.this,MainActivity.class);
        startActivity(intent);
        finish();
    }
}
