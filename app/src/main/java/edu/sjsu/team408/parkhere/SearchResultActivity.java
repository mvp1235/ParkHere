package edu.sjsu.team408.parkhere;

import android.app.ListActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ListView;
import android.widget.Toast;

import java.util.ArrayList;

/**
 * Created by DuocNguyen on 11/1/17.
 */

public class SearchResultActivity extends ListActivity {

    private ArrayList<ParkingSpace> parkingSpaces;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        //Construct data source
        populateDefaultParkingSpaces();

        // Create the adapter to convert the array to views
        ParkingSpaceAdapter adapter = new ParkingSpaceAdapter(this, parkingSpaces);

        // Attach the adapter to a ListView
        setListAdapter(adapter);

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ParkingSpace parking = (ParkingSpace)getListAdapter().getItem(position);
        Toast.makeText(this, "" + parking.getAddress(), Toast.LENGTH_SHORT).show();
    }

    //Generate some parking spaces for testing
    //Later on, we will generate an arraylist of parking spaces with datas in Firebase
    public void populateDefaultParkingSpaces() {
        parkingSpaces = new ArrayList<ParkingSpace>();

//        public Address(String streetAddress, String city, String state, String zipCode) {
        Address address = new Address("1 Washington Square", "San Jose", "CA", "95112");

//        public User(String id, String name, Address address, String phoneNumber, String emailAddress, String profileURL) {
        //Assume one user is creating all the listing
        User user = new User("huy123", "Huy Nguyen", address, "408-123-4567", "huy.nguyen@sjsu.edu",
                "http://static2.businessinsider.com/image/5899ffcf6e09a897008b5c04-1200/.jpg");


//    public ParkingSpace(Address address, User owner, String parkingImageUrl, String specialInstruction, String startDate, String endDate, int price)
        ParkingSpace p = new ParkingSpace(address, user, "https://media-cdn.tripadvisor.com/media/photo-s/0f/ae/73/2f/private-parking-right.jpg",
                "watch out for dogs", "1/1/2017", "2/2/2017", 5.99);

        parkingSpaces.add(p);
        parkingSpaces.add(p);
        parkingSpaces.add(p);
        parkingSpaces.add(p);
        parkingSpaces.add(p);
    }

}
