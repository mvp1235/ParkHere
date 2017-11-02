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

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.hasChild(searchTerm)) {
                    for(DataSnapshot userIDList: dataSnapshot.child(searchTerm).getChildren()) {
                        ParkingSpace p = userIDList.getValue(ParkingSpace.class);
                        parkingSpaces.add(p);
                        showResult();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //unsupported
            }
        });

        //showResult();

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
        startActivityForResult(intent, VIEW_DETAIL_PARKING);

    }

    //Generate some parking spaces for testing
    //Later on, we will generate an arraylist of parking spaces with datas in Firebase
    public void populateDefaultParkingSpaces() {
        //parkingSpaces = new ArrayList<ParkingSpace>();

//        public Address(String streetAddress, String city, String state, String zipCode) {
        Address address = new Address("1 Washington Square", "San Jose", "CA", "95112");

//        public User(String id, String name, Address address, String phoneNumber, String emailAddress, String profileURL) {
        //Assume one user is creating all the listing
        User user = new User("huy123", "Huy Nguyen", address, "408-123-4567", "huy.nguyen@sjsu.edu",
                "http://static2.businessinsider.com/image/5899ffcf6e09a897008b5c04-1200/.jpg");


//    public ParkingSpace(Address address, User owner, String parkingImageUrl, String specialInstruction, String startDate, String endDate, int price)
        ParkingSpace p = new ParkingSpace(address, user, "https://media-cdn.tripadvisor.com/media/photo-s/0f/ae/73/2f/private-parking-right.jpg",
                "watch out for dogs", "1/1/2017", "2/2/2017", 5.99);

        //parkingSpaces.add(p);
        //parkingSpaces.add(p);
        //parkingSpaces.add(p);
        //parkingSpaces.add(p);
        //parkingSpaces.add(p);
    }

    private void showResult (){
        //Construct data source
        populateDefaultParkingSpaces();

        // Create the adapter to convert the array to views
        ParkingSpaceAdapter adapter = new ParkingSpaceAdapter(this, parkingSpaces);

        // Attach the adapter to a ListView
        setListAdapter(adapter);


    }

}