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
import com.google.firebase.firestore.FirebaseFirestore;

import java.util.Date;
import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {
    public List<BlogPost> blogList;
    public Context context;
    private FirebaseFirestore firebaseFirestore;
    private FirebaseAuth firebaseAuth;

    public BlogRecyclerAdapter(List<BlogPost> blogList){
        this.blogList=blogList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView descView;
        private ImageView blogImageView;
        private View mView;
        private TextView userName;
        private TextView blogDate;
        private TextView blogUserName;
        private CircleImageView blogUserImage;
      public ViewHolder(View itemView) {
          super(itemView);
          mView=itemView;

      }
      public void setDescText(String text){
          descView=mView.findViewById(R.id.blog_desc);
          descView.setText(text);
      }
      public void setBlogImageView(String downloadUri){
          blogImageView=mView.findViewById(R.id.blog_image);
          Glide.with(context).load(downloadUri).into(blogImageView);

      }
      public void setUserName(String text){
          userName=mView.findViewById(R.id.blog_user_name);
          //userName.setText(text);


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
            String desc=blogList.get(position).getDesc();
            holder.setDescText(desc);

            final String image_url=blogList.get(position).getImage_url();
            holder.setBlogImageView(image_url);

        String user_id = blogList.get(position).getUser_id();
        firebaseFirestore.collection("Users").document(user_id).get().addOnCompleteListener(new OnCompleteListener<DocumentSnapshot>() {
            @Override
            public void onComplete(@NonNull Task<DocumentSnapshot> task) {

                if(task.isSuccessful()){

                    String userName=task.getResult().getString("name");
                    String userImage=task.getResult().getString("image");

                        holder.setUserData(userName,userImage);

                } else {


                }

            }
        });
        try {
            long millisecond = blogList.get(position).getTimestamp().getTime();
            String dateString = DateFormat.format("MM/dd/yyyy", new Date(millisecond)).toString();
            holder.setTime(dateString);
        } catch (Exception e) {

            Toast.makeText(context, "Exception : " + e.getMessage(), Toast.LENGTH_SHORT).show();

        }
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }
}
