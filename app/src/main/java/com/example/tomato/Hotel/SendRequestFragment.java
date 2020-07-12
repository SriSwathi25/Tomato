package com.example.tomato.Hotel;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;
import androidx.core.widget.NestedScrollView;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.ItemTouchHelper;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tomato.Adapter.MenuAdapter;
import com.example.tomato.Model.Menu;
import com.example.tomato.R;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.material.bottomnavigation.BottomNavigationView;
import com.google.android.material.snackbar.BaseTransientBottomBar;
import com.google.android.material.snackbar.Snackbar;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import static androidx.core.content.ContextCompat.getSystemService;

public class SendRequestFragment extends Fragment {

    private EditText hotel_name;
    private EditText hotel_address;
    private EditText hotel_pincode;

    private EditText hotel_phone;
    private EditText item_name_txt;
    private EditText item_price_txt;

    private RecyclerView menu_recycler_view;
    private List<Menu> menuList;
    private MenuAdapter menuAdapter;

    private Button add;
    private Button cancel;
    private Button send_request;

    private FirebaseUser user;

    private NestedScrollView parent_view;
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_send_request, container, false);

        user =FirebaseAuth.getInstance().getCurrentUser();


        add = v.findViewById(R.id.add_item);

        parent_view = v.findViewById(R.id.parent_scroll);

        hotel_address = v.findViewById(R.id.hotel_address);

        hotel_name = v.findViewById(R.id.hotel_name);
        hotel_pincode = v.findViewById(R.id.hotel_pincode);
        hotel_phone = v.findViewById(R.id.hotel_phone);

        cancel = v.findViewById(R.id.cancel_item);
        send_request = v.findViewById(R.id.send_request);


        item_name_txt = v.findViewById(R.id.item_name);
        item_price_txt = v.findViewById(R.id.item_price);
        menuList = new ArrayList<>();
        menu_recycler_view = v.findViewById(R.id.menu_recycler_view);
        menuAdapter = new MenuAdapter(getContext(),menuList);
        menu_recycler_view.setLayoutManager(new LinearLayoutManager(getContext()));
        menu_recycler_view.addItemDecoration(new DividerItemDecoration(getContext(),DividerItemDecoration.VERTICAL));
        menu_recycler_view.setHasFixedSize(true);
        menu_recycler_view.setAdapter(menuAdapter);

        ItemTouchHelper.SimpleCallback touchHelperCallBack = new ItemTouchHelper.SimpleCallback(0,ItemTouchHelper.LEFT) {
            private final ColorDrawable background = new ColorDrawable(getResources().getColor(R.color.backgorund));

            @Override
            public boolean onMove(@NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, @NonNull RecyclerView.ViewHolder target) {
                Toast.makeText(getContext(), "Swipe to remove this item", Toast.LENGTH_SHORT).show();
                return true;
            }

            @Override
            public void onSwiped(@NonNull RecyclerView.ViewHolder viewHolder, int direction) {
                menuList.remove(viewHolder.getPosition());
                menuAdapter.notifyDataSetChanged();
                Snackbar snackbar = Snackbar.make(parent_view,"Item removed.", BaseTransientBottomBar.LENGTH_LONG);
                snackbar.show();

            }

            @Override
            public void onChildDraw(@NonNull Canvas c, @NonNull RecyclerView recyclerView, @NonNull RecyclerView.ViewHolder viewHolder, float dX, float dY, int actionState, boolean isCurrentlyActive) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive);
                View itemView = viewHolder.itemView;

                if (dX > 0) {
                    background.setBounds(itemView.getLeft(), itemView.getTop(), itemView.getLeft() + ((int) dX), itemView.getBottom());
                } else if (dX < 0) {
                    background.setBounds(itemView.getRight() + ((int) dX), itemView.getTop(), itemView.getRight(), itemView.getBottom());
                } else {
                    background.setBounds(0, 0, 0, 0);
                }

                background.draw(c);
            }
        };
        ItemTouchHelper helper = new ItemTouchHelper(touchHelperCallBack);
        helper.attachToRecyclerView(menu_recycler_view);

        add.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Menu menu = new Menu();
                menu.setItemName(item_name_txt.getText().toString());
                menu.setItemPrice(item_price_txt.getText().toString());
                menuList.add(menu);
                menuAdapter.notifyDataSetChanged();
                item_name_txt.setText("");
                item_price_txt.setText("");
                //closeKeyboard();


            }
        });
        send_request.setOnClickListener(new View.OnClickListener() {
                                            @Override
                                            public void onClick(View v) {
                                                if(TextUtils.isEmpty(hotel_name.getText()) || TextUtils.isEmpty(hotel_phone.getText()) || TextUtils.isEmpty(hotel_pincode.getText()) || TextUtils.isEmpty(hotel_address.getText())  ){
                                                    Snackbar snackbar = Snackbar.make(parent_view,"Fill all fields to proceed", BaseTransientBottomBar.LENGTH_LONG);
                                                    snackbar.show();
                                                }
                                                else
                                                {
                                                    new AlertDialog.Builder(getContext()).
                                                            setTitle("Are you sure you want to send this information for approval ?")
                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    sendHotelRequest(hotel_name.getText().toString(), hotel_phone.getText().toString(), hotel_pincode.getText().toString(), hotel_address.getText().toString(), menuList);

                                                                }
                                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).show();
                                                }

                                            }
                                        }

        );
        return v;
    }
    private void sendHotelRequest(final String name, final String phone, final String pin, final String address,  List<Menu> menuList) {
        final String request_id = FirebaseDatabase.getInstance().getReference().child("HotelRequest").push().getKey();
        final String menu_id = FirebaseDatabase.getInstance().getReference().child("Menu").push().getKey();
        FirebaseDatabase.getInstance().getReference().child("PendingList").child(Integer.toString(user.getEmail().hashCode())).setValue("true");
        for(Menu item:menuList){
            FirebaseDatabase.getInstance().getReference().child("Menu").child(menu_id).push().setValue(item).addOnSuccessListener(new OnSuccessListener<Void>() {
                @Override
                public void onSuccess(Void aVoid) {
                    Log.e("SUCCESS", "Success adding items to menu");
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception e) {
                    Log.e("FAILURE", "SFailure adding items to menu");
                }
            });
        }
        FirebaseDatabase.getInstance().getReference().child("Menu").child(menu_id).addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if(snapshot.exists()){
                    HashMap<String,Object> hm = new HashMap<>();
                    hm.put("user_id", Integer.toString(user.getEmail().hashCode()));
                    hm.put("hotel_name",name);
                    hm.put("hotel_address",address);
                    hm.put("hotel_phone",phone);
                    hm.put("hotel_pincode",pin);
                    hm.put("hotel_email",user.getEmail());
                    hm.put("request_id",request_id);
                    hm.put("menu_id", menu_id);


                    FirebaseDatabase.getInstance().getReference().child("HotelRequest").child(request_id).setValue(hm).addOnSuccessListener(new OnSuccessListener<Void>() {
                        @Override
                        public void onSuccess(Void aVoid) {
                            Log.e("SUCCESS", "Success sending Request");
                            new AlertDialog.Builder(getContext()).
                                    setTitle("Request sent successfully. We will mail our feedback within 24 hours.").setNeutralButton("OK", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    if(getActivity() != null) {
                                        getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrdersFragment()).commit();
                                        BottomNavigationView mBottomNavigationView = getActivity().findViewById(R.id.hotel_bottom_nav);
                                        mBottomNavigationView.setSelectedItemId(R.id.orders);
                                    }
                                }
                            }).show();

                        }
                    }).addOnFailureListener(new OnFailureListener() {
                        @Override
                        public void onFailure(@NonNull Exception e) {
                            Log.e("FAILURE", "SFailure sending Request.");
                        }
                    });


                }
            }

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });





    }

    @Override
    public void onStart() {
        super.onStart();
        if(user != null) {
            FirebaseDatabase.getInstance().getReference().child("PendingList").child(Integer.toString(user.getEmail().hashCode())).addValueEventListener(new ValueEventListener() {
                @Override
                public void onDataChange(@NonNull DataSnapshot snapshot) {
                    if (snapshot.exists()) {
                        if (snapshot.getValue().equals("true")) {
                            new AlertDialog.Builder(getContext())
                                    .setTitle("You Request is in PENDING. Please wait...")
                                    .setNeutralButton("Ok", new DialogInterface.OnClickListener() {
                                        @Override
                                        public void onClick(DialogInterface dialog, int which) {
                                            if (getActivity() != null) {
                                                getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new OrdersFragment()).commit();
                                                BottomNavigationView mBottomNavigationView = getActivity().findViewById(R.id.hotel_bottom_nav);
                                                mBottomNavigationView.setSelectedItemId(R.id.orders);
                                            }

                                        }
                                    }).show();
                        } else {
                            if(getActivity() != null)
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new UpdateMenuFragment()).commit();
                        }
                    }
                    else{
                        if(getActivity() != null)
                            getActivity().getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container, new SendRequestFragment()).commit();

                    }


                }

                @Override
                public void onCancelled(@NonNull DatabaseError error) {

                }
            });
        }
    }

}