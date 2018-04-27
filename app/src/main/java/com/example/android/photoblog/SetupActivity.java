package com.example.android.photoblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.view.View;
import android.widget.Toast;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupActivity extends AppCompatActivity {
    Toolbar setup;
    private CircleImageView setupImage;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        setup=findViewById(R.id.setupToolbar);
        setupImage=findViewById(R.id.profile_image);
        setSupportActionBar(setup);
        getSupportActionBar().setTitle("Account Setup");
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(SetupActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else{
                        Toast.makeText(SetupActivity.this,"Permission Granted",Toast.LENGTH_SHORT).show();
                    }
                }




            }
        });
    }

//    @Override
//    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
//        super.onActivityResult(requestCode, resultCode, data);
//
//    }
}
