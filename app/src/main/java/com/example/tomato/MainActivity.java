package com.example.tomato;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.example.tomato.Admin.AdminHome;
import com.example.tomato.Hotel.HotelHome;
import com.example.tomato.Users.Home;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class MainActivity extends AppCompatActivity {

    private TextView heading;
    private TextView caption;

    private ImageView tomato;
    private View screen;
    private FirebaseUser user;
    private String user_type;




    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        tomato = findViewById(R.id.tomato);
        heading = findViewById(R.id.heading);
        caption = findViewById(R.id.caption);

        screen = findViewById(R.id.screen);
        user = FirebaseAuth.getInstance().getCurrentUser();

        if(user != null) {

        }



        screen.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startActivity(new Intent(MainActivity.this, SignIn.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK));
                finish();
            }
        });

        heading.animate().alpha(1f).translationX(200f).setDuration(1000);
        caption.animate().alpha(1f).setDuration(5000);
        tomato.animate().rotationBy(1080f).translationY(310f).setDuration(2000);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if(user != null) {
            if (user.getEmail().equals("admin@admin.com")) {

                startActivity(new Intent(MainActivity.this, AdminHome.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
            }

            else{
                FirebaseDatabase.getInstance().getReference().child("Users").child(Integer.toString(user.getEmail().hashCode())).addValueEventListener(new ValueEventListener() {
                    @Override
                    public void onDataChange(@NonNull DataSnapshot snapshot) {
                        if (snapshot.exists()) {
                            if (snapshot.child("user_type").getValue().equals("customer")) {
                                startActivity(new Intent(MainActivity.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();

                            } else if (snapshot.child("user_type").getValue().equals("hotel")) {
                                startActivity(new Intent(MainActivity.this,HotelHome.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                finish();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(@NonNull DatabaseError error) {

                    }
                });


        }

    }

    }}




