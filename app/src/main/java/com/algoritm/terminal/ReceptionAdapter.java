package com.algoritm.terminal;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.Filter;
import android.widget.TextView;

import java.util.ArrayList;

public class ReceptionAdapter extends ArrayAdapter<Reception> {

    private ArrayList<Reception> mReceptions;
    private ArrayList<Reception> mReceptionsTemp;
    private int mResource;
    private LayoutInflater mInflater;

    public ReceptionAdapter(Context context, int resource, ArrayList<Reception> objects) {
        super(context, resource, objects);

        mReceptions = objects;
        mReceptionsTemp = (ArrayList<Reception>) mReceptions.clone();
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

    @Override
    public Filter getFilter() {
        Filter filter = new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {
                FilterResults results = new FilterResults();
                String seq = constraint.toString().toLowerCase();
                ArrayList<Reception> list = new ArrayList<>();
                if (seq.trim().length() == 0 || seq == null) {
                    results.count = mReceptionsTemp.size();
                    results.values = mReceptionsTemp;
                    list.addAll(mReceptionsTemp);
                } else {
                    for (Reception reception : mReceptionsTemp) {
                        if (reception.getDescription().toLowerCase().contains(seq.toLowerCase())
                                | reception.getAutoNumber().toLowerCase().contains(seq.toLowerCase())
                                | reception.getDriver().toLowerCase().contains(seq.toLowerCase()))

                            list.add(reception);
                    }
                }
                results.count = list.size();
                results.values = list;
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint, FilterResults results) {
                if (results.count > 0) {
                    mReceptions.clear();
                    mReceptions.addAll((ArrayList<Reception>) results.values);
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }
        };

        return filter;
    }
}
