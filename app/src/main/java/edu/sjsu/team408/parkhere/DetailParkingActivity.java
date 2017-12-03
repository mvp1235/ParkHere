package edu.sjsu.team408.parkhere;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.location.Location;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.TimePicker;
import android.widget.Toast;

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

import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;
import java.util.Locale;

public class DetailParkingActivity extends AppCompatActivity {

    private static final double MILES_TO_METER = 1609.344;
    private final static int FROM_DATE = 0;
    private final static int TO_DATE = 1;
    private final static int FROM_TIME = 2;
    private final static int TO_TIME = 3;

    private final static int LISTING_EDIT_CODE = 10;
    private final static int RESERVATION_LIST_VIEW_CODE = 11;
    private static final int WRITE_REVIEW_CODE = 11;

    private TextView addressTV, ownerTV, specialInstructionTV, dateTV, priceTV, distanceTV, seekerLabel, seekerPhoneLabel, seekerEmailLabel, reviewCount;
    private TextView priceLabel, distanceLabel, specialInstructionLabel;
    private ImageView parkingPhoto;
    private Button reserveBtn, editBtn, reserveListBtn, reviewBtn, deleteBtn;
    private RatingBar ratingBar;
    private LinearLayout ratingLL;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private Listing clickedParking;
    private User currentUser;
    private TextView reserveToTime, reserveFromDate, reserveToDate, reserveFromTime;
    private Calendar calendar;
    private int year, month, day;
    private Listing listingToBook;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_parking);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();

        //Reference to the UI elements
        seekerLabel = (TextView) findViewById(R.id.detailOwnerLabel);
        seekerPhoneLabel = (TextView) findViewById(R.id.detailAddressLabel);
        seekerEmailLabel = (TextView) findViewById(R.id.detailDateLabel);
        priceLabel = (TextView) findViewById(R.id.detailPriceLabel);
        distanceTV = (TextView) findViewById(R.id.detailParkingDistance);
        distanceLabel = (TextView) findViewById(R.id.detailDistanceLabel);
        specialInstructionLabel = (TextView) findViewById(R.id.detailSpecialInstructionLabel);
        ratingBar = (RatingBar) findViewById(R.id.detailParkingRatingBar);
        reviewCount = (TextView) findViewById(R.id.detailParkingTotalReviews);
        addressTV = (TextView) findViewById(R.id.detailParkingAddress);
        ownerTV = (TextView) findViewById(R.id.detailParkingOwner);
        specialInstructionTV = (TextView) findViewById(R.id.detailParkingSpecialInstruction);
        dateTV = (TextView) findViewById(R.id.detailParkingDate);
        priceTV = (TextView) findViewById(R.id.detailParkingPrice);
        parkingPhoto = (ImageView) findViewById(R.id.detailParkingPhoto);
        reserveBtn = (Button) findViewById(R.id.reserveBtn);
        editBtn = (Button) findViewById(R.id.detailEditBtn);
        reserveListBtn = (Button) findViewById(R.id.seeWhoBookedMyParkingSpace);
        reserveListBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(DetailParkingActivity.this, ReservationListActivity.class);
                startActivityForResult(intent, RESERVATION_LIST_VIEW_CODE);
            }
        });
        reviewBtn = (Button) findViewById(R.id.bookingReviewBtn);
        deleteBtn = (Button) findViewById(R.id.deleteListingBtn);

        //seeker set reservation from date to date, from time to time.
        reserveFromDate = (TextView) findViewById(R.id.reserveFromDateTV);
        reserveToDate = (TextView) findViewById(R.id.reserveToDateTV);
        reserveFromTime = (TextView) findViewById(R.id.reserveFromTimeTV);
        reserveToTime = (TextView) findViewById(R.id.reserveToTimeTV);

        //**************
        ratingLL = (LinearLayout) findViewById(R.id.detailParkingRatingLL);
        ratingLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showRatings();
            }
        });



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

        clickedParking = new Listing(bundle);

        //setting distance Textview
        String distanceAway = getDistanceAway(intent);
        distanceAway = String.format(Locale.US, "%.2f miles away", Double.parseDouble(distanceAway));
        distanceTV.setText(distanceAway);

        //This part is for default testing only****
        reserveFromDate.setText("From Date: " + clickedParking.getStartDate());
        reserveToDate.setText("To Date: " + clickedParking.getEndDate());
        reserveFromTime.setText("From Time - " + clickedParking.getStartTime());
        reserveToTime.setText("To Time - " + clickedParking.getEndTime());

        setParkingPhoto(clickedParking.getParkingIDRef());

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
                    if(reserveFromDate.getText().length() == 0 || reserveToDate.getText().length() == 0 || reserveFromTime.getText().length() == 0 ||
                            reserveToTime.getText().length() == 0) {
                        Toast.makeText(getApplicationContext(), "Cannot Leave Reservation Time Blank...", Toast.LENGTH_SHORT).show();
                    } else if (checkReservingYourOwnParking()){
                        //remind owner they cannot reserve their own parking.
                        Toast.makeText(getApplicationContext(), "Cannot Book Your Own Parking Space...", Toast.LENGTH_SHORT).show();
                    } else {
                        makeReservation();    //Duoc -- i will continue fixing this part tmr.
                        notifyOwner();
                    }
                    //updateDatabase();
                }
            });
        }

        // Set appropriate text for button
        if (request == SearchResultActivity.VIEW_DETAIL_PARKING_FROM_RESULT) {
            reserveBtn.setVisibility(View.VISIBLE);
            editBtn.setVisibility(View.GONE);       // only show edit button on listing history
            reserveBtn.setText("Reserve");
            reserveListBtn.setVisibility(View.GONE);
            reviewBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
        } else if (request == BookingHistoryActivity.VIEW_DETAIL_HISTORY_BOOKING_) {
            reserveBtn.setVisibility(View.GONE);    //book again should be taken out since it depends on the listing owner, i.e. you can't really book again if it's not up for listing
            editBtn.setVisibility(View.GONE);       // only show edit button on listing history
            reserveToDate.setVisibility(View.INVISIBLE);
            reserveFromDate.setVisibility(View.INVISIBLE);
            reserveToTime.setVisibility(View.INVISIBLE);
            reserveFromTime.setVisibility(View.INVISIBLE);
            reserveListBtn.setVisibility(View.INVISIBLE);
            deleteBtn.setVisibility(View.GONE);
            reviewBtn.setVisibility(View.VISIBLE);
            reviewBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    writeOwnerReview();
                }
            });

        } else if (request == ListingHistoryActivity.VIEW_DETAIL_HISTORY_LISTING) {
            reserveToDate.setVisibility(View.GONE);
            reserveFromDate.setVisibility(View.GONE);
            reserveToTime.setVisibility(View.GONE);
            reserveFromTime.setVisibility(View.GONE);
            reviewBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.VISIBLE);
            //An edit button will be shown on this screen as well to allow user to edit the listings he/she posted
            editBtn.setVisibility(View.VISIBLE);
            editBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    editListing();
                }
            });

            deleteBtn.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                }
            });

        }else if(request == ReservationListActivity.VIEW_DETAIL_RESERVATION) {
            reserveListBtn.setVisibility(View.GONE);
            editBtn.setVisibility(View.GONE);
            reserveBtn.setVisibility(View.GONE);
            deleteBtn.setVisibility(View.GONE);
            seekerLabel.setText("Seeker: ");
            seekerPhoneLabel.setText("Seeker Phone:");
            seekerEmailLabel.setText("Seeker Email: ");
            priceLabel.setVisibility(View.GONE);
            priceTV.setText("");
            distanceLabel.setVisibility(View.GONE);
            specialInstructionLabel.setVisibility(View.GONE);
            specialInstructionTV.setText("");
            reviewBtn.setVisibility(View.GONE);
            ownerTV.setText(clickedParking.getReservedBy().getName());
            addressTV.setText(clickedParking.getReservedBy().getPhoneNumber());
            dateTV.setText(clickedParking.getReservedBy().getEmailAddress());

            //remove listener for date views when viewing from history
            reserveFromDate.setOnClickListener(null);
            reserveToDate.setOnClickListener(null);
            reserveFromTime.setOnClickListener(null);
            reserveToTime.setOnClickListener(null);

            ratingLL.setVisibility(View.GONE);

        }

        //Hide distance if user is checking history
        LinearLayout ll = findViewById(R.id.detailParkingDistanceLL);

        if (request == BookingHistoryActivity.VIEW_DETAIL_HISTORY_BOOKING_ || request == ListingHistoryActivity.VIEW_DETAIL_HISTORY_LISTING) {
            ll.setVisibility(View.GONE);
        } else {
            ll.setVisibility(View.VISIBLE);
        }

        //Setting up rating bar
        ratingBar.setIsIndicator(true); //disable editing
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = clickedParking.getParkingIDRef();
                    if (dataSnapshot.child("ParkingSpaces").hasChild(targetID)) {
                        ParkingSpace p = dataSnapshot.child("ParkingSpaces").child(targetID).getValue(ParkingSpace.class);
                        double avgRating = 0;
                        int numReviews = 0;
                        if (p != null) {
                            avgRating = p.getAverageRating();
                            if (p.getReviews() != null)
                                numReviews = p.getReviews().size();
                        }
                        ratingBar.setRating((float)avgRating);
                        reviewCount.setText("(" + String.valueOf(numReviews) + " reviews)");
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        
    }

    private String getDistanceAway(Intent intent) {
        double lat = intent.getDoubleExtra(SearchResultActivity.LATITUDE, 0);
        double lng = intent.getDoubleExtra(SearchResultActivity.LONGITUDE, 0);

        Location location = new Location("");
        location.setLatitude(lat);
        location.setLongitude(lng);

        Location thatLocation = new Location("");
        thatLocation.setLatitude(clickedParking.getAddress().getLatitude());
        thatLocation.setLongitude(clickedParking.getAddress().getLongitude());

        return String.valueOf(location.distanceTo(thatLocation) / MILES_TO_METER);
    }


    private void showRatings() {
        Intent intent = new Intent(DetailParkingActivity.this, ViewRatingsActivity.class);
        intent.putExtra(SearchResultActivity.PARKING_ID_REF, clickedParking.getParkingIDRef());
        startActivity(intent);
    }

    public boolean checkReservingYourOwnParking() {
        String ownerParkingID = clickedParking.getOwnerParkingID();
        String ownerID = firebaseAuth.getCurrentUser().getUid();
        return ownerParkingID.equals(ownerID);
    }

    private void writeOwnerReview() {
        String reviewerID = firebaseAuth.getCurrentUser().getUid();
        String revieweeID = clickedParking.getOwnerParkingID();
        String parkingID = clickedParking.getParkingIDRef();

        Intent intent = new Intent(this, BookingReviewActivity.class);
        intent.putExtra("reviewerID", reviewerID);
        intent.putExtra("revieweeID", revieweeID);
        intent.putExtra("parkingID", parkingID);

        startActivityForResult(intent, WRITE_REVIEW_CODE);
    }


    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        if (requestCode == LISTING_EDIT_CODE && resultCode == RESULT_OK) {      //take input from listing editting page and reflect changes to database here
            setResult(RESULT_OK);
            finish();
        } else if (requestCode == WRITE_REVIEW_CODE && resultCode == RESULT_OK) {
            finish();
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
        intent.putExtra("parkingID", clickedParking.getParkingIDRef());
        intent.putExtra("listingID", clickedParking.getId());
        startActivityForResult(intent, LISTING_EDIT_CODE);
    }



    /**
     * Parse an address into separate fields, such as street number, city, state, zipCode
     * @param fullAddress the adddress in full format (1 Washington Square, San Jose, CA 95112)
     * @return An arraylist containing all the fields. [0] for street number, [1] for city, [2] for state, and [3] for zip code
     */
    private ArrayList<String> parseAddress(String fullAddress) {
        if (!fullAddress.isEmpty()) {
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
        return null;
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
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                //Handle any errors
                //Parking photo is the default one, user has not set a photo for the listing yet
                Picasso.with(getApplicationContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(parkingPhoto);
            }
        });

    }

    /**
     * Implement reservation functionality here
     */
    public void makeReservation() {
        final String parkingID = clickedParking.getParkingIDRef();

        //here add the reserved parking to user's myCurrentReservedParkings lists

        //0. Split the parking space depending on seeker's booking range.
        //1. Delete reference from AvailableParkings
        //2. Delete parking from Listings then split it.
        //3. Take seeker's desire booking range and reserve the space.
        //4. Update seeker's object in database.
        //5. Update owner that they have reserved which portion of the available time.
        //6. add the splitted parking space to database if there are remainding ones.

        Listing[] spaces = splitParkingSpace(clickedParking);  //0.
        listingToBook = spaces[0];
        deleteParkingReference(clickedParking, spaces);     //1.

        //combined 2 and 6 inside 1 to prevent concurrency problem (too many listener for data change at once) - Huy
//        deleteParkingListing(parkingID);        //2.
//        addSplittedParkingsToDatabase(spaces);  //6

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);
                            currentUser.addReservedParking(listingToBook);     //3.
                            String currentUserID = currentUser.getId();
                            databaseReference.child("Users").child(currentUserID).setValue(currentUser); //4.

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


    public void notifyOwner() {
        final String ownerID = listingToBook.getOwnerParkingID();
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                    if(currentUser == null){
                        if(firebaseAuth.getCurrentUser() != null) {
                            String targetID = firebaseAuth.getCurrentUser().getUid();
                            if (!targetID.isEmpty()) {
                                if (dataSnapshot.child("Users").hasChild(targetID)) {
                                    currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);
                                }
                            }
                        }
                    }
                    Listing p = listingToBook.clone();
                    User currentUserPublicInfo = currentUser.clone();
                    p.setReservedBy(currentUserPublicInfo);
                    if (dataSnapshot.child("Users").hasChild(ownerID)) {
                        User owner = dataSnapshot.child("Users").child(ownerID).getValue(User.class);
                        owner.addToMyReservetionList(p);
                        databaseReference.child("Users").child(ownerID).setValue(owner);
                    }
                }


            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
    }


    public Listing[] splitParkingSpace(Listing clickedParking) {
        String reserveStartDate = reserveFromDate.getText().toString().split(":")[1].trim();
        String reserveEndDate = reserveToDate.getText().toString().split(":")[1].trim();
        String reserveStartTime = reserveFromTime.getText().toString().split("-")[1].trim();
        String reserveEndTime = reserveToTime.getText().toString().split("-")[1].trim();


        String clickedParkingStartDate= clickedParking.getStartDate();
        String clickedParkingEndDate = clickedParking.getEndDate();
        String clickedParkingStartTime = clickedParking.getStartTime();
        String clickedParkingEndTime = clickedParking.getEndTime();

        Listing reserveParking = clickedParking.clone();
        Listing splittedParking1 = clickedParking.clone();
        Listing splittedParking2 = clickedParking.clone();

        Listing[] splitted = new Listing[3];      //[0] ps to book. [1]&[2] splitted ps

        if(clickedParkingStartDate.equals(reserveStartDate) && reserveEndDate.equals(clickedParkingEndDate)){
            //reserving the entire clicked parking.
                reserveParking = clickedParking;
            if(clickedParkingStartTime.equals(reserveStartTime) && clickedParkingEndTime.equals(reserveEndTime)) {
                //do nothing
                splittedParking1 = null;
                splittedParking2 = null;
            } else if (clickedParkingStartTime.equals(reserveStartTime) && !clickedParkingEndTime.equals(reserveEndTime)) {
                //split 2. reserving first half hour.
                reserveParking.setEndTime(reserveEndTime);
                splittedParking1.setStartTime(reserveEndTime);
                splittedParking2 = null;
            } else if (!clickedParkingStartTime.equals(reserveStartTime) && clickedParkingEndTime.equals(reserveEndTime)) {
                //split 2. reserving second half hour.
                reserveParking.setStartTime(reserveStartTime);
                splittedParking1.setEndTime(reserveStartTime);
                splittedParking2 = null;
            } else {
                //split 3. reserving middle hour.
                reserveParking.setStartTime(reserveStartTime);
                reserveParking.setEndTime(reserveEndTime);

                splittedParking1.setEndTime(reserveStartTime);
                splittedParking2.setStartTime(reserveEndTime);
            }

        } else if(clickedParkingStartDate.equals(reserveStartDate) && !clickedParkingEndDate.equals(reserveEndDate)) {
            //split 2. reserving first half day.
            reserveParking.setEndDate(reserveEndDate);  //set new end date
            GregorianCalendar ref = getGregorianCalendarDate(reserveEndDate);
            ref.add(Calendar.DAY_OF_MONTH, 1);
            String startDate = getDate(ref);
            splittedParking1.setStartDate(startDate);   //set new start date.

            if(clickedParkingStartTime.equals(reserveStartTime) && clickedParkingEndTime.equals(reserveEndTime)){
                //same time. do nothing more
                splittedParking2 = null;

            } else if (clickedParkingStartTime.equals(reserveStartTime) && !clickedParkingEndTime.equals(reserveEndTime)) {
                //end time is different.
                reserveParking.setEndTime(reserveEndTime);
                //change split2 to first half but start different time end same time. same date as reserve parking.
                splittedParking2 = reserveParking.clone();
                splittedParking2.setStartTime(reserveEndTime);
                splittedParking2.setEndTime(clickedParkingEndTime);
            } else if (!clickedParkingStartTime.equals(reserveStartTime) && clickedParkingEndTime.equals(reserveEndTime)){
                //start time is different. change split2 to end different time. Same date as reserve parking.
                reserveParking.setStartTime(reserveStartTime);
                splittedParking2 = reserveParking.clone();
                splittedParking2.setStartTime(clickedParkingStartTime);
                splittedParking2.setEndTime(reserveStartTime);
            } else {
                reserveParking.setStartTime(reserveStartTime);
                reserveParking.setEndTime(reserveEndTime);
                splittedParking2 = reserveParking.clone();
                splittedParking2.setStartTime(clickedParkingStartTime);
                splittedParking2.setEndTime(reserveStartTime);
                Listing splittedParking3 = reserveParking.clone();
                splittedParking3.setStartTime(reserveEndTime);
                splittedParking3.setEndTime(clickedParkingEndTime);
                splitted = new Listing[4];
                splitted[3] = splittedParking3;

            }

        } else if(!clickedParkingStartDate.equals(reserveStartDate) && clickedParkingEndDate.equals(reserveEndDate)) {
            //split 2. reserving second half.
            reserveParking.setStartDate(reserveStartDate);

            GregorianCalendar ref = getGregorianCalendarDate(reserveStartDate);
            ref.add(Calendar.DAY_OF_MONTH, -1);
            String endDate = getDate(ref);  //new end date
            splittedParking1.setEndDate(endDate);
            if(clickedParkingStartTime.equals(reserveStartTime) && clickedParkingEndTime.equals(reserveEndTime)){
                //same time. do nothing more
                splittedParking2 = null;

            } else if (clickedParkingStartTime.equals(reserveStartTime) && !clickedParkingEndTime.equals(reserveEndTime)) {
                //end time is different.
                reserveParking.setEndTime(reserveEndTime);
                //change split2 to first half but start different time end same time. same date as reserve parking.
                splittedParking2 = reserveParking.clone();
                splittedParking2.setStartTime(reserveEndTime);
                splittedParking2.setEndTime(clickedParkingEndTime);
            } else if (!clickedParkingStartTime.equals(reserveStartTime) && clickedParkingEndTime.equals(reserveEndTime)){
                //start time is different. change split2 to end different time. Same date as reserve parking.
                reserveParking.setStartTime(reserveStartTime);
                splittedParking2 = reserveParking.clone();
                splittedParking2.setStartTime(clickedParkingStartTime);
                splittedParking2.setEndTime(reserveStartTime);
            } else {
                reserveParking.setStartTime(reserveStartTime);
                reserveParking.setEndTime(reserveEndTime);
                splittedParking2 = reserveParking.clone();
                splittedParking2.setStartTime(clickedParkingStartTime);
                splittedParking2.setEndTime(reserveStartTime);
                Listing splittedParking3 = reserveParking.clone();
                splittedParking3.setStartTime(reserveEndTime);
                splittedParking3.setEndTime(clickedParkingEndTime);
                splitted = new Listing[4];
                splitted[3] = splittedParking3;
            }

        } else {
            //split 3. reserving the middle one.
            reserveParking.setStartDate(reserveStartDate);
            reserveParking.setEndDate(reserveEndDate);

            GregorianCalendar ref = getGregorianCalendarDate(reserveStartDate);
            ref.add(Calendar.DAY_OF_MONTH, -1);
            splittedParking1.setEndDate(getDate(ref));

            GregorianCalendar ref2 = getGregorianCalendarDate(reserveEndDate);
            ref2.add(Calendar.DAY_OF_MONTH, 1);
            splittedParking2.setStartDate(getDate(ref2));

            if(clickedParkingStartTime.equals(reserveStartTime) && clickedParkingEndTime.equals(reserveEndTime)){
                //do nothing.
            } else if (clickedParkingStartTime.equals(reserveStartTime) && !clickedParkingEndTime.equals(reserveEndTime)) {
                reserveParking.setEndTime(reserveEndTime);
                Listing splittedParking3 = reserveParking.clone();
                splittedParking3.setStartTime(reserveEndTime);
                splittedParking3.setEndTime(clickedParkingEndTime);
                splitted = new Listing[4];
                splitted[3] = splittedParking3;
            } else if (!clickedParkingStartTime.equals(reserveStartTime) && clickedParkingEndTime.equals(reserveEndTime)){
                reserveParking.setStartTime(reserveStartTime);
                Listing splittedParking3 = reserveParking.clone();
                splittedParking3.setStartTime(clickedParkingStartTime);
                splittedParking3.setEndTime(reserveStartTime);
                splitted = new Listing[4];
                splitted[3] = splittedParking3;
            } else {
                reserveParking.setStartTime(reserveStartTime);
                reserveParking.setEndTime(reserveEndTime);

                Listing splittedParking3 = reserveParking.clone();
                Listing splittedParking4 = reserveParking.clone();
                splittedParking3.setStartTime(clickedParkingStartTime);
                splittedParking3.setEndTime(reserveStartTime);
                splittedParking4.setStartTime(reserveEndTime);
                splittedParking4.setEndTime(clickedParkingEndTime);
                splitted = new Listing[5];
                splitted[3] = splittedParking3;
                splitted[4] = splittedParking4;
            }
        }

        splitted[0] = reserveParking;
        splitted[1] = splittedParking1;
        splitted[2] = splittedParking2;


        //for now just return the retire click parking space. Will implement split later.

        return splitted;
    }

    public void deleteParkingReference(final Listing clickedParking, final Listing[] spaces) {
        final String listingID = clickedParking.getId();
        String startDate = clickedParking.getStartDate();
        String endDate = clickedParking.getEndDate();

        final GregorianCalendar startDateRef = getGregorianCalendarDate(startDate);
        final GregorianCalendar endDateRef = getGregorianCalendarDate(endDate);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {

                //Remove all existing available parkings with the currentListingID
                while(!startDateRef.equals(endDateRef)) {
                    String dateRef = getDate(startDateRef);
                    if (dataSnapshot.child("AvailableParkings").hasChild(dateRef)) {
                        if(dataSnapshot.child("AvailableParkings").child(dateRef).hasChild(listingID)) {
                            databaseReference.child("AvailableParkings").child(dateRef).child(listingID).removeValue();
                        }
                    }
                    startDateRef.add(Calendar.DAY_OF_MONTH, 1);     //increment
                }
                //Remove all existing available parkings with the currentListingID
                if(startDateRef.equals(endDateRef)) {
                    String dateRef = getDate(startDateRef);
                    if (dataSnapshot.child("AvailableParkings").hasChild(dateRef)) {
                        if(dataSnapshot.child("AvailableParkings").child(dateRef).hasChild(listingID)) {
                            databaseReference.child("AvailableParkings").child(dateRef).child(listingID).removeValue();
                        }
                    }
                }

                //Remove the listing with the listingID
//                if(dataSnapshot.child("Listings").hasChild(listingID)) {
//                    databaseReference.child("Listings").child(listingID).removeValue();
//                }
                addSplittedParkingsToDatabase(spaces);
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

    public void addSplittedParkingsToDatabase(Listing[] spaces) {
        int i = 1;
        int outOfBounds = spaces.length;
        Listing p = spaces[i];
        while(p != null && (i < outOfBounds)) {
            p = spaces[i];
            if (p != null) {
                p.setId(clickedParking.getId());

                String p1ChildKey = p.getId();

                GregorianCalendar start = getGregorianCalendarDate(p.getStartDate());
                GregorianCalendar end = getGregorianCalendarDate(p.getEndDate());

                int startTimeSystem[] = NewListingActivity.get24HoursTimeSystem(p.getStartTime());
                int endTimeSystem[] = NewListingActivity.get24HoursTimeSystem(p.getEndTime());
                int starthour = startTimeSystem[0];
                int startMinutes = startTimeSystem[1];
                int endHour = endTimeSystem[0];
                int endMinutes = endTimeSystem[1];

                String dataValue = starthour + ":" + startMinutes + ":" + endHour + ":" + endMinutes + "/" + p.getId();

                while (!start.equals(end)) {
                    String p1ParentKey = getDate(start);
                    databaseReference.child("AvailableParkings").child(p1ParentKey).child(p1ChildKey).setValue(dataValue);
                    start.add(Calendar.DAY_OF_MONTH, 1);
                }
                if (start.equals(end)) {
                    String p1ParentKey = getDate(start);
                    databaseReference.child("AvailableParkings").child(p1ParentKey).child(p1ChildKey).setValue(dataValue);
                }
//                databaseReference.child("Listings").child(p1ChildKey).setValue(p);    //leave the original listing data alone
            }
            i++;
        }

    }

    public String getDate(GregorianCalendar g) {
        int month = g.get(Calendar.MONTH) + 1;
        int day = g.get(Calendar.DAY_OF_MONTH);
        int year = g.get(Calendar.YEAR);

        return month + "-" + day + "-" + year;
    }

    public GregorianCalendar getGregorianCalendarDate(String date) {
        String[] tokens = date.split("-");
        int month = Integer.parseInt(tokens[0]);
        int day = Integer.parseInt(tokens[1]);
        int year = Integer.parseInt(tokens[2]);
        return new GregorianCalendar(year, month -1 , day);
    }

    public GregorianCalendar getGregorianCalendarTime(String time) {
        String[] tokens = time.split("-");
        int hour = Integer.parseInt(tokens[0]);
        int minute = Integer.parseInt(tokens[1]);
        return new GregorianCalendar(2018,0,1, hour, minute);
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
        } else if (hour == 12) {
            ampm = "PM";
        }

        hourString = Integer.toString(hour);
        minuteString = (minute < 10) ? "0" + Integer.toString(minute) : Integer.toString(minute);
        String completeTime = hourString + ":" + minuteString + " " + ampm;

        reserveFromTime.setText("From Time- " + completeTime);
    }

    public void setReserveToTime(int hour, int minute) {
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

        reserveToTime.setText("To Time- " + completeTime);
    }

    public void setReserveFromDate(int year, int month, int day){
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = Integer.toString(month);
        dayString = Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        reserveFromDate.setText("From Date: " +completeDate);
    }

    public void setReserveToDate(int year, int month, int day) {
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = Integer.toString(month);
        dayString = Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        reserveToDate.setText("To Date: " + completeDate);
    }


}