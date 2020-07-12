package com.example.tomato.Admin;

import android.net.LinkAddress;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.example.tomato.Adapter.RequestAdapter;
import com.example.tomato.Model.Request;
import com.example.tomato.R;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.List;

public class RequestsFragment extends Fragment {

    private RecyclerView request_recycler_view;
    private RequestAdapter requestAdapter;
    private List<Request> requestList;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_requests, container, false);
        request_recycler_view = v.findViewById(R.id.admin_requests_recycler_view);
        requestList = new ArrayList<>();
        requestAdapter = new RequestAdapter(getContext(), requestList);

        request_recycler_view.setHasFixedSize(true);
        request_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        request_recycler_view.setAdapter(requestAdapter);

        readList();
        return v;
    }

    private void readList() {
        requestList.clear();
        FirebaseDatabase.getInstance().getReference().child("HotelRequest").addValueEventListener(new ValueEventListener() {

            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                for(DataSnapshot dataSnapshot : snapshot.getChildren()){
                    Request req = dataSnapshot.getValue(Request.class);
                    Log.e("HOTEL NAME", req.getHotel_name());
                    requestList.add(req);
                }
                System.out.println(requestList);
                requestAdapter.notifyDataSetChanged();
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });
    }
}