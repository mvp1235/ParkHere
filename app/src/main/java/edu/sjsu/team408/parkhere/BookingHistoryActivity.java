package edu.sjsu.team408.parkhere;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Activity which shows a list of all listings booked by the current user
 */
public class BookingHistoryActivity extends ListActivity {

    public static final int VIEW_DETAIL_HISTORY_BOOKING_ = 5000;

    private ArrayList<Listing> listings;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();


        listings = new ArrayList<>();

        //populateDataForTesting();

        //Populate parking spaces with user's reserved parking spaces
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);

                            listings = currentUser.getMyCurrentReservedParkings();
                            showCurrentlyReservedParkings();
                            checkIntent();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });



    }

    /**
     * Extract and validate data from input fields, and prepare for DetailedParkingActivity
     */
    private void checkIntent() {
        Intent afterPaymentIntent = getIntent();
        String parkingIdRef = afterPaymentIntent.getStringExtra("parkingIDRef");
        if(parkingIdRef == null) {
            return;
        }
//        int count = 0;
//        if (getListAdapter() != null)
           int count = getListAdapter().getCount();

        if(!parkingIdRef.equals("")) {
            for (int i = 0; i < count; i++) {
                Listing parking = (Listing) getListAdapter().getItem(i);
                if (parkingIdRef.equals(parking.getParkingIDRef())) {
                    Intent intent = new Intent(this, DetailParkingActivity.class);

                    Bundle b = new Bundle();
                    b.putParcelable(SearchResultActivity.ADDRESS, parking.getAddress());
                    b.putParcelable(SearchResultActivity.OWNER, parking.getOwner());
                    b.putString(SearchResultActivity.PARKING_IMAGE_URL, parking.getParkingImageUrl());
                    b.putString(SearchResultActivity.SPECIAL_INSTRUCTION, parking.getSpecialInstruction());
                    b.putString(SearchResultActivity.START_DATE, parking.getStartDate());
                    b.putString(SearchResultActivity.END_DATE, parking.getEndDate());
                    b.putString(SearchResultActivity.START_TIME, parking.getStartTime());
                    b.putString(SearchResultActivity.END_TIME, parking.getEndTime());
                    b.putDouble(SearchResultActivity.PRICE, parking.getPrice());
                    b.putString(SearchResultActivity.OWNER_PARKING_ID, parking.getOwnerParkingID());
                    b.putString(SearchResultActivity.PARKING_ID_REF, parking.getParkingIDRef());
                    b.putString(SearchResultActivity.LISTING_ID, parking.getId());


                    intent.putExtra(SearchResultActivity.PARKING_BUNDLE, b);
                    intent.putExtra("requestCode", VIEW_DETAIL_HISTORY_BOOKING_);
                    startActivityForResult(intent, VIEW_DETAIL_HISTORY_BOOKING_);
                }
            }
        }
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Listing parking = (Listing)getListAdapter().getItem(position);
        Intent intent = new Intent(this, DetailParkingActivity.class);

        Bundle b = new Bundle();
        b.putParcelable(SearchResultActivity.ADDRESS, parking.getAddress());
        b.putParcelable(SearchResultActivity.OWNER, parking.getOwner());
        b.putString(SearchResultActivity.PARKING_IMAGE_URL, parking.getParkingImageUrl());
        b.putString(SearchResultActivity.SPECIAL_INSTRUCTION, parking.getSpecialInstruction());
        b.putString(SearchResultActivity.START_DATE, parking.getStartDate());
        b.putString(SearchResultActivity.END_DATE, parking.getEndDate());
        b.putString(SearchResultActivity.START_TIME, parking.getStartTime());
        b.putString(SearchResultActivity.END_TIME, parking.getEndTime());
        b.putDouble(SearchResultActivity.PRICE, parking.getPrice());
        b.putString(SearchResultActivity.OWNER_PARKING_ID, parking.getOwnerParkingID());
        b.putString(SearchResultActivity.LISTING_ID, parking.getId());
        b.putString(SearchResultActivity.PARKING_ID_REF, parking.getParkingIDRef());


        intent.putExtra(SearchResultActivity.PARKING_BUNDLE, b);
        intent.putExtra("requestCode", VIEW_DETAIL_HISTORY_BOOKING_);
        startActivityForResult(intent, VIEW_DETAIL_HISTORY_BOOKING_);

    }

    /**
     * Display the list
     */
    private void showCurrentlyReservedParkings() {
        if(listings == null) {
            //empty
            return;
        }
        // Create the adapter to convert the array to views
        HistoryParkingSpaceAdapter adapter = new HistoryParkingSpaceAdapter(this, listings);

        // Attach the adapter to a ListView
        setListAdapter(adapter);
    }
}
