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
    private TextView username,post,date;

    public SessionAdapter(Context context, int resource, ArrayList<Session> objects) {
        super(context, resource, objects);
        this.context = context;
        resourseId = resource;
        this.dataList = objects;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = ((Activity)context).getLayoutInflater();
        View rowView = inflater.inflate(resourseId, parent, false);
//        username = (TextView) rowView.findViewById(R.id.text_username);
//        post = (TextView) rowView.findViewById(R.id.text_post);
//        date = (TextView) rowView.findViewById(R.id.text_date);
//        username.setText(dataList.get(position).getUsername());
//        post.setText(dataList.get(position).getNote());
//        date.setText(dataList.get(position).getDateCreated());

        return rowView;
    }
}
