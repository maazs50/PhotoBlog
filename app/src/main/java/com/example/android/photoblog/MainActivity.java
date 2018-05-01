package com.example.android.photoblog;

import android.content.Intent;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Toolbar mainToolbar;
    private FloatingActionButton addPostBtn;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mAuth = FirebaseAuth.getInstance();
        mainToolbar=findViewById(R.id.main_toolbar);
        addPostBtn=findViewById(R.id.add_post_btn);
        addPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this,NewPostActivity.class));
            }
        });
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PhotoBlog");
    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser==null){
            sendToLogin();
        }

    }

    private void sendToLogin() {
        Intent intent=new Intent(MainActivity.this, LoginActivity.class);
        startActivity(intent);
        finish();
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.main_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
      switch (item.getItemId()){
          case R.id.action_logout_btn:
          logout();
          return true;
          case R.id.action_settings_btn:
              sendToSetup();
              return true;
          default:
              return false;
      }

    }

    private void logout() {
        mAuth.signOut();

        sendToLogin();
    }
    public void sendToSetup(){
        Intent intent=new Intent(MainActivity.this,SetupActivity.class);
        startActivity(intent);
        finish();
    }
}
