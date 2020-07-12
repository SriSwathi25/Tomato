package com.example.tomato.Admin;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.example.tomato.Adapter.MenuAdapter;
import com.example.tomato.Model.Menu;
import com.example.tomato.Model.Request;
import com.example.tomato.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;

public class RequestDetailFragment extends Fragment {
    public String request_id;
    private Request request;
    private TextView hotel_name;
    private TextView hotel_phone;
    private TextView hotel_email;
    private TextView hotel_address;
    private TextView hotel_pin;

    private RecyclerView menu_recycler_view;
    private MenuAdapter menuAdapter;
    private List<Menu> menuList;

    private Button approve;
    private Button reject;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_request_detail, container, false);
        request_id = getContext().getSharedPreferences("REQUESTLIST", Context.MODE_PRIVATE).getString("request_id","none");

        hotel_name = v.findViewById(R.id.hotel_name);
        hotel_phone = v.findViewById(R.id.hotel_phone);
        hotel_email = v.findViewById(R.id.hotel_email);
        hotel_address = v.findViewById(R.id.hotel_address);
        hotel_pin = v.findViewById(R.id.hotel_pin);

        approve = v.findViewById(R.id.approve);
        reject = v.findViewById(R.id.reject);

        menuList = new ArrayList<>();
        menu_recycler_view = v.findViewById(R.id.menu_list_recycler_view);
        menuAdapter = new MenuAdapter(container.getContext(), menuList);
        menu_recycler_view.setHasFixedSize(true);
        menu_recycler_view.setLayoutManager(new LinearLayoutManager(container.getContext()));
        menu_recycler_view.setAdapter(menuAdapter);

            FirebaseDatabase.getInstance().getReference().child("HotelRequest").child(request_id).addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    request = snapshot.getValue(Request.class);
                    if(request!=null) {
                        readMenu();
                        hotel_name.setText(request.getHotel_name());
                        hotel_address.setText(request.getHotel_address());
                        hotel_email.setText(request.getHotel_email());
                        hotel_pin.setText(request.getHotel_pincode());
                        hotel_phone.setText(request.getHotel_phone());
                    }
                    else{
                        Toast.makeText(getActivity(), "Something went wrong", Toast.LENGTH_SHORT).show();
                    }
                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });

            approve.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if(request!=null) {
                        new AlertDialog.Builder(getActivity())
                                .setTitle("Are you sure you want to approve ?")
                                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        FirebaseDatabase.getInstance().getReference().child("PendingList").child(request.getUser_id()).setValue("false");
                                        FirebaseDatabase.getInstance().getReference().child("Hotel").child(request.getUser_id()).setValue(request).addOnCompleteListener(new OnCompleteListener<Void>() {
                                            @Override
                                            public void onComplete(@NonNull Task<Void> task) {
                                                if (task.isSuccessful()) {
                                                    Toast.makeText(getActivity(), "Request Approved", Toast.LENGTH_SHORT).show();
                                                    HashMap<String, Object> hm = new HashMap<>();
                                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm");
                                                    String currentDateandTime = sdf.format(new Date());
                                                    hm.put("time", currentDateandTime);
                                                    hm.put("Message", "Request Approved");
                                                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(request.getUser_id()).child(Long.toString(System.currentTimeMillis())).setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                        @Override
                                                        public void onComplete(@NonNull Task<Void> task) {
                                                            if (task.isSuccessful()) {
                                                                Log.e("REQUEST APPROVE", "REQUEST APPROVED. MESSAGE SENT");
                                                            }
                                                        }
                                                    });
                                                    FirebaseDatabase.getInstance().getReference().child("HotelRequest").child(request_id).removeValue();
                                                    if(getActivity() != null)
                                                    getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RequestsFragment()).commit();
                                                }

                                            }
                                        });

                                    }
                                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        }).show();
                    }
                    else{
                        Toast.makeText(getActivity(), "Request error", Toast.LENGTH_SHORT).show();
                    }

                }
            });
            reject.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    new AlertDialog.Builder(getActivity())
                            .setTitle("Are you sure you want to reject ?")
                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Toast.makeText(getActivity(), "Request Rejected", Toast.LENGTH_SHORT).show();
                                    HashMap<String, Object> hm = new HashMap<>();
                                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd 'at' HH:mm");
                                    String currentDateandTime = sdf.format(new Date());
                                    hm.put("time", currentDateandTime);
                                    hm.put("Message", "Request Rejected. Please contact 1234567890");
                                    FirebaseDatabase.getInstance().getReference().child("Notifications").child(request.getUser_id()).child(Long.toString(System.currentTimeMillis())).setValue(hm).addOnCompleteListener(new OnCompleteListener<Void>() {
                                        @Override
                                        public void onComplete(@NonNull Task<Void> task) {
                                            if (task.isSuccessful()) {
                                                Log.e("REQUEST REJECT", "REQUEST REJECTED. MESSAGE SENT");
                                            }
                                        }
                                    });
                                    FirebaseDatabase.getInstance().getReference().child("HotelRequest").child(request_id).removeValue();
                                    if (getActivity() != null)
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new RequestsFragment()).commit();

                                }
                            }).show();
                }
            });


        return v;
    }

    private void readMenu() {
        String menu_id = request.getMenu_id();
        menuList.clear();
        FirebaseDatabase.getInstance().getReference().child("Menu").child(menu_id).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    menuList.add(dataSnapshot.getValue(Menu.class));
                }
                menuAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}