package com.example.fragments.Profile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.example.fragments.EditProfileActivity;
import com.example.fragments.LoginActivity;
import com.example.fragments.R;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.StorageTask;

import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import de.hdodenhof.circleimageview.CircleImageView;

public class ProfileFragment extends Fragment {
    Button logout;
    FirebaseAuth mAuth;
    CircleImageView profile_image;
    String profileId;
    FirebaseUser firebaseUser;
    MaterialCardView edit_follow_btn;
    TextView posts, followers, following, name, bio, username, edit_follow_text;
    boolean isSameUser = false;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_profile, container, false);
        logout = view.findViewById(R.id.logout);
        firebaseUser = FirebaseAuth.getInstance().getCurrentUser();
        SharedPreferences prefs = requireActivity().getSharedPreferences("PREFS", Context.MODE_PRIVATE);
        profileId = prefs.getString("profileId", firebaseUser.getUid());
        posts = view.findViewById(R.id.posts);
        followers = view.findViewById(R.id.followers);
        following = view.findViewById(R.id.following);
        edit_follow_text = view.findViewById(R.id.edit_follow_text);
        profile_image = view.findViewById(R.id.profile_image);
        edit_follow_btn = view.findViewById(R.id.edit_follow_btn);
        name = view.findViewById(R.id.name);
        bio = view.findViewById(R.id.bio);
        username = view.findViewById(R.id.username);
        mAuth = FirebaseAuth.getInstance();
        if (profileId.equals(firebaseUser.getUid())){
            isSameUser = true;
            logout.setVisibility(View.VISIBLE);
            edit_follow_text.setText("Edit Profile");
            edit_follow_btn.setCardBackgroundColor(getResources().getColor(R.color.followingCard));
            edit_follow_text.setTextColor(getResources().getColor(R.color.black));
        }else {
            logout.setVisibility(View.GONE);
            getCurrentUsersFollowing();
        }
        logout.setOnClickListener(v -> {
            mAuth.signOut();
            startActivity(new Intent(requireActivity(), LoginActivity.class));
            Objects.requireNonNull(requireActivity()).finish();
        });
        getUserData(profileId);
        edit_follow_btn.setOnClickListener(v -> {
            if(isSameUser) {
                startActivity(new Intent(requireActivity(), EditProfileActivity.class));
            }
        });
        return view;
    }

    private void getCurrentUsersFollowing() {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Follow").child(firebaseUser.getUid()).child("following").child(profileId);
        reference.addValueEventListener(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (snapshot.exists()){
                    edit_follow_text.setText("Following");
                    edit_follow_btn.setCardBackgroundColor(getResources().getColor(R.color.followingCard));
                    edit_follow_text.setTextColor(getResources().getColor(R.color.black));
                }else {
                    edit_follow_text.setText("Follow");
                    edit_follow_btn.setCardBackgroundColor(getResources().getColor(R.color.followCard));
                    edit_follow_text.setTextColor(getResources().getColor(R.color.white));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserData(String profileId) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Users").child(profileId);
        reference.addListenerForSingleValueEvent(new ValueEventListener(){

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    name.setText(Objects.requireNonNull(snapshot.child("fullName").getValue()).toString());
                    username.setText(Objects.requireNonNull(snapshot.child("userName").getValue()).toString());
                    bio.setText(Objects.requireNonNull(snapshot.child("bio").getValue()).toString());
                    String imageUrl = Objects.requireNonNull(snapshot.child("imageUrl").getValue()).toString();
                    if(imageUrl.equals("default")) {
                        profile_image.setImageResource(R.drawable.default_profile4);
                    } else {
                        Glide.with(requireActivity()).load(imageUrl).into(profile_image);
                    }
                    getUserFollowers(profileId);
                    getUserFollowing(profileId);
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserFollowing(String profileId) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Follow").child(profileId).child("following");
        reference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                following.setText(snapshot.getChildrenCount()+"\nFollowing");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    private void getUserFollowers(String profileId) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Follow").child(profileId).child("followers");
        reference.addListenerForSingleValueEvent(new ValueEventListener(){
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                followers.setText(snapshot.getChildrenCount()+"\nFollowers");
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}