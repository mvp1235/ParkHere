package edu.sjsu.team408.parkhere;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
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

public class BookingHistoryActivity extends ListActivity {

    public static final int VIEW_DETAIL_HISTORY_BOOKING_ = 5000;

    private ArrayList<ParkingSpace> parkingSpaces;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private User currentUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_reservation_list);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();


        parkingSpaces = new ArrayList<>();

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

                            parkingSpaces = currentUser.getMyCurrentReservedParkings();
                            showCurrentlyReservedParkings();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {}
        });

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ParkingSpace parking = (ParkingSpace)getListAdapter().getItem(position);
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


        intent.putExtra(SearchResultActivity.PARKING_BUNDLE, b);
        intent.putExtra("requestCode", VIEW_DETAIL_HISTORY_BOOKING_);
        startActivityForResult(intent, VIEW_DETAIL_HISTORY_BOOKING_);

    }

    private void showCurrentlyReservedParkings() {
        if(parkingSpaces == null) {
            //empty
            return;
        }
        // Create the adapter to convert the array to views
        HistoryParkingSpaceAdapter adapter = new HistoryParkingSpaceAdapter(this, parkingSpaces);

        // Attach the adapter to a ListView
        setListAdapter(adapter);
    }
}
