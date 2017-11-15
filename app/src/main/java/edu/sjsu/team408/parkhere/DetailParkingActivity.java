package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.graphics.Bitmap;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;

public class DetailParkingActivity extends AppCompatActivity {

    private TextView addressTV, ownerTV, specialInstructionTV, dateTV, priceTV;
    private ImageView parkingPhoto;
    private Button reserveBtn;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ParkingSpace clickedParking;
    private User currentUser;
    private ParkingSpace chosenParking;
    private static String parkingPhotoString = "https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_parking);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        //Reference to the UI elements
        addressTV = (TextView) findViewById(R.id.detailParkingAddress);
        ownerTV = (TextView) findViewById(R.id.detailParkingOwner);
        specialInstructionTV = (TextView) findViewById(R.id.detailParkingSpecialInstruction);
        dateTV = (TextView) findViewById(R.id.detailParkingDate);
        priceTV = (TextView) findViewById(R.id.detailParkingPrice);
        parkingPhoto = (ImageView) findViewById(R.id.detailParkingPhoto);
        reserveBtn = (Button) findViewById(R.id.reserveBtn);

        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(SearchResultActivity.PARKING_BUNDLE);

        clickedParking = new ParkingSpace(bundle);

        //Retrieve parking photo encoded string and convert back to bitmap and set it to the image view
        getParkingURL(clickedParking.getParkingID());
        //Parking photo is the default one, user has not set a photo for the listing yet
        if (parkingPhotoString.contains("http")) {
            Picasso.with(getApplicationContext()).load(parkingPhotoString).into(parkingPhoto);
        } else {    // parking photo URL is the actual encoded string, decode here and obtain bitmap
            try {
                Bitmap bitmap = EditProfileActivity.decodeFromFirebaseBase64(parkingPhotoString);
                parkingPhoto.setImageBitmap(bitmap);
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        addressTV.setText(clickedParking.getAddress().toString());      //crashes here
        ownerTV.setText(clickedParking.getOwner().getName());
        specialInstructionTV.setText(clickedParking.getSpecialInstruction());

        //If start date and end date are the same, only display the start date
        //otherwise, have it in the format of "startDate - endDate"
        String availableDate = clickedParking.getStartDate();
        if (!clickedParking.getStartDate().equalsIgnoreCase(clickedParking.getEndDate())) {
            availableDate += " - " + clickedParking.getEndDate();
        }
        dateTV.setText(availableDate);

        priceTV.setText("$" + String.valueOf(clickedParking.getPrice()));

        int request = intent.getIntExtra("requestCode", 0);

        //Add reservation functionality to reserve button only when the detailed page was requested from search result page
        if (request == SearchResultActivity.VIEW_DETAIL_PARKING_FROM_RESULT) {
            reserveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeReservation();
                }
            });
        }

        // Set appropriate text for button
        if (request == SearchResultActivity.VIEW_DETAIL_PARKING_FROM_RESULT) {
            reserveBtn.setText("Reserve");
        } else if (request == BookingHistoryActivity.VIEW_DETAIL_HISTORY_BOOKING_) {
            reserveBtn.setText("Book Again");
        } else if (request == ListingHistoryActivity.VIEW_DETAIL_HISTORY_LISTING) {
            reserveBtn.setText("List Again");
        }

        //Hide distance if user is checking history
        LinearLayout ll = findViewById(R.id.detailParkingDistanceLL);
        if (request == BookingHistoryActivity.VIEW_DETAIL_HISTORY_BOOKING_ || request == ListingHistoryActivity.VIEW_DETAIL_HISTORY_LISTING) {
            ll.setVisibility(View.GONE);
        } else {
            ll.setVisibility(View.VISIBLE);
        }
    }


    /**
     * Get the parking URL for a listing with a certain id
     * @param parkingID the id of the listing to be retrieved from database
     * @return the default url of the parking photo if not set, the encoded string of bitmap if the user has set one photo for the listing
     */
    public void getParkingURL(final String parkingID) {
        final String startDate = clickedParking.getStartDate();

        databaseReference.child("AvailableParkings").addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.hasChild(startDate)) {
                    chosenParking = dataSnapshot.child(startDate).child(parkingID).getValue(ParkingSpace.class);
                    parkingPhotoString = chosenParking.getParkingImageUrl();
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Implement reservation functionality here
     */
    public void makeReservation() {
        final String startDate = clickedParking.getStartDate();

        //here add the reserved parking to user's myCurrentReservedParkings lists

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);

                            currentUser.addReservedParking(clickedParking);

                            //for now make reservation will just delete the listing on database.

                            //databaseReference = FirebaseDatabase.getInstance().getReference();
                            String currentUserID = currentUser.getId();
                            databaseReference.child("AvailableParkings").child(startDate).removeValue();
                            databaseReference.child("Users").child(currentUserID).setValue(currentUser);

                            setResult(RESULT_OK);

                            finish();
                        }
                    }
                }
            }
            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}