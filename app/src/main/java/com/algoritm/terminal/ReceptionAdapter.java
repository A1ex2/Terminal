package com.algoritm.terminal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import java.util.ArrayList;

public class ReceptionAdapter extends ArrayAdapter<Reception> {

    private ArrayList<Reception> mReceptions;
    private int mResource;
    private LayoutInflater mInflater;

    public ReceptionAdapter(Context context, int resource, ArrayList<Reception> objects) {
        super(context, resource, objects);

        mReceptions = objects;
        mResource = resource;
        mInflater = LayoutInflater.from(context);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        convertView = mInflater.inflate(mResource, null);

        Reception reception = mReceptions.get(position);

        ((TextView) convertView.findViewById(R.id.itemDescription)).setText(reception.getDescription());
        ((TextView) convertView.findViewById(R.id.itemAutoNumber)).setText(reception.getAutoNumber());
        ((TextView) convertView.findViewById(R.id.itemDriver)).setText(reception.getDriver());
//        ((TextView) convertView.findViewById(R.id.itemDriverPhone)).setText(reception.getDriverPhone());

        return convertView;
    }
}
