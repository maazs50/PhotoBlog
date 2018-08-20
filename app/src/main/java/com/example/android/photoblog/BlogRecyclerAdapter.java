package com.example.android.photoblog;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.text.format.DateFormat;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.RequestOptions;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FieldValue;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {
    public List<BlogPost> blogList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blogList){
        this.blogList=blogList;
    }


    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);
        context=parent.getContext();
        firebaseFirestore = FirebaseFirestore.getInstance();
        firebaseAuth = FirebaseAuth.getInstance();
        return new ViewHolder(view);

    }

    @Override
    public void onBindViewHolder(final ViewHolder holder, int position) {
        holder.setIsRecyclable(false);
        final String blogPostId=blogList.get(position).BlogPostId;
        final String currentUserId=firebaseAuth.getCurrentUser().getUid();
        String desc=blogList.get(position).getDesc();
        holder.setDescText(desc);

            final String image_url=blogList.get(position).getImage_url();
            final String thumb_url=blogList.get(position).getImage_thumb();
            holder.setBlogImageView(image_url,thumb_url);

            String user_id = blogList.get(position).getUser_id();
            firebaseFirestore.collection("Users").document(user_id).get().
            addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName=task.getResult().getString("name");
                    String userImage=task.getResult().getString("image");

                        holder.setUserData(userName,userImage);

                } else {
                        String error=task.getException().toString();
                        Toast.makeText(context,error,Toast.LENGTH_SHORT).show();

                }

            }
        });

            long millisecond = blogList.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        //Get likes
            firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).addSnapshotListener(new EventListener<DocumentSnapshot>() {
                @Override
                public void onEvent(DocumentSnapshot documentSnapshot, FirebaseFirestoreException e) {
               if (documentSnapshot.exists()){
                   holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_accent));

               }else{
                   holder.blogLikeBtn.setImageDrawable(context.getDrawable(R.mipmap.action_like_gray));
               }

                }
            });
            //Get likes count
        firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
           if (!documentSnapshots.isEmpty())  {
               int count=documentSnapshots.size();
               holder.updateLikesCount(count);
           }else{
               holder.updateLikesCount(0);
           }
            }
        });

        holder.blogLikeBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
                    @Override
                    public void onComplete(@NonNull Task<DocumentSnapshot> task) {
                   if (!task.getResult().exists()){

                       Map<String,Object> likeMap=new HashMap<>();
                       likeMap.put("timestamp", FieldValue.serverTimestamp());
                       firebaseFirestore.collection("Posts/" + blogPostId + "/Likes").document(currentUserId).set(likeMap);
                   }
                   else{
                       firebaseFirestore.collection("Posts/"+blogPostId+"/Likes").document(currentUserId).delete();
                   }
                    }
                });
            }
        });
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView descView;
        private ImageView blogImageView;
        private View mView;
        private TextView userName;
        private TextView blogDate;
        private TextView blogUserName;
        private CircleImageView blogUserImage;

        private TextView blogLikeCount;
        private ImageView blogLikeBtn;
        public ViewHolder(View itemView) {
            super(itemView);
            mView=itemView;

            blogLikeBtn = mView.findViewById(R.id.blog_like_btn);

        }
        public void setDescText(String text){
            descView=mView.findViewById(R.id.blog_desc);
            descView.setText(text);
        }
        public void setBlogImageView(String downloadUri, String thunbUri){
            blogImageView=mView.findViewById(R.id.blog_image);
            RequestOptions requestOptions=new RequestOptions();
            requestOptions.placeholder(R.drawable.image_placeholder);
            Glide.with(context).applyDefaultRequestOptions(requestOptions).load(downloadUri).
                    thumbnail(Glide.with(context).load(thunbUri)).into(blogImageView);


        }

        public void setUserData(String name, String image){

            blogUserImage = mView.findViewById(R.id.blog_user_image);
            blogUserName = mView.findViewById(R.id.blog_user_name);

            blogUserName.setText(name);

            //included in glide library
            RequestOptions placeholderOption = new RequestOptions();
            placeholderOption.placeholder(R.drawable.profile_placeholder);

            Glide.with(context).applyDefaultRequestOptions(placeholderOption).load(image).into(blogUserImage);

        }

        public void setTime(String time) {
            blogDate=mView.findViewById(R.id.blog_date);
            blogDate.setText(time);
        }
        public void updateLikesCount(int count){
            blogLikeCount=mView.findViewById(R.id.blog_like_count);
            blogLikeCount.setText(count+" Likes");
        }
    }
}
