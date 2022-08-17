package com.devops.collectomania;

import android.Manifest;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.view.View;
import android.view.Menu;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.material.navigation.NavigationView;

import androidx.annotation.NonNull;
import androidx.core.app.ActivityCompat;
import androidx.navigation.NavController;
import androidx.navigation.Navigation;
import androidx.navigation.ui.AppBarConfiguration;
import androidx.navigation.ui.NavigationUI;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.appcompat.app.AppCompatActivity;

import com.devops.collectomania.databinding.ActivityMainMenuBinding;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

public class MainMenu extends AppCompatActivity {

    private AppBarConfiguration mAppBarConfiguration;
    private ActivityMainMenuBinding binding;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityMainMenuBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setSupportActionBar(binding.appBarMainMenu.toolbar);
        DrawerLayout drawer = binding.drawerLayout;
        NavigationView navigationView = binding.navView;
        // Passing each menu ID as a set of Ids because each
        // menu should be considered as top level destinations.
        mAppBarConfiguration = new AppBarConfiguration.Builder(
                R.id.nav_home, R.id.nav_profile, R.id.nav_myProgress,R.id.nav_logout,R.id.nav_addItem,R.id.nav_myProgress,R.id.nav_myCollection)
                .setOpenableLayout(drawer)
                .build();
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_menu);
        NavigationUI.setupActionBarWithNavController(this, navController, mAppBarConfiguration);
        NavigationUI.setupWithNavController(navigationView, navController);

        if (ActivityCompat.checkSelfPermission(MainMenu.this, Manifest.permission.READ_EXTERNAL_STORAGE) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(MainMenu.this, new String[]{Manifest.permission.READ_EXTERNAL_STORAGE}, 1112);

        }

        //Calling method created
        UpdateNavHeader();

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main_menu, menu);
        return true;
    }

    @Override
    public boolean onSupportNavigateUp() {
        NavController navController = Navigation.findNavController(this, R.id.nav_host_fragment_content_main_menu);
        return NavigationUI.navigateUp(navController, mAppBarConfiguration)
                || super.onSupportNavigateUp();
    }

    //Ui update for profile info
    public void UpdateNavHeader(){

        try{
            NavigationView navigationView = binding.navView;
            View headerView = navigationView.getHeaderView(0);
            TextView navUsername = headerView.findViewById(R.id.txtUserName);
            TextView navUserMail = headerView.findViewById(R.id.txtEmail);
            ImageView navUserPhoto = headerView.findViewById(R.id.imgProfilePic);

            TempUser.userDeets.clear();
            TempUser.Fullname = null;

            DatabaseReference reference = FirebaseDatabase.getInstance().getReference().child("User Profile").child(TempUser.encodedEmail());

            reference.addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {

                    for (DataSnapshot snapshot1: snapshot.getChildren()){
                        TempUser.userDeets.add(snapshot1.getValue().toString());
                    }

                    if(TempUser.userDeets.size()>=3){
                        TempUser.Fullname = TempUser.userDeets.get(1)+" "+TempUser.userDeets.get(0);
                        Picasso.with(MainMenu.this).load(TempUser.userDeets.get(3)).into(navUserPhoto);

                        navUsername.setText(TempUser.userDeets.get(1)+" "+TempUser.userDeets.get(0));

                        Toast.makeText(MainMenu.this, TempUser.Fullname, Toast.LENGTH_SHORT).show();

                    }
                    else {
                        navUserPhoto.setImageResource(R.drawable.ic_baseline_person_outline_24);
                    }

                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {
                    TempUser.Fullname = "User database unread";
                }
            });

            String Email = TempUser.Email;
            navUserMail.setText(Email);

        }catch (Exception e){
            Toast.makeText(MainMenu.this, "Update Profile Info", Toast.LENGTH_SHORT).show();
        }

    }

}