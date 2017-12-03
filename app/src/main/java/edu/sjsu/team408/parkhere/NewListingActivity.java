package edu.sjsu.team408.parkhere;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

public class NewListingActivity extends AppCompatActivity{
    private static final String TAG = NewListingActivity.class.getSimpleName();

    private final static int FROM_DATE = 0;
    private final static int TO_DATE = 1;
    private final static int FROM_TIME = 2;
    private final static int TO_TIME = 3;

    private TextView owner;
    private EditText price, startDate, endDate, startTime, endTime, specialInstructions;
    private TextView addressStreetNumber, addressCity, addressState, addressZipCode;
    private Button saveListingBtn;
    private ImageView listingPhoto;
    private Spinner parkingSpaceSpinner;

    //For picking date of availability
    private Calendar calendar;
    private int year, month, day;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private String userID;

    private static String currentListingIDRef;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing);

        databaseReference = FirebaseDatabase.getInstance().getReference();  //gets database reference
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Get the parking id and use it throughout the activity
        currentListingIDRef = databaseReference.child("AvailableParkings").push().getKey();

        Intent intent = getIntent();

        //Referencing to the UI elements
        owner = (TextView) findViewById(R.id.listingOwner);
        addressStreetNumber = (TextView) findViewById(R.id.listingAddressStreetNumber);
        addressCity = (TextView) findViewById(R.id.listingAddressCity);
        addressState = (TextView) findViewById(R.id.listingAddressState);
        addressZipCode = (TextView) findViewById(R.id.listingAddressZipCode);
        price = (EditText) findViewById(R.id.listingPrice);
        startDate = (EditText) findViewById(R.id.listingStartDate);
        endDate = (EditText) findViewById(R.id.listingEndDate);
        startTime = (EditText) findViewById(R.id.listingStartTime);
        endTime = (EditText) findViewById(R.id.listingEndTime);
        specialInstructions = (EditText) findViewById(R.id.listingSpecialInstructions);
        parkingSpaceSpinner = (Spinner) findViewById(R.id.newListingParkingSpinner);
        listingPhoto = (ImageView) findViewById(R.id.listingPhoto);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            User currentUser = null;
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);
                            ArrayList<String> parkingSpaceIDs = currentUser.getMyParkingSpaces();
                            if (parkingSpaceIDs == null || parkingSpaceIDs.size() == 0) {
                                parkingSpaceIDs = new ArrayList<>();
                                TextView error = findViewById(R.id.pickParkingSpacePrompt);
                                error.setText("No existing parking spaces are found. Please navigate back to profile screen and add a new parking space.");
                                error.setTextColor(Color.RED);
                            }
                            ArrayAdapter<String> adapter;
                            adapter = new ArrayAdapter<String>(getApplicationContext(), android.R.layout.simple_spinner_item, parkingSpaceIDs);
                            parkingSpaceSpinner.setAdapter(adapter);

                            parkingSpaceSpinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
                                @Override
                                public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                                    final String chosenID = parkingSpaceSpinner.getItemAtPosition(position).toString();

                                    //Retrieve parking data from ID and fill the fields
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            if (dataSnapshot.child("ParkingSpaces").hasChild(chosenID)) {
                                                ParkingSpace p = dataSnapshot.child("ParkingSpaces").child(chosenID).getValue(ParkingSpace.class);
                                                addressStreetNumber.setText(p.getAddress().getStreetAddress());
                                                addressCity.setText(p.getAddress().getCity());
                                                addressState.setText(p.getAddress().getState());
                                                addressZipCode.setText(p.getAddress().getZipCode());
                                                specialInstructions.setText(p.getSpecialInstruction());
                                            }
                                        }

                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });

                                    //Retrieve parking space photo on firebase storage if any, otherwise set default image
                                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                        @Override
                                        public void onDataChange(DataSnapshot dataSnapshot) {
                                            storageReference.child("parkingPhotos/" + chosenID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                                                @Override
                                                public void onSuccess(Uri uri) {
                                                    // Got the download URL
                                                    Picasso.with(getApplicationContext()).load(uri.toString()).into(listingPhoto);
                                                }
                                            }).addOnFailureListener(new OnFailureListener() {
                                                @Override
                                                public void onFailure(@NonNull Exception exception) {
                                                    // Handle any errors
                                                    // In this case, load the default parking photo
                                                    Picasso.with(getApplicationContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(listingPhoto);
                                                }
                                            });
                                        }
                                        @Override
                                        public void onCancelled(DatabaseError databaseError) {}
                                    });
                                }

                                @Override
                                public void onNothingSelected(AdapterView<?> parent) {

                                }
                            });
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });



        //For making new listing quicker
//        populateDefaultValuesForTesting();

        saveListingBtn = (Button) findViewById(R.id.saveListingBtn);
        saveListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewListing();
            }
        });


        // setting TextView of owner's name
        databaseReference.child("Users").child(userID).addListenerForSingleValueEvent(
                new ValueEventListener() {
                    @Override
                    public void onDataChange(DataSnapshot dataSnapshot) {
                        User user = dataSnapshot.getValue(User.class);
                        if (user == null) {
                            // User is null, error out
                            Log.e(TAG, "User " + userID + " is unexpectedly null");
                            Toast.makeText(NewListingActivity.this,
                                    "Error: could not fetch user.",
                                    Toast.LENGTH_SHORT).show();
                        } else {
                            owner.setText(user.getName());
                        }
                    }
                    @Override
                    public void onCancelled(DatabaseError databaseError) {
                    }
                }
        );

        //Get the current day, month, and year
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Set on click listeners for the date and time pickers
        startDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(FROM_DATE);
            }
        });
        endDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(TO_DATE);
            }
        });
        startTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(FROM_TIME);
            }
        });
        endTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(TO_TIME);
            }
        });


    }

    /**
     * Take all the user input to create a new listing and update to database
     */
    public void saveNewListing() {
        Intent i = new Intent();
        String streetNumString = addressStreetNumber.getText().toString();
        String cityString = addressCity.getText().toString();
        String stateString = addressState.getText().toString();
        String zipcodeString = addressZipCode.getText().toString();
        String priceString = price.getText().toString();
        final String startDateString = startDate.getText().toString();
        final String endDateString = endDate.getText().toString();
        final String startTimeString = startTime.getText().toString();
        final String endTimeString = endTime.getText().toString();

        //Making sure all fields are filled by user
        if (streetNumString.isEmpty() || cityString.isEmpty() || stateString.isEmpty() || zipcodeString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Address cannot be blank...", Toast.LENGTH_SHORT).show();
        } else if (priceString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Price cannot be blank...", Toast.LENGTH_SHORT).show();
        } else if (startDateString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Start date cannot be blank...", Toast.LENGTH_SHORT).show();
        } else if (endDateString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "End date cannot be blank...", Toast.LENGTH_SHORT).show();
        } else if (startTimeString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Start time cannot be blank...", Toast.LENGTH_SHORT).show();
        } else if (endTimeString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "End time cannot be blank...", Toast.LENGTH_SHORT).show();
        } else {
            i.putExtra("streetNum", streetNumString);
            i.putExtra("city", cityString);
            i.putExtra("state", startDateString);
            i.putExtra("zipcode", zipcodeString);
            i.putExtra("price", Double.parseDouble(priceString));
            i.putExtra("startDate", startDateString);
            i.putExtra("endDate", endDateString);


            ///////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Robin's geocoding code
            // for some reason, it doesn't work properly for all addresses when using on emulator, but is perfect on my Samsung S8
            // I went ahead and used the Google Geocoding API instead, which works fine for all devices
            // for better development since you guys don't own an android device, let's disable this and use my part for now.
            // Getting latitude and longitude of an address

            //From here
//            Geocoder geocoder = new Geocoder(this,  Locale.getDefault());
//            List<android.location.Address> addressList;
//            LatLng point = null;
//
//            try {
//                String addressString = streetNumString + ", " + cityString + ", "
//                        + stateString;
//                Log.i("TEST", addressString);
//                addressList = geocoder.getFromLocationName(addressString, 1);
//                if (addressList.size() > 0) {
//                    android.location.Address location = addressList.get(0);
//                    location.getLatitude();
//                    location.getLongitude();
//                    point = new LatLng(location.getLatitude(), location.getLongitude());
//                    Log.i("TEST", location.toString() + " dsddsdsd");
//                }
//            } catch (IOException e) {
//                Toast.makeText(getApplicationContext(), R.string.addressInvalid,
//                        Toast.LENGTH_SHORT).show();
//                Log.i("TEST", "ERROR MAKING LISTING");
//            }
//            addListingToDatabaseNew(startDateString, endDateString, startTimeString, endTimeString, point);
            //Till here
            /////////////////////////////////////////////////////////////////////////////////////////////////////


            //Huy's replacement
            /////////////////////////////////////////////////////////////////////////////////////////////////////
            //FROM HERE
            //Let's use this version to prevent the problem we've been facing, i.e. it only works for certain address entered
            String addressString = streetNumString + ", " + cityString + ", "
                    + stateString;
            String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                    + Uri.encode(addressString) + "&sensor=true&key=AIzaSyBqgv8PrGCSFVa-Nb2ymE3gGnuv-LgfGps";   //using my own API key here, 2,500 free request per day,
                                                                                                                // which should be fine for development now
            RequestQueue queue = Volley.newRequestQueue(this);
            JsonObjectRequest stateReq = new JsonObjectRequest(Request.Method.GET, url, null, new Response.Listener<JSONObject>() {
                @Override
                public void onResponse(JSONObject response) {
                    JSONObject location;
                    try {
                        // Get JSON Array called "results" and then get the 0th
                        // complete object as JSON
                        Log.i("TEST", response.toString());
                        location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                        // Get the value of the attribute whose name is
                        // "formatted_string"
                        if (location.getDouble("lat") != 0 && location.getDouble("lng") != 0) {
                            LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                            addListingToDatabaseNew(startDateString, endDateString, startTimeString, endTimeString, latLng);
                            //Do what you want
                        }
                    } catch (JSONException e1) {
                        e1.printStackTrace();

                    }
                }

            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError error) {
                    Log.d("Error.Response", error.toString());
                }
            });
            // add it to the queue
            queue.add(stateReq);

            //TILL HERE
            /////////////////////////////////////////////


            setResult(RESULT_OK, i);
            finish();
        }

    }

    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        //Depending on which element ID was called, the appropriate date/time picker will be displayed to the user
        switch (id) {
            case FROM_DATE:
                return new DatePickerDialog(this, startDateListener, year, month, day);
            case TO_DATE:
                return new DatePickerDialog(this, endDateListener, year, month, day);
            case FROM_TIME:
                return new TimePickerDialog(this, startTimeListener, 0, 0,false);
            case TO_TIME:
                return new TimePickerDialog(this, endTimeListener, 0, 0,false);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener startDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    setStartDate(arg1, arg2+1, arg3);
                }
            };

    private DatePickerDialog.OnDateSetListener endDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    setEndDate(arg1, arg2+1, arg3);
                }
            };

    private TimePickerDialog.OnTimeSetListener startTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view,int hourofday,int min){
            setStartTime(hourofday, min);
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view,int hourofday,int min){
            setEndTime(hourofday, min);
        }
    };

    public void setStartTime(int hour, int minute) {
        String hourString, minuteString;
        String ampm = "AM";

        if (hour > 12) {
            hour = hour % 12;
            ampm = "PM";
        } else if (hour == 12) {
            ampm = "PM";
        }

        hourString = Integer.toString(hour);
        minuteString = (minute < 10) ? "0" + Integer.toString(minute) : Integer.toString(minute);
        String completeTime = hourString + ":" + minuteString + " " + ampm;

        startTime.setText(completeTime);
    }

    public void setEndTime(int hour, int minute) {
        String hourString, minuteString;
        String ampm = "AM";

        if (hour > 12) {
            hour = hour % 12;
            ampm = "PM";
        } else if (hour == 12) {
            ampm = "PM";
        }

        hourString = Integer.toString(hour);
        minuteString = (minute < 10) ? "0" + Integer.toString(minute) : Integer.toString(minute);
        String completeTime = hourString + ":" + minuteString + " " + ampm;

        endTime.setText(completeTime);
    }

    public void setStartDate(int year, int month, int day) {
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = Integer.toString(month);
        dayString = Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        startDate.setText(completeDate);
    }

    public void setEndDate(int year, int month, int day) {
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = Integer.toString(month);
        dayString = Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        endDate.setText(completeDate);
    }



    /**
     * Adds listing to database more efficient way.
     * @param startDate start date
     * @param endDate   end date
     * @param startTime start time
     * @param endTime   end time
     */
    public void addListingToDatabaseNew(String startDate, String endDate, String startTime,
                                     String endTime, LatLng point) {
        String startDateList[] = startDate.split("-");
        String endDateList[] = endDate.split("-");
        int startTimeSystem[] = get24HoursTimeSystem(startTime);
        int endTimeSystem[] = get24HoursTimeSystem(endTime);

        int starthour = startTimeSystem[0];
        int startMinutes = startTimeSystem[1];
        int endHour = endTimeSystem[0];
        int endMinutes = endTimeSystem[1];


        int startMonth = Integer.parseInt(startDateList[0]);
        int startDay = Integer.parseInt(startDateList[1]);
        int startYear = Integer.parseInt(startDateList[2]);

        int endMonth = Integer.parseInt(endDateList[0]);
        int endDay = Integer.parseInt(endDateList[1]);
        int endYear = Integer.parseInt(endDateList[2]);

        GregorianCalendar startDateCalendar = new GregorianCalendar(startYear, startMonth - 1, startDay);
        GregorianCalendar endDateCalendar = new GregorianCalendar(endYear, endMonth - 1, endDay);


        String owner = this.owner.getText().toString();
        String price = this.price.getText().toString();
        String address = addressStreetNumber.getText().toString() + ", " + addressCity.getText().toString()
                + ", " + addressState.getText().toString() + " " + addressZipCode.getText().toString();

        String dataValue = starthour + ":" + startMinutes + ":" + endHour + ":" + endMinutes + "/" + currentListingIDRef; //starthour-startminutes-endhour-endminutes-currentParkingID
        String parentKey;

        String pID = parkingSpaceSpinner.getSelectedItem().toString();
        Listing parking = getParkingSpace(startDate, endDate, startTime, endTime,userID, owner, price, address, point, pID);
        String ownerParkingID = userID;
        parking.setOwnerParkingID(ownerParkingID);
        //Adding special instruction to parking (if any is provided)
        String specialIns = specialInstructions.getText().toString();
        parking.setSpecialInstruction(specialIns);


        String childKey;
//        parkingSpaceUidKey = databaseReference.child("AvailableParkings").push().getKey();    // no longer using these, gonna use the member variable currentParkingIDRef

        while(!startDateCalendar.equals(endDateCalendar)) {
            int currentMonth = startDateCalendar.get(Calendar.MONTH) + 1 ;
            int currentYear = startDateCalendar.get(Calendar.YEAR);
            int currentDay = startDateCalendar.get(Calendar.DAY_OF_MONTH);

            String currentDate = currentMonth + "-" + currentDay + "-" + currentYear;

            childKey = currentListingIDRef;
            parentKey = currentDate;

            databaseReference.child("AvailableParkings").child(parentKey).child(childKey).setValue(dataValue); //add listing to database

            startDateCalendar.add(Calendar.DAY_OF_MONTH, 1); //increment
        }
        if(startDateCalendar.equals(endDateCalendar)) {
            int currentMonth = startDateCalendar.get(Calendar.MONTH) + 1 ;
            int currentYear = startDateCalendar.get(Calendar.YEAR);
            int currentDay = startDateCalendar.get(Calendar.DAY_OF_MONTH);

            String currentDate = currentMonth + "-" + currentDay + "-" + currentYear;

            childKey = currentListingIDRef;
            parentKey = currentDate;

            databaseReference.child("AvailableParkings").child(parentKey).child(childKey).setValue(dataValue); //add listing to database
        }
        addListingToUser(parking);

        databaseReference.child("Listings").child(currentListingIDRef).setValue(parking);
    }


    /**
     * Converts time string into 24 hours time system
     * @param time
     * @return
     */
    public static int[] get24HoursTimeSystem(String time) {
        int result[] = new int[2];    // out of bounds. Check if it's 25 that means it's wrong.
        String timeList[] = time.split(":");
        int hour = Integer.parseInt(timeList[0]);
        int minutes = Integer.parseInt(timeList[1].substring(0,2));
        String hourSystem = timeList[1].substring(timeList[1].length()-2, timeList[1].length());    //AM or PM
        if(hourSystem.equals("PM") && hour != 12) {
            hour = hour + 12;
        }
        result[0] = Integer.parseInt(hour + "");
        result[1] = Integer.parseInt(minutes + "");
        return result;
    }


    //This method contains parkingID
    public static Listing getParkingSpace(String startDate, String endDate, String startTime,
                                          String endTime, String userID, String ownerName,
                                          String price, String address, LatLng point, String parkingID) {
        if(startDate.isEmpty() || endDate.isEmpty() || startTime.isEmpty() || endTime.isEmpty() ||
                userID.isEmpty() || price.isEmpty() || address.isEmpty()) {
            return null;
        }
        //String result ="";
        //result +=  startDate + ":" + endDate + ":" + startTime + ":" + endTime + ":" + ownerName + ":" + userID + ":" + price + ":" + address;
        Address addr = new Address(address, point);    //correct format later
        User owner = new User(userID+"", ownerName, null,null,null,null);
        String parkingImageUrl = "https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png";   //default for testing

        String specialInstruction = "";


        //return result;
        return new Listing(currentListingIDRef, addr, owner, parkingImageUrl, specialInstruction, startDate, endDate, startTime, endTime ,Double.parseDouble(price), parkingID);
    }

    private void addListingToUser(Listing ps) {
        final ArrayList<Listing> newList = new ArrayList<Listing>();
        newList.add(ps);
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            User currentUser = null;
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);
                            currentUser.addToListingHistory(newList);
                            databaseReference.child("Users").child(currentUser.getId()).setValue(currentUser);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    public static Listing getValue(String startDate, String endDate, String startTime,
                                   String endTime, String userID, String ownerName, String price,
                                   String address, LatLng point, String parkingID) {
        Listing p = new Listing();

        p.setStartDate(startDate);
        p.setEndDate(endDate);
        p.setStartDate(startTime);
        p.setEndTime(endTime);
        p.setPrice(Double.parseDouble(price));
        p.setAddress(new Address(address, point));
        p.setParkingIDRef(parkingID);

        return p;
    }

}
