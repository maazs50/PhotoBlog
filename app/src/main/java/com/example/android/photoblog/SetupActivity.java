package com.example.android.photoblog;

import android.Manifest;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.Uri;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import org.w3c.dom.Text;

import de.hdodenhof.circleimageview.CircleImageView;


public class SetupActivity extends AppCompatActivity {
    private StorageReference storageReference;
    private EditText setupName;
    private Button setupBtn;
    private Toolbar setup;
    private Uri mainImageUri;
    private CircleImageView setupImage;
    private FirebaseAuth firebaseAuth;
    private ProgressBar setupProgess;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_setup);
        //Initializations
        setup=findViewById(R.id.setupToolbar);
        setupImage=findViewById(R.id.setup_image);
        setupName=findViewById(R.id.setup_name);
        setupBtn=findViewById(R.id.setup_btn);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseAuth=FirebaseAuth.getInstance();
    setupProgess=findViewById(R.id.setup_progress);


        setSupportActionBar(setup);
        getSupportActionBar().setTitle("Account Setup");

        //reguests permission and allows to choose image
        setupImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.M){
                    if (ContextCompat.checkSelfPermission(SetupActivity.this, Manifest.permission.READ_EXTERNAL_STORAGE)!= PackageManager.PERMISSION_GRANTED){
                        Toast.makeText(SetupActivity.this,"Permission Denied",Toast.LENGTH_SHORT).show();
                        ActivityCompat.requestPermissions(SetupActivity.this,new String[]{Manifest.permission.READ_EXTERNAL_STORAGE},1);
                    }
                    else{
                        CropImage.activity()
                                .setGuidelines(CropImageView.Guidelines.ON).setAspectRatio(1,1)
                                .start(SetupActivity.this);
                    }
                }




            }
        });


        //Save the details in firebase
        setupBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String user_name=setupName.getText().toString();
                if (!TextUtils.isEmpty(user_name)&&setupImage!=null){
                    //The user logged in id is saved
                    String user_id=firebaseAuth.getCurrentUser().getUid();
                    setupProgess.setVisibility(View.VISIBLE);
                    StorageReference image_path=storageReference.child("profile_images").child(user_id+".jpg");
                    image_path.putFile(mainImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                           if(task.isSuccessful()){
                               Toast.makeText(SetupActivity.this,"The image uploaded successfully",Toast.LENGTH_SHORT).show();

                           }
                           else{
                               String error=task.getException().toString();
                               Toast.makeText(SetupActivity.this,error,Toast.LENGTH_SHORT).show();
                           }
                           setupProgess.setVisibility(View.INVISIBLE);
                        }
                    });
                }
            }
        });


    }

    //return the result and sets the image for profile and take uri reference to save in firebase
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
             mainImageUri   = result.getUri();
             setupImage.setImageURI(mainImageUri);
            } else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {
                Exception error = result.getError();
            }
        }
    }
}
