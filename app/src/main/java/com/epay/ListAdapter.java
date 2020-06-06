package com.epay;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import androidx.annotation.NonNull;

import java.util.ArrayList;
import java.util.Collections;

import static com.epay.R.layout.list_recycler;
import static com.epay.R.layout.nav_header_main;

public class ListAdapter extends ArrayAdapter<ArrayList<Data>> {
    private Context context;
    private ArrayList<Data> dataArrayList;


    public ListAdapter(@NonNull Context context, ArrayList<Data> dataArrayList) {
        super(context, list_recycler, Collections.singletonList(dataArrayList));
        this.context = context;
        this.dataArrayList = dataArrayList;
    }

    public View getView(int position, View view, ViewGroup parent) {
         View view1 = LayoutInflater.from(context).inflate(list_recycler,null,true);

         TextView titleText = (TextView) view1.findViewById(R.id.titleData);

         Data data = dataArrayList.get(position);
         titleText.setText(data.getName());

        return view1;

    };
}
