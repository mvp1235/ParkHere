package edu.sjsu.team408.parkhere;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.TimePicker;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

import org.w3c.dom.Text;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

public class DetailParkingActivity extends AppCompatActivity {

    private final static int FROM_DATE = 0;
    private final static int TO_DATE = 1;
    private final static int FROM_TIME = 2;
    private final static int TO_TIME = 3;

    private final static int LISTING_EDIT_CODE = 10;

    private TextView addressTV, ownerTV, specialInstructionTV, dateTV, priceTV;
    private ImageView parkingPhoto;
    private Button reserveBtn, editBtn;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private ParkingSpace clickedParking;
    private User currentUser;
    private static String parkingPhotoString = "https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png";
    private Uri parkingURI = null;
    private TextView reserveToTime, reserveFromDate, reserveToDate, reserveFromTime;
    private Calendar calendar;
    private int year, month, day;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_parking);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Reference to the UI elements
        addressTV = (TextView) findViewById(R.id.detailParkingAddress);
        ownerTV = (TextView) findViewById(R.id.detailParkingOwner);
        specialInstructionTV = (TextView) findViewById(R.id.detailParkingSpecialInstruction);
        dateTV = (TextView) findViewById(R.id.detailParkingDate);
        priceTV = (TextView) findViewById(R.id.detailParkingPrice);
        parkingPhoto = (ImageView) findViewById(R.id.detailParkingPhoto);
        reserveBtn = (Button) findViewById(R.id.reserveBtn);
        editBtn = (Button) findViewById(R.id.detailEditBtn);

        //seeker set reservation from date to date, from time to time.
        reserveFromDate = (TextView) findViewById(R.id.reserveFromDateTV);
        reserveToDate = (TextView) findViewById(R.id.reserveToDateTV);
        reserveFromTime = (TextView) findViewById(R.id.reserveFromTimeTV);
        reserveToTime = (TextView) findViewById(R.id.reserveToTimeTV);

        //Get the current day, month, and year
        calendar = Calendar.getInstance();
        year = calendar.get(Calendar.YEAR);
        month = calendar.get(Calendar.MONTH);
        day = calendar.get(Calendar.DAY_OF_MONTH);

        //Set on click listeners for the date and time pickers
        reserveFromDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(FROM_DATE);
            }
        });
        reserveToDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(TO_DATE);
            }
        });
        reserveFromTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(FROM_TIME);
            }
        });
        reserveToTime.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                showDialog(TO_TIME);
            }
        });





        Intent intent = getIntent();

        Bundle bundle = intent.getBundleExtra(SearchResultActivity.PARKING_BUNDLE);

        clickedParking = new ParkingSpace(bundle);

        setParkingPhoto(clickedParking.getParkingID());

        addressTV.setText(clickedParking.getAddress().toString());      //crashes here
        ownerTV.setText(clickedParking.getOwner().getName());
        specialInstructionTV.setText(clickedParking.getSpecialInstruction());

        //If start date and end date are the same, only display the start date
        //otherwise, have it in the format of "startDate - endDate"
        String availableDate = clickedParking.getStartDate();
        if (!clickedParking.getStartDate().equalsIgnoreCase(clickedParking.getEndDate())) {
            availableDate += " - " + clickedParking.getEndDate();
        }
        String availableTime = " From " + clickedParking.getStartTime() + " to " + clickedParking.getEndTime();

        dateTV.setText(availableDate + availableTime);

        priceTV.setText("$" + String.valueOf(clickedParking.getPrice()));

        int request = intent.getIntExtra("requestCode", 0);

        //Add reservation functionality to reserve button only when the detailed page was requested from search result page
        if (request == SearchResultActivity.VIEW_DETAIL_PARKING_FROM_RESULT) {
            reserveBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    makeReservation();    //Duoc -- i will continue fixing this part tmr.
                }
            });
        }

        // Set appropriate text for button
        if (request == SearchResultActivity.VIEW_DETAIL_PARKING_FROM_RESULT) {
            reserveBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);       // only show edit button on listing history
            reserveBtn.setText("Reserve");
        } else if (request == BookingHistoryActivity.VIEW_DETAIL_HISTORY_BOOKING_) {
            reserveBtn.setVisibility(View.GONE);    //book again should be taken out since it depends on the listing owner, i.e. you can't really book again if it's not up for listing
            editBtn.setVisibility(View.GONE);       // only show edit button on listing history

//            reserveBtn.setText("Book Again");

        } else if (request == ListingHistoryActivity.VIEW_DETAIL_HISTORY_LISTING) {
            reserveBtn.setText("List Again");     //list again will take user to the new listing activity, with all data pre-filled, and allow user to pick another data and time
                                                    //To be implemented later

            //An edit button will be shown on this screen as well to allow user to edit the listings he/she posted
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editListing();
                }
            });

        }

        //Hide distance if user is checking history
        LinearLayout ll = findViewById(R.id.detailParkingDistanceLL);
        if (request == BookingHistoryActivity.VIEW_DETAIL_HISTORY_BOOKING_ || request == ListingHistoryActivity.VIEW_DETAIL_HISTORY_LISTING) {
            ll.setVisibility(View.GONE);
        } else {
            ll.setVisibility(View.VISIBLE);
        }
    }

    private void editListing() {
        Intent intent = new Intent(DetailParkingActivity.this, EditListingActivity.class);

        ArrayList<String> parsedAddress = parseAddress(addressTV.getText().toString());
        intent.putExtra("streetAddress", parsedAddress.get(0));
        intent.putExtra("city", parsedAddress.get(1));
        intent.putExtra("state", parsedAddress.get(2));
        intent.putExtra("zipCode", parsedAddress.get(3));
        intent.putExtra("owner", ownerTV.getText().toString());
        intent.putExtra("price", priceTV.getText().toString().substring(1)); //get rid of substring before sending over intent to edit listing activity
        intent.putExtra("specialInstructions", specialInstructionTV.getText().toString());
        intent.putExtra("parkingID", clickedParking.getParkingID());

        startActivityForResult(intent, LISTING_EDIT_CODE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LISTING_EDIT_CODE && resultCode == RESULT_OK) {      //take input from listing editting page and reflect changes to database here
            finish();
        }
    }

    /**
     * Parse an address into separate fields, such as street number, city, state, zipCode
     * @param fullAddress the adddress in full format (1 Washington Square, San Jose, CA 95112)
     * @return An arraylist containing all the fields. [0] for street number, [1] for city, [2] for state, and [3] for zip code
     */
    private ArrayList<String> parseAddress(String fullAddress) {
        ArrayList<String> result = new ArrayList<>();
        String streetNumber, city, state, zipCode;
        String[] splits = fullAddress.split(",");

        streetNumber = splits[0].trim();
        city = splits[1].trim();

        String stateZipCode = splits[2].trim(); // get rid of preceding and proceding spaces first
        state = stateZipCode.split(" ")[0];
        zipCode = stateZipCode.split(" ")[1];

        result.add(streetNumber);
        result.add(city);
        result.add(state);
        result.add(zipCode);
        return result;
    }

    /**
     * Get the parking URL for a listing with a certain id
     * @param parkingID the id of the listing to be retrieved from database
     * @return the default url of the parking photo if not set, the encoded string of bitmap if the user has set one photo for the listing
     */
    public void setParkingPhoto(final String parkingID) {
        storageReference.child("parkingPhotos/" + parkingID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                //Parking photo is set by user
                Picasso.with(getApplicationContext()).load(uri.toString()).into(parkingPhoto);
                Log.i("SET PARKING PHOTO", "Success");
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Handle any errors
                //Parking photo is the default one, user has not set a photo for the listing yet
                Picasso.with(getApplicationContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(parkingPhoto);
                Log.i("SET PARKING PHOTO", "Fail");
            }
        });

    }

    /**
     * Implement reservation functionality here
     */
    public void makeReservation() {
        final String parkingID = clickedParking.getParkingID();

        //here add the reserved parking to user's myCurrentReservedParkings lists

        //0. Split the parking space depending on seeker's booking rage.
        //1. Delete reference from AvailableParkings
        //2. Delete parking from Listings then split it.
        //3. Take seeker's desire booking range and reserve the space.
        //4. Update seeker's object in database.
        //5. Update owner that they have reserved which portion of the available time.
        //6. add the splitted parking space to database if there are remainding ones.

        ParkingSpace[] spaces = splitParkingSpace(clickedParking);  //0.
        final ParkingSpace parkingSpaceToBook = spaces[0];
        ParkingSpace parkingSpaceSplit1 = spaces[1];    //to be added back to database
        ParkingSpace parkingSpaceSplit2 = spaces[2];    //to be added back to database
        deleteParkingReference(clickedParking);     //1.
        deleteParkingListing(parkingID);        //2.

        addSplittedParkingsToDatabase(parkingSpaceSplit1, parkingSpaceSplit2);  //6

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);
                            currentUser.addReservedParking(parkingSpaceToBook);     //3.
                            String currentUserID = currentUser.getId();
                            databaseReference.child("Users").child(currentUserID).setValue(currentUser); //4.
                            notifyOwner(parkingSpaceToBook);  //5.


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
    public ParkingSpace[] splitParkingSpace(ParkingSpace clickedParking) {
        ParkingSpace[] splitted = new ParkingSpace[3];      //[0] ps to book. [1]&[2] splitted ps
        splitted[0] = clickedParking;

        //for now just return the retire click parking space. Will implement split later.

        return splitted;
    }

    public void deleteParkingReference(ParkingSpace clickedParking) {
        final String parkingID = clickedParking.getParkingID();
        String startDate = clickedParking.getStartDate();
        String endDate = clickedParking.getEndDate();

        final GregorianCalendar startDateRef = getGregorianCalendar(startDate);
        final GregorianCalendar endDateRef = getGregorianCalendar(endDate);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                while(!startDateRef.equals(endDateRef)) {
                    String dateRef = getDate(startDateRef);
                    if (dataSnapshot.child("AvailableParkings").hasChild(dateRef)) {
                        if(dataSnapshot.child("AvailableParkings").child(dateRef).hasChild(parkingID)) {
                            databaseReference.child("AvailableParkings").child(dateRef).child(parkingID).removeValue();
                        }
                    }
                    startDateRef.add(Calendar.DAY_OF_MONTH, 1);     //increment
                }
                if(startDateRef.equals(endDateRef)) {
                    String dateRef = getDate(startDateRef);
                    if (dataSnapshot.child("AvailableParkings").hasChild(dateRef)) {
                        if(dataSnapshot.child("AvailableParkings").child(dateRef).hasChild(parkingID)) {
                            databaseReference.child("AvailableParkings").child(dateRef).child(parkingID).removeValue();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void deleteParkingListing(final String parkingID) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Listings").hasChild(parkingID)) {
                    databaseReference.child("Listings").child(parkingID).removeValue();
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }

    public void notifyOwner(ParkingSpace bookedParkingSpace) {

    }

    public void addSplittedParkingsToDatabase(ParkingSpace p1, ParkingSpace p2) {

    }

    public String getDate(GregorianCalendar g) {
        int month = g.get(Calendar.MONTH) + 1;
        int day = g.get(Calendar.DAY_OF_MONTH);
        int year = g.get(Calendar.YEAR);
        return month + "-" + day + "-" + year;
    }

    public GregorianCalendar getGregorianCalendar(String date) {
        String[] tokens = date.split("-");
        int month = Integer.parseInt(tokens[0]);
        int day = Integer.parseInt(tokens[1]);
        int year = Integer.parseInt(tokens[2]);
        return new GregorianCalendar(year, month -1 , day);
    }






    @Override
    protected Dialog onCreateDialog(int id) {
        // TODO Auto-generated method stub

        //Depending on which element ID was called, the appropriate date/time picker will be displayed to the user
        switch (id) {
            case FROM_DATE:
                return new DatePickerDialog(this, fromDateListener, year, month, day);
            case TO_DATE:
                return new DatePickerDialog(this, toDateListener, year, month, day);
            case FROM_TIME:
                return new TimePickerDialog(this, fromTimeListener, 12, 0,false);
            case TO_TIME:
                return new TimePickerDialog(this, toTimeListener, 12, 0,false);
        }
        return null;
    }

    private DatePickerDialog.OnDateSetListener fromDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    setReserveFromDate(arg1, arg2+1, arg3);
                }
            };

    private DatePickerDialog.OnDateSetListener toDateListener = new
            DatePickerDialog.OnDateSetListener() {
                @Override
                public void onDateSet(DatePicker arg0,
                                      int arg1, int arg2, int arg3) {
                    // TODO Auto-generated method stub
                    // arg1 = year
                    // arg2 = month
                    // arg3 = day
                    setReserveToDate(arg1, arg2+1, arg3);
                }
            };

    private TimePickerDialog.OnTimeSetListener fromTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view,int hourofday,int min){
            setReserveFromTime(hourofday, min);
        }
    };

    private TimePickerDialog.OnTimeSetListener toTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view,int hourofday,int min){
            setReserveToTime(hourofday, min);
        }
    };

    public void setReserveFromTime(int hour, int minute) {
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

        reserveFromTime.setText("From Time: " + completeTime);
    }

    public void setReserveToTime(int hour, int minute) {
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

        reserveToTime.setText("To Time: " + completeTime);
    }

    public void setReserveFromDate(int year, int month, int day) {
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = (month < 10) ? "0" + Integer.toString(month) : Integer.toString(month);
        dayString = (day < 10) ? "0" + Integer.toString(day) : Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        reserveFromDate.setText("From Date: " +completeDate);
    }

    public void setReserveToDate(int year, int month, int day) {
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = (month < 10) ? "0" + Integer.toString(month) : Integer.toString(month);
        dayString = (day < 10) ? "0" + Integer.toString(day) : Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        reserveToDate.setText("To Date: " + completeDate);
    }


}