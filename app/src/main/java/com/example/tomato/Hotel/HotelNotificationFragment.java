package com.example.tomato.Hotel;

import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import com.example.tomato.Adapter.NotificationAdapter;
import com.example.tomato.Model.HotelNotification;
import com.example.tomato.R;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

public class HotelNotificationFragment extends Fragment {

        private RecyclerView notif_rv;
        private List<HotelNotification> notificationList;
        private NotificationAdapter notificationAdapter;
        private Button btn;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_hotel_notification, container, false);
        notificationList = new ArrayList<>();
        notificationAdapter = new NotificationAdapter(container.getContext(),notificationList);
        notif_rv = view.findViewById(R.id.notifications_recycler_view);
        notif_rv.setLayoutManager(new LinearLayoutManager(container.getContext()));
        notif_rv.setHasFixedSize(true);
        notif_rv.addItemDecoration(new DividerItemDecoration(container.getContext(),DividerItemDecoration.HORIZONTAL));
        notif_rv.setAdapter(notificationAdapter);
        readNotifs();
        btn = view.findViewById(R.id.notifbtn);
        btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Log.e("CLICK", "NOTIF CLICKKKKK");
            }
        });
        return view;
    }

    private void readNotifs() {
        notificationList.clear();
        FirebaseDatabase.getInstance().getReference().child("Notifications").child(Integer.toString(FirebaseAuth.getInstance().getCurrentUser().getEmail().hashCode())).addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                        HotelNotification notif = dataSnapshot.getValue(HotelNotification.class);
                        notificationList.add(notif);
                    }
                    Collections.reverse(notificationList);
                    notificationAdapter.notifyDataSetChanged();

                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}