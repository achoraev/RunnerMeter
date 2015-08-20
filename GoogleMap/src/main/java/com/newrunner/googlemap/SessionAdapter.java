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
    private TextView distance, duration, maxSpeed, averageSpeed;

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
        distance = (TextView) rowView.findViewById(R.id.view_distance);
        duration = (TextView) rowView.findViewById(R.id.view_duration);
        maxSpeed = (TextView) rowView.findViewById(R.id.view_maxspeed);
        averageSpeed = (TextView) rowView.findViewById(R.id.view_avrspeed);
        distance.setText(String.valueOf(dataList.get(position).getDistance()));
        duration.setText(String.valueOf(dataList.get(position).getDuration()));
        maxSpeed.setText(String.valueOf(dataList.get(position).getMaxSpeed()));
        averageSpeed.setText(String.valueOf(dataList.get(position).getAverageSpeed()));

        return rowView;
    }
}
