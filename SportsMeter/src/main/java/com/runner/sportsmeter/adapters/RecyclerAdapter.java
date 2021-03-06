package com.runner.sportsmeter.adapters;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import com.runner.sportsmeter.R;
import com.runner.sportsmeter.common.Utility;
import com.runner.sportsmeter.models.Sessions;

import java.util.ArrayList;

/**
 * Created by angelr on 17-Sep-15.
 */
public class RecyclerAdapter extends RecyclerView.Adapter<RecyclerAdapter.ViewHolder> {
    private ArrayList<Sessions> dataList;

    // Provide a reference to the views for each data item
    // Complex data items may need more than one view per item, and
    // you provide access to all the views for a data item in a view holder
    public static class ViewHolder extends RecyclerView.ViewHolder {
        // each data item is just a string in this case
        private TextView distance, duration, maxSpeed, averageSpeed, createdAt, timePerKm, user, position;
        public ViewHolder(View v) {
            super(v);
            distance = (TextView) v.findViewById(R.id.view_distance);
            duration = (TextView) v.findViewById(R.id.view_duration);
            maxSpeed = (TextView) v.findViewById(R.id.view_maxspeed);
            averageSpeed = (TextView) v.findViewById(R.id.view_avrspeed);
            timePerKm = (TextView) v.findViewById(R.id.view_timeperkm);
            createdAt = (TextView) v.findViewById(R.id.view_created);
            user = (TextView) v.findViewById(R.id.view_user);
            position = (TextView) v.findViewById(R.id.position);
        }
    }

    // Provide a suitable constructor (depends on the kind of dataset)
    public RecyclerAdapter(ArrayList<Sessions> myDataset) {
        dataList = myDataset;
    }

    // Create new views (invoked by the layout manager)
    @Override
    public RecyclerAdapter.ViewHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.leaderboard_row, parent, false);
        // set the view's size, margins, paddings and layout parameters

        ViewHolder vh = new ViewHolder(v);
        return vh;
    }

    // Replace the contents of a view (invoked by the layout manager)
    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        // - get element from your dataset at this position
        // - replace the contents of the view with that element
        holder.position.setText(String.valueOf(position + 1));
        holder.distance.setText(Utility.formatDistance(dataList.get(position).getDistance()));
        holder.duration.setText(Utility.formatDurationToMinutesString(dataList.get(position).getDuration()));
        holder.maxSpeed.setText(Utility.formatSpeed(dataList.get(position).getMaxSpeed()));
        holder.averageSpeed.setText(Utility.formatSpeed(dataList.get(position).getAverageSpeed()));
        holder.timePerKm.setText(Utility.formatPace(dataList.get(position).getTimePerKilometer()));
        holder.createdAt.setText(Utility.formatDate(dataList.get(position).getCreatedAt()));
        holder.user.setText(String.valueOf(dataList.get(position).getName()));
    }

    // Return the size of your dataset (invoked by the layout manager)
    @Override
    public int getItemCount() {
        return dataList.size();
    }
}