package edu.sjsu.team408.parkhere;

import android.app.AlertDialog;
import android.app.ProgressDialog;
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

/**
 * Activity for editing a parking space information
 */
public class EditParkingSpaceActivity extends AppCompatActivity {

    private final static int REQUEST_GALLERY_PHOTO = 9000;
    private final static int REQUEST_IMAGE_CAPTURE = 9001;

    private Button saveParkingSpaceBtn, parkingSpaceEditPhotoBtn;
    private ImageView parkingSpacePhoto;
    private EditText streetNumberET, cityET, stateET, zipCodeET, instructionET;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private String userID;
    private AlertDialog photoActionDialog;
    private ProgressDialog progressDialog;

    private String currentParkingID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_parking_space);

        databaseReference = FirebaseDatabase.getInstance().getReference();  //gets database reference
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        Intent intent = getIntent();
        String streetNumStr = intent.getStringExtra("streetAddress");
        String cityStr = intent.getStringExtra("city");
        String stateStr = intent.getStringExtra("state");
        String zipCodeStr = intent.getStringExtra("zipCode");
        String specialInstructionsStr = intent.getStringExtra("specialInstructions");
        currentParkingID = intent.getStringExtra("parkingID");

        saveParkingSpaceBtn = findViewById(R.id.saveParkingSpaceBtn);
        parkingSpaceEditPhotoBtn = findViewById(R.id.parkingSpaceEditPhotoBtn);
        parkingSpacePhoto = findViewById(R.id.parkingSpacePhoto);
        streetNumberET = findViewById(R.id.parkingSpaceAddressStreetNumber);
        cityET = findViewById(R.id.parkingSpaceAddressCity);
        stateET = findViewById(R.id.parkingSpaceAddressState);
        zipCodeET = findViewById(R.id.parkingSpaceAddressZipCode);
        instructionET = findViewById(R.id.parkingSpaceSpecialInstructions);

        streetNumberET.setText(streetNumStr);
        cityET.setText(cityStr);
        stateET.setText(stateStr);
        zipCodeET.setText(zipCodeStr);
        instructionET.setText(specialInstructionsStr);

        saveParkingSpaceBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editParkingSpace();
            }
        });
        parkingSpaceEditPhotoBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoActionDialog();
            }
        });

        storageReference.child("parkingPhotos/" + currentParkingID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.with(getApplicationContext()).load(uri.toString()).into(parkingSpacePhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Picasso.with(getApplicationContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(parkingSpacePhoto);
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

            //upload the photo to Firebase storage
            StorageReference filepath = storageReference.child("parkingPhotos").child(currentParkingID);
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

            //upload the photo to Firebase storage
            Uri imageUri = data.getData();
            StorageReference filepath = storageReference.child("parkingPhotos").child(currentParkingID);
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

    /**
     * Shows user the prompt to pick between taking a photo or picking one from gallery
     */
    private void showPhotoActionDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditParkingSpaceActivity.this);
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
     * Extract and validate data from input field for editing on Firebase
     */
    public void editParkingSpace() {
        Intent i = new Intent();
        final String streetNumString = streetNumberET.getText().toString();
        final String cityString = cityET.getText().toString();
        final String stateString = stateET.getText().toString();
        final String zipcodeString = zipCodeET.getText().toString();
        final String specialInstructionString = instructionET.getText().toString();

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
                    final JSONObject location;
                    try {
                        // Get JSON Array called "results" and then get the 0th
                        // complete object as JSON
                        location = response.getJSONArray("results").getJSONObject(0).getJSONObject("geometry").getJSONObject("location");
                        // Get the value of the attribute whose name is
                        // "formatted_string"
                        if (location.getDouble("lat") != 0 && location.getDouble("lng") != 0) {

                            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                @Override
                                public void onDataChange(DataSnapshot dataSnapshot) {
                                    if (dataSnapshot.child("ParkingSpaces").hasChild(currentParkingID)) {

                                        LatLng latLng = null;
                                        try {
                                            latLng = new LatLng(location.getDouble("lat"), location.getDouble("lng"));
                                        } catch (JSONException e) {
                                            e.printStackTrace();
                                        }

                                        ParkingSpace p = dataSnapshot.child("ParkingSpaces").child(currentParkingID).getValue(ParkingSpace.class);
                                        Address address = new Address(streetNumString, cityString, stateString, zipcodeString, latLng.latitude, latLng.longitude);

                                        p.setAddress(address);
                                        p.setSpecialInstruction(specialInstructionString);

                                        editParkingDatabase(p);
                                    }
                                }

                                @Override
                                public void onCancelled(DatabaseError databaseError) {

                                }
                            });

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

    /**
     * Edit the parking space on Firebase
     * @param p the parking space to be edited
     */
    private void editParkingDatabase(final ParkingSpace p) {
        databaseReference.child("ParkingSpaces").child(p.getParkingID()).setValue(p);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            User currentUser = null;
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);
                            if (currentUser != null) {
                                currentUser.addToParkingSpacesList(p.getParkingID());
                            }
                            databaseReference.child("Users").child(currentUser.getId()).setValue(currentUser);
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

}
