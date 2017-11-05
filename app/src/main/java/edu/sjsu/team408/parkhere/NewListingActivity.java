package edu.sjsu.team408.parkhere;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.*;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.ValueEventListener;

import static edu.sjsu.team408.parkhere.MainActivity.mAuth;

public class NewListingActivity extends AppCompatActivity {

    private final static int FROM_DATE = 0;
    private final static int TO_DATE = 1;
    private final static int FROM_TIME = 2;
    private final static int TO_TIME = 3;

    private TextView owner;
    private EditText addressStreetNumber, addressCity, addressState, addressZipCode, price, startDate, endDate, startTime, endTime;
    private Button saveListingBtn;

    //For picking date of availability
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing);

        databaseReference = FirebaseDatabase.getInstance().getReference();  //gets database reference
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        Intent intent = getIntent();

        //Referencing to the UI elements
        owner = (TextView) findViewById(R.id.listingOwner);
        addressStreetNumber = (EditText) findViewById(R.id.listingAddressStreetNumber);
        addressCity = (EditText) findViewById(R.id.listingAddressCity);
        addressState = (EditText) findViewById(R.id.listingAddressState);
        addressZipCode = (EditText) findViewById(R.id.listingAddressZipCode);
        price = (EditText) findViewById(R.id.listingPrice);
        startDate = (EditText) findViewById(R.id.listingStartDate);
        endDate = (EditText) findViewById(R.id.listingEndDate);
        startTime = (EditText) findViewById(R.id.listingStartTime);
        endTime = (EditText) findViewById(R.id.listingEndTime);

        //For making new listing quicker
        populateDefaultValuesForTesting();

        saveListingBtn = (Button) findViewById(R.id.saveListingBtn);
        saveListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewListing();
            }
        });

        owner.setText(userID);

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
        String startDateString = startDate.getText().toString();
        String endDateString = endDate.getText().toString();
        String startTimeString = startTime.getText().toString();
        String endTimeString = endTime.getText().toString();

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



            Geocoder geocoder = new Geocoder(this);
            List<android.location.Address> addressList;
            LatLng point = null;

            try {
                String addressString = streetNumString + ", " + cityString + ", " + stateString + " " + zipcodeString;
                addressList = geocoder.getFromLocationName(addressString, 5);
                if (addressList.size() > 0) {
                    android.location.Address location = addressList.get(0);
                    location.getLatitude();
                    location.getLongitude();
                    point = new LatLng(location.getLatitude(), location.getLongitude());
                }
            } catch (IOException e) {
                Toast.makeText(getApplicationContext(), "The address is invalid, please try again", Toast.LENGTH_SHORT).show();
            }

            addListingToDatabase(startDateString, endDateString, startTimeString, endTimeString, point);
//            addListingToDatabase(startDateString, endDateString, startTimeString, endTimeString);

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
        } else if (hour == 0) {
            hour = 12;
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
        } else if (hour == 0) {
            hour = 12;
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
        monthString = (month < 10) ? "0" + Integer.toString(month) : Integer.toString(month);
        dayString = (day < 10) ? "0" + Integer.toString(day) : Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        startDate.setText(completeDate);
    }

    public void setEndDate(int year, int month, int day) {
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = (month < 10) ? "0" + Integer.toString(month) : Integer.toString(month);
        dayString = (day < 10) ? "0" + Integer.toString(day) : Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        endDate.setText(completeDate);
    }

    /**
     * Adds listing to database
     * @param startDate start date
     * @param endDate   end date
     * @param startTime start time
     * @param endTime   end time
     */
    public void addListingToDatabase(String startDate, String endDate, String startTime, String endTime, LatLng point) {
        String startDateList[] = startDate.split("-");
        String endDateList[] = endDate.split("-");
        ArrayList<ParkingSpace> listOfParkings = new ArrayList<>();

        int startMonth = Integer.parseInt(startDateList[0]);
        int startDay = Integer.parseInt(startDateList[1]);
        int startYear = Integer.parseInt(startDateList[2]);

        int endMonth = Integer.parseInt(endDateList[0]);
        int endDay = Integer.parseInt(endDateList[1]);
        int endYear = Integer.parseInt(endDateList[2]);

        String parentKey = "";
        String parkingSpaceUidKey = "";
        ParkingSpace dataValue = null;

        parkingSpaceUidKey = FirebaseDatabase.getInstance().getReference()


        //i'll clean up code later....
        //for now restriction is owner can post 1 listing per day.
        //doesn't support same day different time listing yet. Ex available 2-3 and 5-6. Will implement later
        if(endYear - startYear == 0) {
            //same year
            if(endMonth - startMonth == 0) {
                //same month
                if(endDay - startDay == 0) {

                    //same day, just add one key and value to database
                    String owner = this.owner.getText().toString();
                    String price = this.price.getText().toString();
                    //combine separate fields into full address
                    String address = addressStreetNumber.getText().toString() + ", " + addressCity.getText().toString()
                            + ", " + addressState.getText().toString() + " " + addressZipCode.getText().toString();
                    dataValue = getValue(startDate, endDate, startTime, endTime, this.userID, owner, price, address);
                    parentKey = startDate;
                    databaseReference.child("AvailableParkings").child(parentKey).child(parkingSpaceUidKey).setValue(dataValue); //add listing to database
                    listOfParkings.add(dataValue);
                } else {
                    //more than one day in the same month
                    int i = endDay - startDay;
                    String owner = this.owner.getText().toString();
                    String price = this.price.getText().toString();
                    String address = addressStreetNumber.getText().toString() + ", " + addressCity.getText().toString()
                            + ", " + addressState.getText().toString() + " " + addressZipCode.getText().toString();
                    while(i >= 0) {
                        int newStartDayInt = startDay + i;
                        int newEndDayInt = endDay;
                        //if(i != 0) {
                          //  newEndDayInt += i;
                        //}
                        String newStartDate = startMonth + "-" + newStartDayInt + "-" + startYear;
                        //String newEndDate = endMonth + "-" + newEndDayInt + "-" + endYear;
                        String newEndDate = newStartDate;  //making start date and end date the same.

                        if(newStartDayInt < 10){
                            newStartDate = startMonth + "-0" + newStartDayInt + "-" + startYear;
                        }
                       // if(newEndDayInt < 10) {
                        //    newEndDate = endMonth + "-0" + newEndDayInt + "-" + endYear;
                       // }

                        dataValue = getValue(newStartDate, newEndDate, startTime, endTime, this.userID, owner, price, address);
                        parentKey = newStartDate;
                        databaseReference.child("AvailableParkings").child(parentKey).child(parkingSpaceUidKey).setValue(dataValue); //add listing to database
                        databaseReference.child("AvailableParkings").child(parentKey).child(parkingSpaceUidKey).setValue(dataValue); //add listing to database
                        listOfParkings.add(dataValue);
                        i--;
                    }

                }
            } else {
                //not within the same month
            }
        } else {
            //available more than 1 year....
        }
        //userID = 4;   //we will fix this later.... purpose is to simulate different userID for every listing //this doesn't change value for testing
        //*******   Process so far: Complete listing for one day and multiple days in a row.
        //technique - making start day as parent key and userID for child key.
        //suppose we have 2 owners put up listing for same day. We can still differentiate them by userID child key.

        addListingToUser(listOfParkings);


    }

    private static ParkingSpace getValue(String startDate, String endDate, String startTime,
                                         String endTime, String userID, String ownerName,
                                         String price, String address) {
        //String result ="";
        //result +=  startDate + ":" + endDate + ":" + startTime + ":" + endTime + ":" + ownerName + ":" + userID + ":" + price + ":" + address;
        edu.sjsu.team408.parkhere.Address addr = new edu.sjsu.team408.parkhere.Address(address);    //correct format later
        User owner = new User(userID+"", ownerName, null,null,null,null);
        String parkingImageUrl = "https://media-cdn.tripadvisor.com/media/photo-s/0f/ae/73/2f/private-parking-right.jpg";   //default for testing
        String specialInstruction = "";


        //return result;
        return new ParkingSpace(addr, owner, parkingImageUrl, specialInstruction, startDate, endDate, Double.parseDouble(price));
    }

    public void populateDefaultValuesForTesting() {
        addressStreetNumber.setText("1 Washington Square");
        addressCity.setText("San Jose");
        addressState.setText("CA");
        addressZipCode.setText("95112");
        price.setText("5.0");
        startDate.setText("11-10-2017");
        endDate.setText("11-10-2017");
        startTime.setText("5:00 PM");
        endTime.setText("10:00 PM");

    }


    private void addListingToUser(ArrayList<ParkingSpace> list) {
        final ArrayList<ParkingSpace> newList = list;
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("asd", "asdfa");
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
}
