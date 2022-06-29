package edu.ua.parkalot;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.google.gson.annotations.SerializedName;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

public class ParkAdapter extends RecyclerView.Adapter<ParkAdapter.ParkViewHolder> {

    static final String TAG = "RecyclerViewAdapter";

    Context context;
    List<Park> parkList;

    public ParkAdapter(Context context, ArrayList<Park> parkList) {
        this.context = context;
        this.parkList = parkList;
    }

    //@NonNull
    @Override
    public ParkViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        Context context = parent.getContext();
        int layoutIdForListItem = R.layout.park_item;
        LayoutInflater inflater = LayoutInflater.from(context);
        View view = inflater.inflate(layoutIdForListItem, parent, false);
        return new ParkViewHolder(view);
    }

    @Override
    public void onBindViewHolder(@NonNull ParkViewHolder holder, int position) {
        Log.d(TAG, "onBindViewHolder called");

        holder.bind(position);

        //holder.tv_name.setText(parkList.get(position));

        // Not the best way to do it (not optimal)
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Log.d(TAG, "onClick -> clicked on: " + parkList.get(position).name + " | Coords: " + parkList.get(position).getCoordinates().getLat() + "," + parkList.get(position).getCoordinates().getLongitude() + " | src: " + parkList.get(position).getImg());
                Toast.makeText(context, parkList.get(position).name, Toast.LENGTH_SHORT).show();

                /* Fill variables 'putExtra' so you can use in other activities */
                Intent about_activity = new Intent(context, AboutActivity.class);
                about_activity.putExtra("parkID", parkList.get(position).id);
                about_activity.putExtra("Title", parkList.get(position).name);
                about_activity.putExtra("lat_coords", parkList.get(position).getCoordinates().getLat());
                about_activity.putExtra("long_coords", parkList.get(position).getCoordinates().getLongitude());
                context.startActivity(about_activity);
            }
        });
    }

    @Override
    public int getItemCount() {
        return parkList.size();
    }

    class ParkViewHolder extends RecyclerView.ViewHolder {
        private final TextView tv_name;
        private final TextView tv_coords;
        private final TextView tv_area;
        private final TextView tv_slots;
        private final TextView tv_sched;
        private final TextView tv_public;


        public ParkViewHolder(@NonNull View itemView) {
            super(itemView);

            tv_name = itemView.findViewById(R.id.tv_name);
            tv_coords = itemView.findViewById(R.id.tv_coords);
            tv_area = itemView.findViewById(R.id.tv_area);
            tv_slots = itemView.findViewById(R.id.tv_slots);
            tv_sched = itemView.findViewById(R.id.tv_sched);
            tv_public = itemView.findViewById(R.id.tv_public);
        }

        void bind(int parkIndex) {
            Park park = parkList.get(parkIndex);
            //parque_id.setText(park.getId());
            tv_name.setText(park.getName());
            tv_coords.setText(String.valueOf(park.getCoordinates().latitude) + ";" + String.valueOf(park.getCoordinates().longitude));
            tv_area.setText(park.getArea().value + " " + park.getArea().units);
            tv_slots.setText(String.valueOf(park.getCapacity()) + " slots");
            tv_sched.setText(String.valueOf(park.getSchedule()));
            tv_public.setText(String.valueOf(park.getType()));
        }
    }

    public interface onParkListener {
        void onItemClick(int position);
    }

    static class Park {
        //@SerializedName("name")
        private Integer id;
        private String img;
        private String name;
        private Location coords;
        private Area area;
        private Integer slots;
        private String schedule;
        private String type;

        public Park(Integer id, String img, String name, Location coords, Area area, Integer slots, String schedule, String type) {
            this.id = id;
            this.img = img;
            this.name = name;
            this.coords = coords;
            this.area = area;
            this.slots = slots;
            this.schedule = schedule;
            this.type = type;
        }

        public Integer getId() {
            return id;
        }

        public String getImg() {
            return img;
        }

        public String getName() {
            return name;
        }

        public Location getCoordinates() {
            return coords;
        }

        public Area getArea() {
            return area;
        }

        public Integer getCapacity() {
            return slots;
        }

        public String getSchedule() {
            return schedule;
        }

        public String getType() {
            return type;
        }
    }

    static class Location {
        @SerializedName("lat")
        private Double latitude;
        @SerializedName("long")
        private Double longitude;

        public Location(Double latitude, Double longitude) {
            this.latitude = latitude;
            this.longitude = longitude;
        }

        public Double getLat() {
            return latitude;
        }

        public Double getLongitude() {
            return longitude;
        }
    }

    public class Area {
        private long value;
        private String units;

        public long getValue() {
            return value;
        }

        public void setValue(long value) {
            this.value = value;
        }

        public String getUnits() {
            return units;
        }

        public void setUnits(String value) {
            this.units = value;
        }
    }
}


