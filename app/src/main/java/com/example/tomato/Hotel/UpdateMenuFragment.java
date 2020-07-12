package com.example.tomato.Hotel;

import android.app.FragmentTransaction;
import android.content.DialogInterface;
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
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.tomato.Adapter.MenuAdapter;
import com.example.tomato.Model.Menu;
import com.example.tomato.R;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
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
import java.util.List;

public class UpdateMenuFragment extends Fragment {

    private EditText item_name_txt;
    private EditText item_price_txt;

    private RecyclerView menu_recycler_view;
    private List<Menu> menuList;
    private MenuAdapter menuAdapter;

    private Button add;
    private Button cancel;
    private Button send_request;
    private String menu_id;

    private FirebaseUser user;

    private NestedScrollView parent_view;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View v = inflater.inflate(R.layout.fragment_update_menu, container, false);

        user = FirebaseAuth.getInstance().getCurrentUser();


        add = v.findViewById(R.id.add_item);

        parent_view = v.findViewById(R.id.parent_scroll);

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


        FirebaseDatabase.getInstance().getReference().child("Hotel").child(Integer.toString(user.getEmail().hashCode())).child("menu_id").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(@NonNull DataSnapshot snapshot) {
                if (!snapshot.exists()) {
                    Snackbar snackbar = Snackbar.make(parent_view, "Something Went Wrong..", Snackbar.LENGTH_LONG);
                    snackbar.show();
                } else {
                    menu_id = snapshot.getValue(String.class);
                    menuList.clear();
                    FirebaseDatabase.getInstance().getReference().child("Menu").child(menu_id).addValueEventListener(new ValueEventListener() {

                        @Override
                        public void onDataChange(@NonNull DataSnapshot snapshot) {
                            for (DataSnapshot dataSnapshot : snapshot.getChildren()) {
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

            @Override
            public void onCancelled(@NonNull DatabaseError error) {

            }
        });



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
                                                new AlertDialog.Builder(getContext()).
                                                            setTitle("Are you sure you want to update Menu ?")
                                                            .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                                                                @Override
                                                                public void onClick(DialogInterface dialog, int which) {
                                                                    updateMenu();
                                                                    
                                                                }
                                                            }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                                        @Override
                                                        public void onClick(DialogInterface dialog, int which) {

                                                        }
                                                    }).show();
                                                }
                                            });
        
        
        return v;
    }

    private void updateMenu() {
        FirebaseDatabase.getInstance().getReference().child("Menu").child(menu_id).removeValue().addOnCompleteListener(new OnCompleteListener<Void>() {
            @Override
            public void onComplete(@NonNull Task<Void> task) {
                if(task.isSuccessful()){
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
                    Snackbar snackbar = Snackbar.make(parent_view, "Menu Updated Successfully", Snackbar.LENGTH_LONG);
                    snackbar.show();
                    getFragmentManager().beginTransaction().replace(R.id.fragment_container,new OrdersFragment()).commit();
                    BottomNavigationView mBottomNavigationView = getActivity().findViewById(R.id.hotel_bottom_nav);
                    mBottomNavigationView.setSelectedItemId(R.id.orders);

                }
            }
        });


    }
}