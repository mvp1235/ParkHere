package edu.sjsu.team408.parkhere;

import android.Manifest;
import android.app.ListActivity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.*;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.design.widget.Snackbar;
import android.support.v4.app.ActivityCompat;
import android.util.Log;
import android.view.View;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.Toast;

import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * Created by DuocNguyen on 11/1/17.
 */

public class SearchResultActivity extends ListActivity {
    private static final String TAG = SearchResultActivity.class.getSimpleName();
    private static final int REQUEST_PERMISSIONS_REQUEST_CODE = 34;
    private static final String TIME_TAG = "MyActivity";

    static final String LISTING_ID = "listingID";
    static final String PARKING_BUNDLE = "parkingBundle";
    static final String ADDRESS = "address";
    static final String OWNER = "owner";
    static final String PARKING_IMAGE_URL = "parkingImageUrl";
    static final String SPECIAL_INSTRUCTION = "specialInstruction";
    static final String START_DATE = "startDate";
    static final String END_DATE = "endDate";
    static final String PRICE = "price";
    static final String PARKING_ID_REF = "parkingIDRef";
    static final String START_TIME = "startTime";
    static final String END_TIME = "endTime";
    static final String OWNER_PARKING_ID = "OwnerParkingID";
    static final String RESERVE_BY = "reservedBy";
    static final String SEEKER = "seeker";
    static final String SEEKER_BUNDLE = "seekerBundle";
    static final String LATITUDE = "latitude";
    static final String LONGITUDE = "longitude";

    static final int VIEW_DETAIL_PARKING_FROM_RESULT = 101;
    private ArrayList<String> availableParkingSpaces;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private ArrayList<Listing> listings;
    private User currentUser;
    private String parkingIDRef;
    private FusedLocationProviderClient mFusedLocationClient;
    private Location mLocation;
    private boolean userHasDesiredLocation;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_search_result);

        Intent intent = getIntent();
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        availableParkingSpaces = new ArrayList<String>();
        //get user input for location
        final String dateSearchTerm = intent.getStringExtra("date");
        final String locationSearchTerm = intent.getStringExtra("location");
        final String searchTimeTerm = intent.getStringExtra("time");

        if (locationSearchTerm.isEmpty())
            userHasDesiredLocation = false;
        else
            userHasDesiredLocation = true;

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("AvailableParkings").hasChild(dateSearchTerm)) {
                    for(DataSnapshot userIDList: dataSnapshot.child("AvailableParkings")
                            .child(dateSearchTerm).getChildren()) {
                        String p = userIDList.getValue(String.class);
                        availableParkingSpaces.add(p);
                    }
                    searchResult(searchTimeTerm);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //not supported
            }
        });

        /**
         //old search algorithm, this one iterates through all dates in the "AvailableParkings"
         databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
        @Override
        public void onDataChange(DataSnapshot dataSnapshot) {
        long startTime = System.currentTimeMillis(); //seconds
        for(DataSnapshot dates: dataSnapshot.child("AvailableParkings").getChildren()){
        String availableDate = dates.getKey();
        if(availableDate.equals(dateSearchTerm)) {
        for(DataSnapshot userIDList: dataSnapshot.child("AvailableParkings").child(dateSearchTerm).getChildren()){
        String p = userIDList.getValue(String.class);
        availableParkingSpaces.add(p);
        }
        searchResult(searchTimeTerm);
        }
        }
        long endTime = System.currentTimeMillis(); //seconds
        String totalSearchTime = endTime - startTime + " Millis";
        Log.i(TAG, "Total search Time: " + totalSearchTime);
        }

        @Override
        public void onCancelled(DatabaseError databaseError) {

        }
        });
         */

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });



        if (userHasDesiredLocation) {
            // Getting latitude and longitude of an address
            Geocoder geocoder = new Geocoder(this);
            List<android.location.Address> addressList;
            LatLng point = null;
            try {
                addressList = geocoder.getFromLocationName(locationSearchTerm, 5);
                if (addressList.size() > 0) {
                    android.location.Address address = addressList.get(0);
                    address.getLatitude();
                    address.getLongitude();
                    mLocation = new Location("");
                    mLocation.setLatitude(address.getLatitude());
                    mLocation.setLongitude(address.getLongitude());
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), R.string.addressInvalid, Toast.LENGTH_SHORT).show();
            }
        } else // get current location
            mFusedLocationClient = LocationServices.getFusedLocationProviderClient(this);
    }

    @Override
    protected void onStart() {
        super.onStart();
        if (!checkPermissions()) {
            requestPermissions();
        } else if (!userHasDesiredLocation){
            getLastLocation();
        }
    }
    @SuppressWarnings("MissingPermission")
    private void getLastLocation() {
        mFusedLocationClient.getLastLocation()
                .addOnCompleteListener(this, new OnCompleteListener<Location>() {
                    @Override
                    public void onComplete(@NonNull Task<Location> task) {
                        if (task.isSuccessful() && task.getResult() != null) {
                            mLocation = task.getResult();
                        } else {
                            Log.w(TAG, "getLastLocation:exception", task.getException());
                            showSnackbar(getString(R.string.no_location_detected));
                        }
                    }
                });
    }
    private void showSnackbar(final String text) {
        View container = findViewById(R.id.activity_search_result_container);
        if (container != null) {
            Snackbar.make(container, text, Snackbar.LENGTH_LONG).show();
        }
    }
    private void showSnackbar(final int mainTextStringId, final int actionStringId,
                              View.OnClickListener listener) {
        Snackbar.make(findViewById(android.R.id.content),
                getString(mainTextStringId),
                Snackbar.LENGTH_INDEFINITE)
                .setAction(getString(actionStringId), listener).show();
    }
    private boolean checkPermissions() {
        int permissionState = ActivityCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_COARSE_LOCATION);
        return permissionState == PackageManager.PERMISSION_GRANTED;
    }
    private void requestPermissions() {
        boolean shouldProvideRationale =
                ActivityCompat.shouldShowRequestPermissionRationale(this,
                        Manifest.permission.ACCESS_COARSE_LOCATION);

        // Provide an additional rationale to the user. This would happen if the user denied the
        // request previously, but didn't check the "Don't ask again" checkbox.
        if (shouldProvideRationale) {
            Log.i(TAG, "Displaying permission rationale to provide additional context.");

            showSnackbar(R.string.permission_rationale, android.R.string.ok,
                    new View.OnClickListener() {
                        @Override
                        public void onClick(View view) {
                            // Request permission
                            startLocationPermissionRequest();
                        }
                    });

        } else {
            Log.i(TAG, "Requesting permission");
            // Request permission. It's possible this can be auto answered if device policy
            // sets the permission in a given state or the user denied the permission
            // previously and checked "Never ask again".
            startLocationPermissionRequest();
        }
    }
    private void startLocationPermissionRequest() {
        ActivityCompat.requestPermissions(SearchResultActivity.this,
                new String[]{Manifest.permission.ACCESS_COARSE_LOCATION},
                REQUEST_PERMISSIONS_REQUEST_CODE);
    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        Listing parking = (Listing)getListAdapter().getItem(position);
        parkingIDRef = parking.getParkingIDRef();
        Intent intent = new Intent(this, DetailParkingActivity.class);

        Bundle b = new Bundle();
        b.putParcelable(ADDRESS, parking.getAddress());
        b.putParcelable(OWNER, parking.getOwner());
        b.putString(SPECIAL_INSTRUCTION, parking.getSpecialInstruction());
        b.putString(START_DATE, parking.getStartDate());
        b.putString(END_DATE, parking.getEndDate());
        b.putString(START_TIME, parking.getStartTime());
        b.putString(END_TIME, parking.getEndTime());
        b.putDouble(PRICE, parking.getPrice());
        b.putString(PARKING_ID_REF, parking.getParkingIDRef());
        b.putString(PARKING_IMAGE_URL, parking.getParkingImageUrl());
        b.putString(OWNER_PARKING_ID, parking.getOwnerParkingID());
        b.putParcelable(RESERVE_BY, parking.getReservedBy());
        b.putString(LISTING_ID, parking.getId());

        intent.putExtra(LATITUDE, mLocation.getLatitude());
        intent.putExtra(LONGITUDE, mLocation.getLongitude());

        intent.putExtra(PARKING_BUNDLE, b);
        intent.putExtra("requestCode", VIEW_DETAIL_PARKING_FROM_RESULT);
        Bundle seeker = new Bundle();
        seeker.putParcelable(SEEKER, currentUser);
        intent.putExtra(SEEKER_BUNDLE, seeker);
        startActivityForResult(intent, VIEW_DETAIL_PARKING_FROM_RESULT);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if(requestCode == VIEW_DETAIL_PARKING_FROM_RESULT) {
            if (resultCode == RESULT_OK) {
                finish();
                Log.i(TIME_TAG, "Starting time after payment is done : " + System.currentTimeMillis()/1000 + " Seconds");
                Intent intent = new Intent(this, BookingHistoryActivity.class);
                intent.putExtra("parkingIDRef", parkingIDRef);
                startActivity(intent);
            }
        }
    }

    private void searchResult (String searchTimeTerm) {
        listings = new ArrayList<>();  // get the parking spaces.
        if(availableParkingSpaces.size() > 0) {
            for(String available: availableParkingSpaces) {
                String tokens[] = available.split("/"); //[0] contains time, [1] contains parkingID to search database
                final String parkingID = tokens[1];
                String availableTime = tokens[0];   //starthour-startminute-endhour-endminute
                final boolean withinAvailableTime = isWithinAvailableTime(searchTimeTerm, availableTime);
                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        if(withinAvailableTime) {
                            if (dataSnapshot.child("Listings").hasChild(parkingID)) {
                                Listing p = dataSnapshot.child("Listings").child(parkingID).getValue(Listing.class);
                                listings.add(p);
                                showResult();
                            }
                        }
                    }

                    @Override
                    public void onCancelled(DatabaseError databaseError) {

                    }
                });

            }
        }
    }

    private String[] getTimeFormat(String availableTime) {
        String[] results = new String[2];
        String availableTimeTokens[] = availableTime.split(":");
        String startHour = availableTimeTokens[0];
        String startMinute = availableTimeTokens[1];
        String endHour = availableTimeTokens[2];
        String endMinute = availableTimeTokens[3];

        String startAMPM = "AM";
        String endAMPM = "AM";

        int startHourInt = Integer.parseInt(startHour);
        if (startHourInt > 11) {
            startAMPM = "PM";
            if (startHourInt != 12) {
                startHourInt = startHourInt % 12;
                startHour = String.valueOf(startHourInt);
            }

        }
        int endHourInt = Integer.parseInt(endHour);
        if (endHourInt > 11) {
            endAMPM = "PM";
            if (endHourInt != 12) {
                endHourInt = endHourInt % 12;
                endHour = String.valueOf(endHourInt);
            }
        }
        if(Integer.parseInt(startMinute) < 10)
            startMinute = "0" + startMinute;
        if(Integer.parseInt(endMinute) < 10)
            endMinute = "0" + endMinute;

        results[0] = startHour + ":" + startMinute + " " + startAMPM;   //start time
        results[1] = endHour + ":" + endMinute + " " + endAMPM;   //end time

        return results;
    }

    private void showResult() {
        // if a location is specified
        if (mLocation != null) {
            // Create the adapter to convert the array to views
            ListingAdapter adapter = new ListingAdapter(this, listings,
                    mLocation);

            Log.i("TEST", mLocation.toString());

            // Attach the adapter to a ListView
            setListAdapter(adapter);
        } else {
            Toast.makeText(this, R.string.addressInvalid, Toast.LENGTH_SHORT).show();
        }
    }


    private static boolean isWithinAvailableTime(String searchTime, String availableTime) {
        //if no time is specify, we return all results
        if (searchTime.isEmpty())
            return true;

        String availableTimeTokens[] = availableTime.split(":");
        int startHour = Integer.parseInt(availableTimeTokens[0]);
        int startMinute = Integer.parseInt(availableTimeTokens[1]);
        int endHour = Integer.parseInt(availableTimeTokens[2]);
        int endMinute = Integer.parseInt(availableTimeTokens[3]);

        int searchTimeSystem[] = NewListingActivity.get24HoursTimeSystem(searchTime);
        int searchHour = searchTimeSystem[0];
        int searchMinute = searchTimeSystem[1];

        return (searchHour - startHour >= 0) && (searchHour - endHour <= 0);
    }

}