package com.newrunner.googlemap;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

/**
 * Created by angelr on 19-Aug-15.
 */
public class SessionAdapter extends ArrayAdapter<Session> {
    private Context context;
    private int resourseId;
    private ArrayList<Session> dataList;
    private TextView distance, duration, maxSpeed, averageSpeed, createdAt, timePerKm, user;

    public SessionAdapter(Context context, int resource, ArrayList<Session> objects) {
        super(context, resource, objects);
        this.context = context;
        resourseId = resource;
        this.dataList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity) context).getLayoutInflater();
        View rowView = inflater.inflate(resourseId, parent, false);
        // initialize
        distance = (TextView) rowView.findViewById(R.id.view_distance);
        duration = (TextView) rowView.findViewById(R.id.view_duration);
        maxSpeed = (TextView) rowView.findViewById(R.id.view_maxspeed);
        averageSpeed = (TextView) rowView.findViewById(R.id.view_avrspeed);
        timePerKm = (TextView) rowView.findViewById(R.id.view_timeperkm);
        createdAt = (TextView) rowView.findViewById(R.id.view_created);
        user = (TextView) rowView.findViewById(R.id.view_user);
        // set text
        distance.setText(String.valueOf(dataList.get(position).getDistance()) + " m");
        duration.setText(String.valueOf(Calculations.roundToTwoDigitsAfterDecimalPoint(dataList.get(position).getDuration()/1000/60) + " min"));
        maxSpeed.setText(String.valueOf(dataList.get(position).getMaxSpeed()) + " kmph");
        averageSpeed.setText(String.valueOf(dataList.get(position).getAverageSpeed()) + " kmph");
        timePerKm.setText(String.valueOf(dataList.get(position).getTimePerKilometer()) + " min/km");
        createdAt.setText(String.valueOf(dataList.get(position).getCreatedAt()));
        user.setText(String.valueOf(dataList.get(position).getUserName()));

        return rowView;
    }
}
