package edu.sjsu.team408.parkhere;

import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.ProgressDialog;
import android.app.TimePickerDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
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
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.google.firebase.storage.UploadTask;
import com.squareup.picasso.Picasso;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.GregorianCalendar;

/**
 * Activity for editing a listing created by the user
 */
public class EditListingActivity extends AppCompatActivity {

    private final static int REQUEST_GALLERY_PHOTO = 9000;
    private final static int REQUEST_IMAGE_CAPTURE = 9001;
    private final static int FROM_DATE = 0;
    private final static int TO_DATE = 1;
    private final static int FROM_TIME = 2;
    private final static int TO_TIME = 3;

    private TextView owner;
    private EditText addressStreetNumber, addressCity, addressState, addressZipCode, price, startDate, endDate, startTime, endTime, specialInstructions;
    private Button saveListingBtn, editListingPhotoBtn;
    private ImageView listingPhoto;

    //For picking date of availability
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private String userID;
    private AlertDialog photoActionDialog;
    private ProgressDialog progressDialog;

    private String currentParkingID;
    private static String currentListingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_listing);

        databaseReference = FirebaseDatabase.getInstance().getReference();  //gets database reference
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);


        owner = (TextView) findViewById(R.id.editListingOwner);
        addressStreetNumber = (EditText) findViewById(R.id.editListingAddressStreetNumber);
        addressCity = (EditText) findViewById(R.id.editListingAddressCity);
        addressState = (EditText) findViewById(R.id.editListingAddressState);
        addressZipCode = (EditText) findViewById(R.id.eidtListingAddressZipCode);
        price = (EditText) findViewById(R.id.editListingPrice);
        startDate = (EditText) findViewById(R.id.editListingStartDate);
        endDate = (EditText) findViewById(R.id.editListingEndDate);
        startTime = (EditText) findViewById(R.id.editListingStartTime);
        endTime = (EditText) findViewById(R.id.editListingEndTime);
        specialInstructions = (EditText) findViewById(R.id.editListingSpecialInstructions);
        listingPhoto = (ImageView) findViewById(R.id.editListingPhoto);

        saveListingBtn = (Button) findViewById(R.id.editSaveListingBtn);
        editListingPhotoBtn = (Button) findViewById(R.id.editListingEditPhotoBtn);

        saveListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editListing();
            }
        });

        editListingPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoActionDialog();
            }
        });

        Intent intent = getIntent();
        String ownerStr = intent.getStringExtra("owner");
        String streetNumStr = intent.getStringExtra("streetAddress");
        String cityStr = intent.getStringExtra("city");
        String stateStr = intent.getStringExtra("state");
        String zipCodeStr = intent.getStringExtra("zipCode");
        String priceStr = intent.getStringExtra("price");
        String specialInstructionsStr = intent.getStringExtra("specialInstructions");
        currentParkingID = intent.getStringExtra("parkingID");
        currentListingID = intent.getStringExtra("listingID");

        //Prefilling datas
        owner.setText(ownerStr);
        addressStreetNumber.setText(streetNumStr);
        addressCity.setText(cityStr);
        addressState.setText(stateStr);
        addressZipCode.setText(zipCodeStr);
        price.setText(priceStr);
        specialInstructions.setText(specialInstructionsStr);

        //Retrieve date and time from Firebase and prefill
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Listings").hasChild(currentParkingID)) {
                    Listing parking = dataSnapshot.child("Listings").child(currentParkingID).getValue(Listing.class);

                    String startDateStr = parking.getStartDate();
                    String endDateStr = parking.getEndDate();
                    String startTimeStr = parking.getStartTime();
                    String endTimeStr = parking.getEndTime();
                    startDate.setText(startDateStr);
                    endDate.setText(endDateStr);
                    startTime.setText(startTimeStr);
                    endTime.setText(endTimeStr);

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });


        // Loading the parking photo
        final ImageView parkingPhoto = (ImageView) findViewById(R.id.editListingPhoto);
        StorageReference storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("parkingPhotos/" + currentParkingID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.with(getApplicationContext()).load(uri.toString()).into(parkingPhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Picasso.with(getApplicationContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(parkingPhoto);
            }
        });



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

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading the photo...");
            progressDialog.show();

            Bundle extras = data.getExtras();
            Bitmap parkingBitmap = (Bitmap) extras.get("data");

            ByteArrayOutputStream bytes = new ByteArrayOutputStream();
            parkingBitmap.compress(Bitmap.CompressFormat.JPEG, 100, bytes);
            String path = MediaStore.Images.Media.insertImage(getApplicationContext().getContentResolver(), parkingBitmap, "Title", null);
            Uri imageUri = Uri.parse(path);

            //Upload the photo to Firebase storage
            StorageReference filepath = storageReference.child("parkingPhotos").child(currentParkingID);
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    Toast.makeText(getApplicationContext(), "Photo uploaded.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

            //Set the bitmap to the image view
            listingPhoto.setImageBitmap(parkingBitmap);
            photoActionDialog.dismiss();

        } else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading the photo...");
            progressDialog.show();

            //Upload photo to Firebase storage
            Uri imageUri = data.getData();
            StorageReference filepath = storageReference.child("parkingPhotos").child(currentParkingID);
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(getApplicationContext(), "Photo uploaded.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            //Set the URI to the image view
            listingPhoto.setImageURI(imageUri);
            photoActionDialog.dismiss();
        }
    }

    /**
     * Shows user the prompt to pick between taking a photo or picking one from gallery
     */
    private void showPhotoActionDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditListingActivity.this);
        View mView = getLayoutInflater().inflate(R.layout.dialog_pick_photos, null);
        LinearLayout galleryLL = mView.findViewById(R.id.galleryLL);
        LinearLayout cameraLL = mView.findViewById(R.id.cameraLL);

        galleryLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(Intent.ACTION_OPEN_DOCUMENT, MediaStore.Images.Media.INTERNAL_CONTENT_URI);
                startActivityForResult(intent, REQUEST_GALLERY_PHOTO);
            }
        });

        cameraLL.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                dispatchTakePictureIntent();
            }
        });

        mBuilder.setView(mView);
        photoActionDialog = mBuilder.create();
        photoActionDialog.show();
    }

    /**
     * Start intent for taking picture
     */
    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Prepare data for editing the listing information on Firebase
     */
    public void editListing() {
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

////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            // Robin's geocoding code
            // Getting latitude and longitude of an address
//            Geocoder geocoder = new Geocoder(this);
//            List<android.location.Address> addressList;
//            LatLng point = null;
//            try {
//                String addressString = streetNumString + ", " + cityString + ", "
//                        + stateString + " " + zipcodeString;
//                addressList = geocoder.getFromLocationName(addressString, 5);
//                if (addressList.size() > 0) {
//                    android.location.Address location = addressList.get(0);
//                    location.getLatitude();
//                    location.getLongitude();
//                    point = new LatLng(location.getLatitude(), location.getLongitude());
//                }
//            } catch (IOException e) {
//                Toast.makeText(getApplicationContext(), R.string.addressInvalid,
//                        Toast.LENGTH_SHORT).show();
//            }
//
//            editListingOnDatabase(startDateString, endDateString, startTimeString, endTimeString,
//                    point);
///////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

            //Huy's replacement
            /////////////////////////////////////////////////////////////////////////////////////////////////////
            //FROM HERE
            //Let's use this version to prevent the problem we've been facing, i.e. it only works for certain address entered
            LatLng point = null;
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
                            editListingOnDatabase(startDateString, endDateString, startTimeString, endTimeString, latLng);
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
        public void onTimeSet(TimePicker view, int hourofday, int min){
            setStartTime(hourofday, min);
        }
    };

    private TimePickerDialog.OnTimeSetListener endTimeListener = new TimePickerDialog.OnTimeSetListener() {
        public void onTimeSet(TimePicker view,int hourofday,int min){
            setEndTime(hourofday, min);
        }
    };

    /**
     * Receive the input from time picker and set appropriate input for start time textview
     * @param hour hour returned by time picker
     * @param minute minute returned by time picker
     */
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

    /**
     * Receive the input from time picker and set appropriate input for end time textview
     * @param hour hour returned by time picker
     * @param minute minute returned by time picker
     */
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

    /**
     * Receive input from date picker and set appropriate input for start date textview
     * @param year year returned by date picker
     * @param month month returned by date picker
     * @param day day returned by date picker
     */
    public void setStartDate(int year, int month, int day) {
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = Integer.toString(month);
        dayString = Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        startDate.setText(completeDate);
    }

    /**
     * Receive input from date picker and set appropriate input for end date textview
     * @param year year returned by date picker
     * @param month month returned by date picker
     * @param day day returned by date picker
     */
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
    public void editListingOnDatabase(final String startDate, final String endDate, final String startTime,
                                      final String endTime, final LatLng point) {

        String ownerStr = owner.getText().toString();
        String priceStr = price.getText().toString();
        String address = addressStreetNumber.getText().toString() + ", " + addressCity.getText().toString()
                + ", " + addressState.getText().toString() + " " + addressZipCode.getText().toString();

        final Listing parking = getParkingSpace(startDate, endDate, startTime, endTime, userID, ownerStr, priceStr, address, point, currentParkingID);


        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                for (DataSnapshot d : dataSnapshot.child("AvailableParkings").getChildren()) {
                    if (d.hasChild(currentParkingID)) {
                        d.child(currentParkingID).getRef().removeValue();
                    }
                }

                String startDateList[] = startDate.split("-");
                String endDateList[] = endDate.split("-");
                int startTimeSystem[] = NewListingActivity.get24HoursTimeSystem(startTime);
                int endTimeSystem[] = NewListingActivity.get24HoursTimeSystem(endTime);

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


                String dataValue = starthour + ":" + startMinutes + ":" + endHour + ":" + endMinutes + "/" + currentParkingID; //starthour-startminutes-endhour-endminutes-currentParkingID
                String parentKey;
//        String parkingSpaceUidKey;

                //Adding special instruction to parking (if any is provided)
                String specialIns = specialInstructions.getText().toString();
                parking.setSpecialInstruction(specialIns);

                String childKey;
//        parkingSpaceUidKey = databaseReference.child("AvailableParkings").push().getKey();    // no longer using these, gonna use the member variable currentParkingID

                while(!startDateCalendar.equals(endDateCalendar)) {
                    int currentMonth = startDateCalendar.get(Calendar.MONTH) + 1 ;
                    int currentYear = startDateCalendar.get(Calendar.YEAR);
                    int currentDay = startDateCalendar.get(Calendar.DAY_OF_MONTH);


                    String currentDate = currentMonth + "-" + currentDay + "-" + currentYear;

                    childKey = currentParkingID;
                    parentKey = currentDate;

                    databaseReference.child("AvailableParkings").child(parentKey).child(childKey).setValue(dataValue); //add listing to database


                    startDateCalendar.add(Calendar.DAY_OF_MONTH, 1); //increment
                }
                if(startDateCalendar.equals(endDateCalendar)) {

                    int currentMonth = startDateCalendar.get(Calendar.MONTH) + 1 ;
                    int currentYear = startDateCalendar.get(Calendar.YEAR);
                    int currentDay = startDateCalendar.get(Calendar.DAY_OF_MONTH);

                    String currentDate = currentMonth + "-" + currentDay + "-" + currentYear;

                    childKey = currentParkingID;
                    parentKey = currentDate;


                    databaseReference.child("AvailableParkings").child(parentKey).child(childKey).setValue(dataValue); //add listing to database
                }
                databaseReference.child("Listings").child(currentParkingID).setValue(parking);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });
        String ownerID = firebaseAuth.getCurrentUser().getUid();
        parking.setOwnerParkingID(ownerID);
        editUserListing(parking);
    }

    /**
     * Gets a Listing object containging all provided information
     * @param startDate starting date of the listing
     * @param endDate ending date of the listing
     * @param startTime starting time of the listing
     * @param endTime ending time of the listing
     * @param userID owner ID
     * @param ownerName owner name
     * @param price price of listing
     * @param address address of listing
     * @param point lat/lng coordinate of listing
     * @param parkingID id of the associated parking space
     * @return a Listing object containing all the provided information
     */
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
        return new Listing(currentListingID, addr, owner, parkingImageUrl, specialInstruction, startDate, endDate, startTime, endTime ,Double.parseDouble(price), parkingID);
    }

    /**
     * Edit an existing listing
     * @param ps the listing to be edited
     */
    private void editUserListing(final Listing ps) {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                String ownerID = ps.getOwnerParkingID();
                if(dataSnapshot.child("Users").hasChild(ownerID)) {
                    User owner = dataSnapshot.child("Users").child(ownerID).getValue(User.class);
                    ArrayList<Listing> ownerListing = owner.getMyListingHistory();
                    Listing target = ownerListing.get(0);
                    for (Listing l : ownerListing) {
                        if (l.getParkingIDRef().equals(currentParkingID)) {
                            target = l;
                        }
                    }
                    ownerListing.remove(target);
                    ownerListing.add(ps);
                    owner.setMyListingHistory(ownerListing);
                    databaseReference.child("Users").child(ownerID).setValue(owner);
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }
}