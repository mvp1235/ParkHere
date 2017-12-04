package edu.sjsu.team408.parkhere;

import android.Manifest;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.net.Uri;
import android.os.Build;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.tasks.OnCompleteListener;
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

import org.json.JSONException;
import org.json.JSONObject;

import java.io.ByteArrayOutputStream;


public class NewParkingSpaceActivity extends AppCompatActivity {

    private final static int REQUEST_GALLERY_PHOTO = 9000;
    private final static int REQUEST_IMAGE_CAPTURE = 9001;

    private EditText addressStreetNumber, addressCity, addressState, addressZipCode, specialInstructions;
    private Button saveParkingSpaceBtn, editParkingSpacePhotoBtn;
    private ImageView parkingSpacePhoto;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private String userID;
    private AlertDialog photoActionDialog;
    private ProgressDialog progressDialog;

    private String currentParkingIDRef;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_parking_space);


        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            //Request runtime permission for camera access
            if (checkSelfPermission(android.Manifest.permission.CAMERA)
                    != PackageManager.PERMISSION_GRANTED) {
                requestPermissions(new String[]{android.Manifest.permission.CAMERA},
                        REQUEST_IMAGE_CAPTURE);

            }

            //Request runtime permission for external storage writing permission
            if (checkSelfPermission(Manifest.permission.WRITE_EXTERNAL_STORAGE)
                    != PackageManager.PERMISSION_GRANTED) {

                requestPermissions(new String[]{android.Manifest.permission.WRITE_EXTERNAL_STORAGE},
                        REQUEST_IMAGE_CAPTURE);
            }
        }



        databaseReference = FirebaseDatabase.getInstance().getReference();  //gets database reference
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        //Get the parking id and use it throughout the activity
        currentParkingIDRef = databaseReference.child("AvailableParkings").push().getKey();

        //Referencing to the UI elements
        addressStreetNumber = (EditText) findViewById(R.id.parkingSpaceAddressStreetNumber);
        addressCity = (EditText) findViewById(R.id.parkingSpaceAddressCity);
        addressState = (EditText) findViewById(R.id.parkingSpaceAddressState);
        addressZipCode = (EditText) findViewById(R.id.parkingSpaceAddressZipCode);
        specialInstructions = (EditText) findViewById(R.id.parkingSpaceSpecialInstructions);
        parkingSpacePhoto = (ImageView) findViewById(R.id.parkingSpacePhoto);

        //For making new listing quicker
        //populateDefaultValuesForTesting();

        saveParkingSpaceBtn = (Button) findViewById(R.id.saveParkingSpaceBtn);
        saveParkingSpaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewParkingSpace();
            }
        });

        editParkingSpacePhotoBtn = (Button) findViewById(R.id.parkingSpaceEditPhotoBtn);
        editParkingSpacePhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoActionDialog();
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
            Log.i("TEST", path + "sddsdsd");
            Uri imageUri = Uri.parse(path);

            StorageReference filepath = storageReference.child("parkingPhotos").child(currentParkingIDRef);
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    Toast.makeText(getApplicationContext(), "Photo uploaded.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

            parkingSpacePhoto.setImageBitmap(parkingBitmap);
            photoActionDialog.dismiss();

        } else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading the photo...");
            progressDialog.show();

            Uri imageUri = data.getData();
            StorageReference filepath = storageReference.child("parkingPhotos").child(currentParkingIDRef);
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(getApplicationContext(), "Photo uploaded.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            parkingSpacePhoto.setImageURI(imageUri);
            photoActionDialog.dismiss();
        }
    }


    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        if (requestCode == REQUEST_IMAGE_CAPTURE) {
            if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                // Now user should be able to use camera
            }
            else {
                // Your app will not have this permission. Turn off all functions
                // that require this permission or it will force close like your
                // original question
            }
        }
    }

    private void showPhotoActionDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(NewParkingSpaceActivity.this);
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

    private void dispatchTakePictureIntent() {
        Intent takePictureIntent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        if (takePictureIntent.resolveActivity(getPackageManager()) != null) {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE);
        }
    }

    /**
     * Take all the user input to create a new listing and update to database
     */
    public void saveNewParkingSpace() {
        Intent i = new Intent();
        final String streetNumString = addressStreetNumber.getText().toString();
        final String cityString = addressCity.getText().toString();
        final String stateString = addressState.getText().toString();
        final String zipcodeString = addressZipCode.getText().toString();
        final String specialInstructionString = specialInstructions.getText().toString();

        //Making sure all fields are filled by user
        if (streetNumString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Street Number cannot be blank...", Toast.LENGTH_SHORT).show();
        } else if (cityString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "City cannot be blank...", Toast.LENGTH_SHORT).show();
        } else if (stateString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "State cannot be blank...", Toast.LENGTH_SHORT).show();
        } else if (zipcodeString.isEmpty()) {
            Toast.makeText(getApplicationContext(), "Zip Code cannot be blank...", Toast.LENGTH_SHORT).show();
        } else {
            i.putExtra("streetNum", streetNumString);
            i.putExtra("city", cityString);
            i.putExtra("state", stateString);
            i.putExtra("zipcode", zipcodeString);
            i.putExtra("specialInstruction", specialInstructionString);


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
                        location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                        // Get the value of the attribute whose name is
                        // "formatted_string"
                        if (location.getDouble("lat") != 0 && location.getDouble("lng") != 0) {
                            LatLng latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                            ParkingSpace p = new ParkingSpace();
                            Address address = new Address(streetNumString, cityString, stateString, zipcodeString, latLng.latitude, latLng.longitude);
                            User owner = new User(userID+"", null, null,null,null,null);

                            p.setAddress(address);
                            p.setParkingImageUrl("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png");
                            p.setSpecialInstruction(specialInstructionString);
                            p.setParkingID(currentParkingIDRef);
                            p.setOwner(owner);

                            addParkingToDatabase(p);
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

    private void addParkingToDatabase(final ParkingSpace p) {
        databaseReference.child("ParkingSpaces").child(p.getParkingID()).setValue(p); //add listing to ParkingSpaces database

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            User currentUser = null;
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);
                            currentUser.addToParkingSpacesList(p.getParkingID());
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
