package edu.sjsu.team408.parkhere;

import android.content.Context;
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

/**
 * Created by MVP on 11/29/2017.
 */

public class ParkingSpaceAdapter extends ArrayAdapter<ParkingSpace> {
    private StorageReference storageReference;

    private Context mContext;
    private String parkingID;

    public ParkingSpaceAdapter(@NonNull Context context, ArrayList<ParkingSpace> parkingSpaces) {
        super(context, 0, parkingSpaces);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(final int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the data item for this position
        ParkingSpace parking = getItem(position);
        parkingID = parking.getParkingID();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.parking_space_layout,
                    parent, false);
        }

        // Lookup view for data population
        final ImageView parkingPhoto = (ImageView) convertView.findViewById(R.id.parkingPhoto);

        TextView parkingAddress = (TextView) convertView.findViewById(R.id.parkingAddress);
        TextView parkingInstruction = (TextView) convertView.findViewById(R.id.parkingInstruction);
        TextView parkingIdTV = (TextView) convertView.findViewById(R.id.parkingID);

        parkingAddress.setText(parking.getAddress().toString());
        parkingInstruction.setText(parking.getSpecialInstruction());
        parkingIdTV.setText(parkingID);

        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("parkingPhotos/" + parking.getParkingID()).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                Picasso.with(mContext).load(uri.toString()).into(parkingPhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                // In this case, load the default parking photo
                Picasso.with(mContext).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(parkingPhoto);
            }
        });

        return convertView;
    }


}
