package com.example.mp_team3;

import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.widget.AppCompatButton;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentTransaction;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

import com.bumptech.glide.Glide;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.firestore.DocumentReference;
import com.google.firebase.firestore.DocumentSnapshot;
import com.google.firebase.firestore.FirebaseFirestore;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.Collections;

public class Fragment_home extends Fragment {
    FloatingActionButton createPost;
    RecyclerView homeRecycler;
    ProductAdapter adapter;
    ArrayList<PostModel> pList;
    View view;
    FirebaseDatabase db;
    DatabaseReference dbRef;
    RecyclerView.LayoutManager layoutManager;
    AppCompatButton btnGotoSearch;
    ImageButton btnMenu;
    SwipeRefreshLayout homeSwipe;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        view = inflater.inflate(R.layout.fragment_home,container,false);
        createPost = (FloatingActionButton) view.findViewById(R.id.fabCreatePost);
        btnGotoSearch = (AppCompatButton) view.findViewById(R.id.btnGotoSearch);

        // FloatingActionButton 누르면 PostActivity로 이동
        createPost.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PostActivity.class);
                intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
                startActivity(intent);
            }
        });

        // 검색창 누르면 검색 페이지로 이동
        btnGotoSearch.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), Search.class);
                startActivity(intent);
            }
        });


        homeRecycler = (RecyclerView) view.findViewById(R.id.homeRecycler);
        homeRecycler.setHasFixedSize(true);
        layoutManager = new LinearLayoutManager(getContext());
        homeRecycler.setLayoutManager(layoutManager);
        pList = new ArrayList<>();
        //iList = new ArrayList<>();

        db = FirebaseDatabase.getInstance();
        dbRef = db.getReference("posts");
        dbRef.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                pList.clear();
                //iList.clear();
                for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
                    PostModel postModel = dataSnapshot.getValue(PostModel.class);
                    pList.add(postModel);
                    Collections.sort(pList);

                }
                adapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });

        adapter = new ProductAdapter(pList, getContext());
        homeRecycler.setAdapter(adapter);

        homeSwipe = (SwipeRefreshLayout) view.findViewById(R.id.homeSwipe);
        homeSwipe.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                FragmentTransaction ft = getParentFragmentManager().beginTransaction();
                ft.commit();
                final Handler handler = new Handler();
                handler.postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        homeSwipe.setRefreshing(false);
                    }
                }, 500);
            }
        });

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        FragmentTransaction ft = getParentFragmentManager().beginTransaction();
        ft.detach(this).attach(this).commit();
    }
}