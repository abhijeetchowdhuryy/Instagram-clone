package com.example.fragments;

import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.os.Bundle;

import com.example.fragments.Home.HomeFragment;
import com.example.fragments.Profile.ProfileFragment;
import com.example.fragments.Reels.ReelsFragment;
import com.example.fragments.Search.SearchFragment;
import com.google.android.material.checkbox.MaterialCheckBox;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

public class MainActivity extends AppCompatActivity {

    MaterialCheckBox home, search, reels,  profile;

    Fragment selectedFragment = null;

    private FirebaseAuth mAuth;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        mAuth = FirebaseAuth.getInstance();
        home = findViewById(R.id.home);
        search = findViewById(R.id.search);
        reels = findViewById(R.id.reels);
        profile = findViewById(R.id.profile);

        home.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                selectedFragment = new HomeFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                search.setChecked(false);
                reels.setChecked(false);
                profile.setChecked(false);

                // Create translation animations
                ObjectAnimator translationUp = ObjectAnimator.ofFloat(home, "translationY", 0f, -40f);
                translationUp.setDuration(200);

                ObjectAnimator translationDown = ObjectAnimator.ofFloat(home, "translationY", -40f, 0f);
                translationDown.setDuration(200);

                translationDown.setStartDelay(200);

                // Create an AnimatorSet to play the animations sequentially
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(translationUp).before(translationDown);

                // Start the animation
                animatorSet.start();
            }
            else if (selectedFragment instanceof HomeFragment)
                home.setChecked(true);
        });
        search.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                selectedFragment = new SearchFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                home.setChecked(false);
                reels.setChecked(false);
                profile.setChecked(false);


                // Create translation animations
                ObjectAnimator translationUp = ObjectAnimator.ofFloat(search, "translationY", 0f, -40f);
                translationUp.setDuration(200);

                ObjectAnimator translationDown = ObjectAnimator.ofFloat(search, "translationY", -40f, 0f);
                translationDown.setDuration(200);

                translationDown.setStartDelay(200);

                // Create an AnimatorSet to play the animations sequentially
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(translationUp).before(translationDown);

                // Start the animation
                animatorSet.start();
            }
            else if (selectedFragment instanceof SearchFragment)
                search.setChecked(true);
        });

        reels.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                selectedFragment = new ReelsFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                home.setChecked(false);
                search.setChecked(false);
                profile.setChecked(false);


                // Create translation animations
                ObjectAnimator translationUp = ObjectAnimator.ofFloat(reels, "translationY", 0f, -40f);
                translationUp.setDuration(200);

                ObjectAnimator translationDown = ObjectAnimator.ofFloat(reels, "translationY", -40f, 0f);
                translationDown.setDuration(200);

                translationDown.setStartDelay(200);

                // Create an AnimatorSet to play the animations sequentially
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(translationUp).before(translationDown);

                // Start the animation
                animatorSet.start();
            }
            else if (selectedFragment instanceof ReelsFragment)
                reels.setChecked(true);
        });

        profile.setOnCheckedChangeListener((buttonView, isChecked) -> {
            if(isChecked){
                FirebaseAuth mAuth = FirebaseAuth.getInstance();
                if (mAuth.getCurrentUser() != null) {
                    SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
                    sharedPreferences.edit().putString("profileId", mAuth.getCurrentUser().getUid()).apply();
                }
                selectedFragment = new ProfileFragment();
                getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, selectedFragment).commit();
                home.setChecked(false);
                search.setChecked(false);
                reels.setChecked(false);
                // Create translation animations
                ObjectAnimator translationUp = ObjectAnimator.ofFloat(profile, "translationY", 0f, -40f);
                translationUp.setDuration(200);

                ObjectAnimator translationDown = ObjectAnimator.ofFloat(profile, "translationY", -40f, 0f);
                translationDown.setDuration(200);

                translationDown.setStartDelay(200);

                // Create an AnimatorSet to play the animations sequentially
                AnimatorSet animatorSet = new AnimatorSet();
                animatorSet.play(translationUp).before(translationDown);

                // Start the animation
                animatorSet.start();
            }
            else if (selectedFragment instanceof ProfileFragment){
                profile.setChecked(true);
            }
        });
    }
    public void onStart() {
        super.onStart();
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if(currentUser==null)
        {
            startActivity(new Intent(MainActivity.this,LoginActivity.class));
            finish();
        } else {
            SharedPreferences sharedPreferences = getSharedPreferences("PREFS", MODE_PRIVATE);
            sharedPreferences.edit().putString("profileId", currentUser.getUid()).apply();
        }
    }
}







