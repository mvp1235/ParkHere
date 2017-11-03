package edu.sjsu.team408.parkhere;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;

/**
 * Created by MVP on 11/3/2017.
 */

public class HistoryParkingSpaceAdapter extends ArrayAdapter<ParkingSpace> {

    private Context mContext;

    public HistoryParkingSpaceAdapter(@NonNull Context context, ArrayList<ParkingSpace> parkingSpaces) {
        super(context, 0, parkingSpaces);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the data item for this position
        ParkingSpace parking = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.history_item_layout, parent, false);
        }

        // Lookup view for data population
        ImageView parkingPhoto = (ImageView) convertView.findViewById(R.id.historyParkingPhoto);
        TextView parkingDate = (TextView) convertView.findViewById(R.id.historyParkingDate);
        TextView parkingPrice = (TextView) convertView.findViewById(R.id.hitoryParkingPrice);

        //Load URL into image view
        Picasso.with(mContext).load(parking.getParkingImageUrl()).into(parkingPhoto);

        //If start date and end date are the same, only display the start date
        //otherwise, have it in the format of "startDate - endDate"
        String availableDate = parking.getStartDate();
        if (!parking.getStartDate().equalsIgnoreCase(parking.getEndDate())) {
            availableDate += " - " + parking.getEndDate();
        }
        parkingDate.setText(availableDate);

        parkingPrice.setText("$" + String.valueOf(parking.getPrice()));

        return convertView;
    }

}
