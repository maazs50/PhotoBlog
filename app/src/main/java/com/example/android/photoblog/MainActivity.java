package com.example.android.photoblog;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.design.widget.BottomNavigationView;
import android.support.design.widget.FloatingActionButton;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;
import android.support.v4.app.Fragment;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;

public class MainActivity extends AppCompatActivity {
    private FirebaseAuth mAuth;
    Toolbar mainToolbar;
    private FloatingActionButton addPostBtn;
    private FirebaseFirestore firebaseFirestore;
    private String  current_user_id;
    private BottomNavigationView mainbottomNav;
    private HomeFragment homeFragment;
    private AccountFragment accountFragment;
    private NotificationFragment notificationFragment;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        firebaseFirestore=FirebaseFirestore.getInstance();
        setContentView(R.layout.activity_main);
        mainToolbar = findViewById(R.id.main_toolbar);
        setSupportActionBar(mainToolbar);
        getSupportActionBar().setTitle("PhotoBlog");
        mAuth = FirebaseAuth.getInstance();
        if(mAuth.getCurrentUser()!=null) {

            addPostBtn = findViewById(R.id.add_post_btn);
            mainbottomNav = findViewById(R.id.mainBottomNav);
            homeFragment = new HomeFragment();
            accountFragment = new AccountFragment();
            notificationFragment = new NotificationFragment();
            replaceFragment(homeFragment);
            mainbottomNav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
                @Override
                public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                    switch (item.getItemId()) {
                        case R.id.bottom_action_home:
                            replaceFragment(homeFragment);
                            return true;
                        case R.id.bottom_action_account:
                            replaceFragment(accountFragment);
                            return true;
                        case R.id.bottom_action_notif:
                            replaceFragment(notificationFragment);
                            return true;

                        default:
                            return false;
                    }
                }
            });
            addPostBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    startActivity(new Intent(MainActivity.this, NewPostActivity.class));
                }
            });

        }

    }
    @Override
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser=mAuth.getCurrentUser();
        if (currentUser==null){
            sendToLogin();
        }
        else
            {
                //sending to the setup activity if the profile pic is not set
            current_user_id=mAuth.getCurrentUser().getUid();
            firebaseFirestore.collection("Users").document(current_user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                    if (task.isSuccessful()){
                        if (!task.getResult().exists()){
                            sendToSetup();
                        }
                    }else{
                        String error=task.getException().toString();
                        Toast.makeText(MainActivity.this,error,Toast.LENGTH_SHORT).show();
                    }
                }
            });
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
    public void replaceFragment(Fragment fragment){
        FragmentTransaction fragmentTransaction=getSupportFragmentManager().beginTransaction();
        fragmentTransaction.replace(R.id.main_container,fragment);
        fragmentTransaction.commit();

    }
}
