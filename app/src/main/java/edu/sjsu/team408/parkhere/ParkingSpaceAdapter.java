package edu.sjsu.team408.parkhere;

import android.content.Context;
import android.location.Location;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by MVP on 11/1/2017.
 */

public class ParkingSpaceAdapter extends ArrayAdapter<ParkingSpace> {
    private Context mContext;
    private Location desiredLocation;

    public ParkingSpaceAdapter(@NonNull Context context, ArrayList<ParkingSpace> parkingSpaces,
                               Location desiredLocation) {
        super(context, 0, parkingSpaces);
        mContext = context;
        this.desiredLocation = desiredLocation;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the data item for this position
        ParkingSpace parking = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_results_layout,
                    parent, false);
        }

        // Lookup view for data population
        ImageView parkingPhoto = (ImageView) convertView.findViewById(R.id.parkingPhoto);
        TextView parkingDate = (TextView) convertView.findViewById(R.id.parkingDate);
        TextView parkingPrice = (TextView) convertView.findViewById(R.id.parkingPrice);
        TextView parkingDistance = (TextView) convertView.findViewById(R.id.parkingDistance);

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

        double distance = parking.getAddress().getDistanceBetweenThisAnd(desiredLocation);
        String distanceString = String.format(Locale.US, "%.2f miles away", distance);
        parkingDistance.setText(distanceString);


        return convertView;
    }
}
