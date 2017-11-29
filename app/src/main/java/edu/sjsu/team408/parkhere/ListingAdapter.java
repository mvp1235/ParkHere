package edu.sjsu.team408.parkhere;

import android.content.Context;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import java.util.ArrayList;
import java.util.Locale;

/**
 * Created by MVP on 11/1/2017.
 */

public class ListingAdapter extends ArrayAdapter<Listing> {
    private Context mContext;
    private Location desiredLocation;

    public ListingAdapter(@NonNull Context context, ArrayList<Listing> listings,
                          Location desiredLocation) {
        super(context, 0, listings);
        mContext = context;
        this.desiredLocation = desiredLocation;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the data item for this position
        Listing parking = getItem(position);

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.search_results_layout,
                    parent, false);
        }

        // Lookup view for data population
        final ImageView parkingPhoto = (ImageView) convertView.findViewById(R.id.parkingPhoto);
        TextView parkingDate = (TextView) convertView.findViewById(R.id.parkingDate);
        TextView parkingPrice = (TextView) convertView.findViewById(R.id.parkingPrice);
        TextView parkingDistance = (TextView) convertView.findViewById(R.id.parkingDistance);

        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("parkingPhotos/" + parking.getParkingIDRef()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                Picasso.with(getContext()).load(uri.toString()).into(parkingPhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                // In this case, load the default parking photo
                Picasso.with(getContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(parkingPhoto);
            }
        });

        //If start date and end date are the same, only display the start date
        //otherwise, have it in the format of "startDate - endDate"

        String availableDate = parking.getStartDate();
        if (!parking.getStartDate().equalsIgnoreCase(parking.getEndDate())) {
            availableDate += " - " + parking.getEndDate();
        }

        String availableTime = " From " + parking.getStartTime() + " to " + parking.getEndTime();

        parkingDate.setText(availableDate + availableTime);

        parkingPrice.setText("$" + String.valueOf(parking.getPrice()));

        double distance = parking.getAddress().getDistanceBetweenThisAnd(desiredLocation);
        String distanceString = String.format(Locale.US, "%.2f miles away", distance);
        parkingDistance.setText(distanceString);


        return convertView;
    }

}

