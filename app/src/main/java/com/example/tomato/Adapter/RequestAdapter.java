package com.example.tomato.Adapter;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.fragment.app.FragmentActivity;
import androidx.recyclerview.widget.RecyclerView;

import com.example.tomato.Admin.RequestDetailFragment;
import com.example.tomato.Model.Request;
import com.example.tomato.R;

import java.util.List;

public class RequestAdapter extends RecyclerView.Adapter<RequestAdapter.ViewHolder> {
    private Context mContext;
    private List<Request> requestsList;
    public RequestAdapter(Context mContext, List<Request> requestsList){
        this.mContext = mContext;
        this.requestsList = requestsList;
    }

    @NonNull
    @Override
    public RequestAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.request_item,parent, false);
        return new RequestAdapter.ViewHolder(v);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        final Request req = requestsList.get(position);
        holder.hotel_name.setText(req.getHotel_name());
        holder.request_view.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                mContext.getSharedPreferences("REQUESTLIST",Context.MODE_PRIVATE).edit().putString("request_id",req.getRequest_id()).commit();
                ((FragmentActivity)mContext).getSupportFragmentManager().beginTransaction().replace(R.id.fragment_container,new RequestDetailFragment()).commit();
            }
        });

    }

    @Override
    public int getItemCount() {
        return requestsList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public TextView hotel_name;
        public Button request_view;
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
            hotel_name = itemView.findViewById(R.id.hotel_name);
            request_view = itemView.findViewById(R.id.request_view);
        }
    }
}
