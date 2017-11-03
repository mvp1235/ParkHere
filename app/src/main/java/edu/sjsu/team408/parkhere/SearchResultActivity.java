package edu.sjsu.team408.parkhere;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;

/**
 * Created by DuocNguyen on 11/1/17.
 */

public class SearchResultActivity extends ListActivity {

    static final String PARKING_BUNDLE = "parkingBundle";
    static final String ADDRESS = "address";
    static final String OWNER = "owner";
    static final String PARKING_IMAGE_URL = "parkingImageUrl";
    static final String SPECIAL_INSTRUCTION = "specialInstruction";
    static final String START_DATE = "startDate";
    static final String END_DATE = "endDate";
    static final String PRICE = "price";


    private static final int VIEW_DETAIL_PARKING = 101;
    private ArrayList<ParkingSpace> parkingSpaces;
    private DatabaseReference databaseReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        databaseReference = FirebaseDatabase.getInstance().getReference();

        parkingSpaces = new ArrayList<ParkingSpace>();
        //get user input for location
        final String searchTerm = intent.getStringExtra("date");

        databaseReference.addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("AvailableParkings").hasChild(searchTerm)) {
                    for(DataSnapshot userIDList: dataSnapshot.child("AvailableParkings").child(searchTerm).getChildren()) {
                        ParkingSpace p = userIDList.getValue(ParkingSpace.class);
                        parkingSpaces.add(p);
                        showResult();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //not supported
            }
        });
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ParkingSpace parking = (ParkingSpace)getListAdapter().getItem(position);
        Intent intent = new Intent(this, DetailParkingActivity.class);

        Bundle b = new Bundle();
        b.putParcelable(ADDRESS, parking.getAddress());
        b.putParcelable(OWNER, parking.getOwner());
        b.putString(PARKING_IMAGE_URL, parking.getParkingImageUrl());
        b.putString(SPECIAL_INSTRUCTION, parking.getSpecialInstruction());
        b.putString(START_DATE, parking.getStartDate());
        b.putString(END_DATE, parking.getEndDate());
        b.putDouble(PRICE, parking.getPrice());

        intent.putExtra(PARKING_BUNDLE, b);
        intent.putExtra("requestCode", VIEW_DETAIL_PARKING);
        startActivityForResult(intent, VIEW_DETAIL_PARKING);

    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == VIEW_DETAIL_PARKING) {
            if (resultCode == RESULT_OK) {
                finish();
            }
        }
    }

    private void showResult (){
        // Create the adapter to convert the array to views
        ParkingSpaceAdapter adapter = new ParkingSpaceAdapter(this, parkingSpaces);

        // Attach the adapter to a ListView
        setListAdapter(adapter);
    }

}