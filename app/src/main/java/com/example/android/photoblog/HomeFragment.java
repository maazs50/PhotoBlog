package com.example.android.photoblog;


import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.firestore.DocumentChange;
import com.google.firebase.firestore.EventListener;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.firestore.FirebaseFirestoreException;
import com.google.firebase.firestore.QuerySnapshot;

import java.util.ArrayList;
import java.util.List;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

private RecyclerView blog_list_view;
private List<BlogPost> blogList;
private FirebaseFirestore firebaseFirestore;
private BlogRecyclerAdapter blogRecyclerAdapter;
public HomeFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_home, container, false);


        blogList = new ArrayList<>();
        blog_list_view = view.findViewById(R.id.blog_list_view);


        blogRecyclerAdapter = new BlogRecyclerAdapter(blogList);
        blog_list_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        blog_list_view.setAdapter(blogRecyclerAdapter);

        blog_list_view=view.findViewById(R.id.blog_list_view);
        // Inflate the layout for this fragment
        firebaseFirestore=FirebaseFirestore.getInstance();
        firebaseFirestore.collection("Posts").addSnapshotListener(new EventListener<QuerySnapshot>() {
            @Override
            public void onEvent(QuerySnapshot documentSnapshots, FirebaseFirestoreException e) {
           for (DocumentChange doc:documentSnapshots.getDocumentChanges()){
               if (doc.getType()== DocumentChange.Type.ADDED){
                   BlogPost blogPost=doc.getDocument().toObject(BlogPost.class);
                   blogList.add(blogPost);
                   //forgot this
                    blogRecyclerAdapter.notifyDataSetChanged();
               }
           }
            }
        });
        return view;

    }

}
