package com.example.tomato.Hotel;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.Fragment;

import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;
import android.widget.Toast;

import com.example.tomato.MainActivity;
import com.example.tomato.R;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

public class HotelHome extends AppCompatActivity {
    BottomNavigationView bottomnav;
    Fragment fragment;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_hotel_home);
        bottomnav = findViewById(R.id.hotel_bottom_nav);
        user =FirebaseAuth.getInstance().getCurrentUser();


        bottomnav.setOnNavigationItemSelectedListener(new BottomNavigationView.OnNavigationItemSelectedListener() {
            @Override
            public boolean onNavigationItemSelected(@NonNull MenuItem item) {
                switch(item.getItemId()){
                    case R.id.view : fragment = new HotelViewFragment(); break;
                    case R.id.orders : fragment = new OrdersFragment(); break;
                    case R.id.send_request : fragment = new SendRequestFragment(); break;
                    case R.id.notifications : fragment = new HotelNotificationFragment(); break;

                    case R.id.logout : FirebaseAuth.getInstance().signOut();
                        Toast.makeText(HotelHome.this, "Logged Out.", Toast.LENGTH_SHORT).show();
                        startActivity(new Intent(getApplicationContext(), MainActivity.class));
                        finish();
                }
                if(fragment != null) {
                    getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, fragment).commit();
                }
                return true;
            }
        });
        if(fragment == null){
            getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrdersFragment()).commit();
            bottomnav.setSelectedItemId(R.id.orders);
        }


    }
}