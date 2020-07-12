package com.example.tomato;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.RelativeLayout;

import com.example.tomato.Admin.AdminHome;
import com.example.tomato.Hotel.HotelHome;
import com.example.tomato.Users.Home;
import com.firebase.ui.auth.AuthUI;
import com.firebase.ui.auth.IdpResponse;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

public class SignIn extends AppCompatActivity {

    private static final int RC_SIGN_IN = 1;
    private RelativeLayout parent;
    private FirebaseUser user;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);
        parent = findViewById(R.id.parent_view);
        user = FirebaseAuth.getInstance().getCurrentUser();
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.PhoneBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build()
                );

        startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == RC_SIGN_IN) {
            IdpResponse response = IdpResponse.fromResultIntent(data);

            if (resultCode == RESULT_OK) {
                // Successfully signed in
                user = FirebaseAuth.getInstance().getCurrentUser();

                if(response.isNewUser()){
                    createUser();
                }
                else {


                    Log.e("USER EMAILL", user.getEmail());
                    if (user.getEmail().equals("admin@admin.com")) {
                        Snackbar snackbar = Snackbar.make(parent, "Login Success", Snackbar.LENGTH_LONG);
                        snackbar.show();
                        startActivity(new Intent(SignIn.this, AdminHome.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                    }
                    else {
                        FirebaseDatabase.getInstance().getReference().child("Users").child(Integer.toString(user.getEmail().hashCode())).addValueEventListener(new ValueEventListener() {
                            @Override
                            public void onDataChange(@NonNull DataSnapshot snapshot) {
                                if (snapshot.exists()) {
                                    if (snapshot.child("user_type").getValue().equals("customer")) {
                                        startActivity(new Intent(SignIn.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
                                        finish();
                                    } else if (snapshot.child("user_type").getValue().equals("hotel")) {
                                        startActivity(new Intent(SignIn.this, HotelHome.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP));
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


                // ...
            } else {
                // Sign in failed. If response is null the user canceled the
                // sign-in flow using the back button. Otherwise check
                // response.getError().getErrorCode() and handle the error.
                // ...
                Snackbar snackbar = Snackbar.make(parent,"Login Fail", Snackbar.LENGTH_LONG);
                snackbar.show();
            }

        }
    }

    private void createUser() {
        AlertDialog.Builder alertDialog = new AlertDialog.Builder(SignIn.this);
        alertDialog.setTitle("Choose User type");
        final String[] items = {"Restaurant","User"};
        final int[] checkedItem = {1};

        alertDialog.setSingleChoiceItems(items, checkedItem[0], new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                switch (which) {
                    case 0:
                        checkedItem[0] = 0;
                        break;
                    case 1:
                        checkedItem[0] = 1;
                       break;
                }
            }
        }).setNeutralButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                if(checkedItem[0] == 0){
                    HashMap<String,Object> hm = new HashMap<>();
                    hm.put("user_email",user.getEmail());
                    hm.put("user_type", "hotel");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(Integer.toString(user.getEmail().hashCode())).setValue(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar snackbar = Snackbar.make(parent, "Account created successfully", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            startActivity(new Intent(SignIn.this, HotelHome.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar snackbar = Snackbar.make(parent, "Something went wrong. Please try again later.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                            startActivity(new Intent(SignIn.this, Home.class).addFlags(Intent.FLAG_ACTIVITY_CLEAR_TASK|Intent.FLAG_ACTIVITY_CLEAR_TOP));
                            finish();
                        }
                    });

                }
                else{
                    HashMap<String,Object> hm2 = new HashMap<>();
                    hm2.put("user_email",user.getEmail());
                    hm2.put("user_type", "customer");
                    FirebaseDatabase.getInstance().getReference().child("Users").child(Integer.toString(user.getEmail().hashCode())).setValue(hm2).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Snackbar snackbar = Snackbar.make(parent, "Account created successfully", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Snackbar snackbar = Snackbar.make(parent, "Something went wrong. Please try again later.", Snackbar.LENGTH_LONG);
                            snackbar.show();
                        }
                    });

                }
            }
        });
        AlertDialog alert = alertDialog.create();
        alert.setCanceledOnTouchOutside(false);
        alert.show();
    }



}