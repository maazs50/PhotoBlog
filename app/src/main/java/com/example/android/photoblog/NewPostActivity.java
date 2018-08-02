package com.example.android.photoblog;

import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.theartofdev.edmodo.cropper.CropImage;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Random;
import java.util.UUID;

import id.zelory.compressor.Compressor;

public class NewPostActivity extends AppCompatActivity {
    Toolbar newPostToolbar;
    private ImageView newPostImage;
    private static final int MAX_LENGTH=100;
    private EditText newPostDesc;
    private Button newPostBtn;
    private Bitmap compressedImageFile;
    private Uri postImageUri;
    private ProgressBar newPostProgress;
    private FirebaseFirestore firebaseFirestore;
    private StorageReference storageReference;
    private FirebaseAuth mAuth;
    String current_user_id;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_post);
        newPostToolbar=findViewById(R.id.new_post_toolbar);
        setSupportActionBar(newPostToolbar);
        getSupportActionBar().setTitle("Add a new Post");
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        newPostImage = findViewById(R.id.new_post_image);
        newPostDesc = findViewById(R.id.new_post_desc);
        newPostBtn = findViewById(R.id.post_btn);
        newPostProgress=findViewById(R.id.new_post_progress);
        storageReference = FirebaseStorage.getInstance().getReference();
        firebaseFirestore=FirebaseFirestore.getInstance();
        mAuth=FirebaseAuth.getInstance();
        current_user_id=mAuth.getCurrentUser().getUid();
        newPostImage.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                CropImage.activity()
                        .setGuidelines(CropImageView.Guidelines.ON)
                        .setMinCropResultSize(512,512)
                        .setAspectRatio(1, 1)
                        .start(NewPostActivity.this);
            }
        });
        newPostBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String desc=newPostDesc.getText().toString();
                if (!TextUtils.isEmpty(desc)&&newPostImage!=null){

                    newPostProgress.setVisibility(View.VISIBLE);
                    final String randonName= UUID.randomUUID().toString();
                    StorageReference filePath=storageReference.child("post_images").child(randonName+".jpg");
                    filePath.putFile(postImageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                        @Override
                        public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                       if (task.isSuccessful()){
                           File newImageFile=new File(postImageUri.getPath());
                           try {
                               compressedImageFile = new Compressor(NewPostActivity.this).compressToBitmap(newImageFile);

                           }
                           catch (IOException e){
                                e.printStackTrace();
                           }
                           ByteArrayOutputStream baos = new ByteArrayOutputStream();
                           compressedImageFile.compress(Bitmap.CompressFormat.JPEG, 100, baos);
                           byte[] thumbData = baos.toByteArray();
                           UploadTask uploadTask=storageReference.child("post_images/thumbs").child(randonName+".jpg").putBytes(thumbData);
                           uploadTask.addOnSuccessListener(new OnSuccessListener<UploadTask.TaskSnapshot>() {
                               @Override
                               public void onSuccess(UploadTask.TaskSnapshot taskSnapshot) {
                                   String downloadUri=taskSnapshot.getDownloadUrl().toString();
                                   String downloadthumbUri = taskSnapshot.getDownloadUrl().toString();

                                   Map<String, Object> postMap=new HashMap<>();
                                   postMap.put("image_url",downloadUri);
                                   postMap.put("desc",desc);
                                   postMap.put("image_thumb", downloadthumbUri);

                                   postMap.put("user_id",current_user_id);
                                   postMap.put("timestamp",FieldValue.serverTimestamp());
                                   firebaseFirestore.collection("Posts").add(postMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                                       @Override
                                       public void onComplete(@NonNull Task<DocumentReference> task) {
                                           if (task.isSuccessful()){
                                               Toast.makeText(NewPostActivity.this,"Post was added",Toast.LENGTH_SHORT).show();
                                               sendToMain();
                                           }else{
                                               String error=task.getException().toString();
                                               Toast.makeText(NewPostActivity.this,error,Toast.LENGTH_SHORT).show();

                                           }
                                           newPostProgress.setVisibility(View.INVISIBLE);
                               }
                           });




                               }
                           }).addOnFailureListener(new OnFailureListener() {
                               @Override
                               public void onFailure(@NonNull Exception e) {
                                   String error=e.toString();
                                   Toast.makeText(NewPostActivity.this,error,Toast.LENGTH_SHORT).show();
                               }
                           });
                       }
                       else{
                           newPostProgress.setVisibility(View.INVISIBLE);
                       }
                        }
                    });
                }
            }

        });
    }
    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == CropImage.CROP_IMAGE_ACTIVITY_REQUEST_CODE) {
            CropImage.ActivityResult result = CropImage.getActivityResult(data);
            if (resultCode == RESULT_OK) {
                postImageUri=result.getUri();
                newPostImage.setImageURI(postImageUri);

            }
            else if (resultCode == CropImage.CROP_IMAGE_ACTIVITY_RESULT_ERROR_CODE) {

                Exception error = result.getError();

            }
        }

    }

    private void sendToMain(){
        Intent mainIntent=new Intent(NewPostActivity.this,MainActivity.class);
        startActivity(mainIntent);
        finish();
    }
    public static String random() {
        Random generator = new Random();
        StringBuilder randomStringBuilder = new StringBuilder();
        int randomLength = generator.nextInt(MAX_LENGTH);
        char tempChar;
        for (int i = 0; i < randomLength; i++){
            tempChar = (char) (generator.nextInt(96) + 32);
            randomStringBuilder.append(tempChar);
        }
        return randomStringBuilder.toString();
    }
}
