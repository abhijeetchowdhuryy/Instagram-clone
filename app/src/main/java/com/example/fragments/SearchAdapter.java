package com.example.fragments;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.example.fragments.Profile.ProfileFragment;
import com.google.android.material.card.MaterialCardView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.List;

import de.hdodenhof.circleimageview.CircleImageView;

public class SearchAdapter extends RecyclerView.Adapter<SearchAdapter.ViewHolder> {
    List<Users> usersList;
    FirebaseUser currentUser;

    Context context;

    public SearchAdapter(List<Users> usersList, Context context){

        this.usersList = usersList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext()).inflate(R.layout.users_list, parent, false);
        return new SearchAdapter.ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
        Users users = usersList.get(position);
        holder.user_name.setText(users.getUserName());
        getFollowers(users.getId(), holder);
        holder.followButton.setOnClickListener(v -> {
            if (holder.followText.getText().toString().equals("Follow")){
                followUser(users.getId(), holder);
            }else {
                unfollowUser(users.getId(), holder);
            }
        });
        holder.itemView.setOnClickListener(v -> {
            SharedPreferences sharedPreferences = context.getSharedPreferences("PREFS", Context.MODE_PRIVATE);
            sharedPreferences.edit().putString("profileId", users.getId()).apply();
            ((MainActivity)context).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new ProfileFragment()).commit();
        });
        if(users.getImageUrl().equals("default")) {
            holder.user_image.setImageResource(R.drawable.default_profile4);
        }else{
            Glide.with(context).load(users.getImageUrl()).into(holder.user_image);
        }
    }

    private void unfollowUser(String id, ViewHolder holder) {
        FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Follow").child(currentUser.getUid()).child("following").child(id).removeValue();
        FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Follow").child(id).child("followers").child(currentUser.getUid()).setValue(true);
        getFollowers(id, holder);
    }

    private void followUser(String id, ViewHolder holder) {
        FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Follow").child(currentUser.getUid()).child("following").child(id).setValue(true);
        FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Follow").child(id).child("followers").child(currentUser.getUid()).setValue(true);
        getFollowers(id, holder);
    }

    private void getFollowers(String id, ViewHolder holder) {
        DatabaseReference reference = FirebaseDatabase.getInstance("https://fragments-a4d22-default-rtdb.asia-southeast1.firebasedatabase.app/").getReference("Follow").child(currentUser.getUid()).child("following").child(id);
        reference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    holder.followText.setText("Following");
                    holder.followButton.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.followingCard));
                    holder.followText.setTextColor(holder.itemView.getResources().getColor(R.color.black));
                }else{
                    holder.followText.setText("Follow");
                    holder.followButton.setCardBackgroundColor(holder.itemView.getResources().getColor(R.color.followCard));
                    holder.followText.setTextColor(holder.itemView.getResources().getColor(R.color.white));
                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }

    @Override
    public int getItemCount() {
        return usersList.size();
    }

    static class ViewHolder extends RecyclerView.ViewHolder{
        TextView user_name, followText;
        MaterialCardView followButton;
        CircleImageView user_image;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            user_name = itemView.findViewById(R.id.user_name);
            followText = itemView.findViewById(R.id.followText);
            followButton = itemView.findViewById(R.id.followButton);
            user_image = itemView.findViewById(R.id.user_image);
        }
    }
}
