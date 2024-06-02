package com.school.bus_autosystem.Adapter;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.school.bus_autosystem.BusArriveActivity;
import com.school.bus_autosystem.R;
import com.school.bus_autosystem.ResponseClass.BusstopArriveResponse.Response.Body.Items.Item;

import java.util.List;

public class BusStopAdapter extends RecyclerView.Adapter<BusStopAdapter.ViewHolder> {
    private List<Item> busStopList;
    private Context context;

    public BusStopAdapter(Context context, List<Item> busStopList) {
        this.busStopList = busStopList;
        this.context = context;
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        View view = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.bus_stop_type, parent, false);
        return new ViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        Item busStop = busStopList.get(position);
        holder.routeNoTextView.setText(String.valueOf(busStop.routeno));
        holder.vehicleTypeTextView.setText(busStop.vehicletp);
//        holder.arrivalTimeTextView.setText(String.format("%d초 남음", busStop.arrtime));


        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(context, BusArriveActivity.class);
                //버스 진짜 번호 110-1
                intent.putExtra("busNumber", String.valueOf(busStop.routeno));
                //routid : BSB5200107000
                Log.d("checkheck", "routid : " + busStop.routeid);
                // nodeid : BSB184300201 -> 이게 버스정류장 id
                Log.d("checkheck", "nodeid : " +  busStop.nodeid);
                // nodenm : 롯데백화점센텀시티점
                Log.d("checkheck", "nodenm : " +  busStop.nodenm);
                intent.putExtra("busStopId", String.valueOf(busStop.nodeid));
                intent.putExtra("busStopNamer", String.valueOf(busStop.nodenm));



                context.startActivity(intent);
            }
        });
    }

    @Override
    public int getItemCount() {
        return busStopList.size();
    }

    public static class ViewHolder extends RecyclerView.ViewHolder {
        public TextView routeNoTextView;
        public TextView vehicleTypeTextView;
//        public TextView arrivalTimeTextView;

        public ViewHolder(View view) {
            super(view);
            routeNoTextView = view.findViewById(R.id.route_no);
            vehicleTypeTextView = view.findViewById(R.id.vehicle_type);
//            arrivalTimeTextView = view.findViewById(R.id.arrival_time);
        }
    }
}
