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

public class ListingHistoryActivity extends ListActivity {

    public static final int VIEW_DETAIL_HISTORY_LISTING = 2001;

    private ArrayList<Listing> listings;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_listing_history);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        listings = new ArrayList<>();


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            User currentUser = null;
                            currentUser = dataSnapshot.child("Users").child(targetID).
                                    getValue(User.class);

                            listings = currentUser.getMyListingHistory();

                            showMyListingHistory();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == VIEW_DETAIL_HISTORY_LISTING && resultCode == RESULT_OK) {
            finish();
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
        b.putDouble(SearchResultActivity.PRICE, parking.getPrice());
        b.putString(SearchResultActivity.OWNER_PARKING_ID, parking.getOwnerParkingID());
        b.putString(SearchResultActivity.PARKING_ID_REF, parking.getParkingIDRef());
        b.putString(SearchResultActivity.LISTING_ID, parking.getId());
        b.putString(SearchResultActivity.START_TIME, parking.getStartTime());
        b.putString(SearchResultActivity.END_TIME, parking.getEndTime());

        intent.putExtra(SearchResultActivity.PARKING_BUNDLE, b);
        intent.putExtra("requestCode", VIEW_DETAIL_HISTORY_LISTING);
        startActivityForResult(intent, VIEW_DETAIL_HISTORY_LISTING);

    }


    private void showMyListingHistory() {
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
