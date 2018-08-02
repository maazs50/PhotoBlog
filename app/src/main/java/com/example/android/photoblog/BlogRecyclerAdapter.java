package com.example.android.photoblog;

import android.support.annotation.NonNull;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.List;

public class BlogRecyclerAdapter extends RecyclerView.Adapter<BlogRecyclerAdapter.ViewHolder> {
    public List<BlogPost> blogList;

    public BlogRecyclerAdapter(List<BlogPost> blogList){
        this.blogList=blogList;
    }
    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView descView;
        private View mView;
      public ViewHolder(View itemView) {
          super(itemView);
          mView=itemView;

      }
      public void setDescText(String text){
          descView=mView.findViewById(R.id.blog_desc);
          descView.setText(text);
      }
  }
    @NonNull
    @Override
    public BlogRecyclerAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view= LayoutInflater.from(parent.getContext()).inflate(R.layout.blog_list_item,parent,false);

        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull BlogRecyclerAdapter.ViewHolder holder, int position) {
            String desc=blogList.get(position).getDesc();
            holder.setDescText(desc);
    }

    @Override
    public int getItemCount() {
        return blogList.size();
    }
}
