package com.example.android.photoblog;

import android.app.Activity;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.View;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.HashMap;
import java.util.Map;

public class CommentsActivity extends Activity {
    private EditText comment_field;
    private ImageView comment_post_btn;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;
    private String blog_post_id;
    private String current_user_id;
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_comments);
        comment_field=findViewById(R.id.comment_field);
        comment_post_btn=findViewById(R.id.comment_post_btn);

        firebaseAuth=FirebaseAuth.getInstance();
        firebaseFirestore=FirebaseFirestore.getInstance();

        current_user_id=firebaseAuth.getCurrentUser().getUid();
        blog_post_id=getIntent().getStringExtra("blog_post_id");


        comment_post_btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String commentMessage=comment_field.getText().toString();
                if (!commentMessage.isEmpty()){
                    Map<String,Object> commentMap=new HashMap<>();
                    commentMap.put("Message",commentMessage);
                    commentMap.put("user_id",current_user_id);
                    commentMap.put("timestamp", FieldValue.serverTimestamp());
                    firebaseFirestore.collection("Posts/"+blog_post_id+"/Comments").add(commentMap).addOnCompleteListener(new OnCompleteListener<DocumentReference>() {
                        @Override
                        public void onComplete(@NonNull Task<DocumentReference> task) {
                       if (!task.isSuccessful()){
                           Toast.makeText(CommentsActivity.this,"Comment not posted",Toast.LENGTH_SHORT).show();
                       }else{
                           Toast.makeText(CommentsActivity.this,"Comment posted",Toast.LENGTH_SHORT).show();

                           comment_field.setText("");
                       }
                        }
                    });

                }
            }
        });
    }

}
