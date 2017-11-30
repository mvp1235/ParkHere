package edu.sjsu.team408.parkhere;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.location.Geocoder;
import android.net.Uri;
import android.provider.MediaStore;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Base64;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
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
import java.io.IOException;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private static final int REQUEST_IMAGE_CAPTURE = 9000;
    private static final int REQUEST_GALLERY_PHOTO = 9001;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private User currentUser;
    private EditText nameET, emailET, phoneET, streetNumET, cityET, stateET, zipCodeET;
    private ImageView profileIV;
    private Button saveButton, editProfilePictureBtn;


    private AlertDialog photoActionDialog;
    private ProgressDialog progressDialog;

    private String currentUserId;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        storageReference = FirebaseStorage.getInstance().getReference();
        progressDialog = new ProgressDialog(this);

        currentUserId = firebaseAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");

        nameET = (EditText) findViewById(R.id.editUserFullName);
        emailET = (EditText) findViewById(R.id.editUserEmail);
        phoneET = (EditText) findViewById(R.id.editUserPhone);
        streetNumET = (EditText) findViewById(R.id.editUserStreetNumber);
        cityET = (EditText) findViewById(R.id.editUserCity);
        stateET = (EditText) findViewById(R.id.editUserState);
        zipCodeET = (EditText) findViewById(R.id.editUserZipCode);

        profileIV = (ImageView) findViewById(R.id.editProfilePicture);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateProfile();
                finish();
            }
        });
        editProfilePictureBtn = (Button) findViewById(R.id.profileEditPhotoBtn);
        editProfilePictureBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showPhotoActionDialog();
            }
        });

        nameET.setText(name);
        emailET.setText(email);
        phoneET.setText(phone);

        if (!address.isEmpty()) {
            String addressToken[] = address.split(",");
            String stateAndZipCode[] = addressToken[2].trim().split(" ");

            String streetAddress = addressToken[0].trim();
            String city = addressToken[1].trim();
            String state = stateAndZipCode[0].trim();
            String zipcode = stateAndZipCode[1].trim();

            streetNumET.setText(streetAddress);
            cityET.setText(city);
            stateET.setText(state);
            zipCodeET.setText(zipcode);
        }

        //Load image to profile ImageView
        storageReference.child("userProfilePhotos/" + currentUserId).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL for 'users/me/profile.png'
                Picasso.with(getApplicationContext()).load(uri.toString()).into(profileIV);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                Picasso.with(getApplicationContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(profileIV);
            }
        });



    }

    private void showPhotoActionDialog() {
        AlertDialog.Builder mBuilder = new AlertDialog.Builder(EditProfileActivity.this);
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

            StorageReference filepath = storageReference.child("userProfilePhotos").child(currentUserId);
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {

                    Toast.makeText(getApplicationContext(), "Photo uploaded.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });

            profileIV.setImageBitmap(parkingBitmap);
            photoActionDialog.dismiss();

        } else if (requestCode == REQUEST_GALLERY_PHOTO && resultCode == RESULT_OK) {
            progressDialog.setMessage("Uploading the photo...");
            progressDialog.show();

            Uri imageUri = data.getData();
            StorageReference filepath = storageReference.child("userProfilePhotos").child(currentUserId);
            filepath.putFile(imageUri).addOnCompleteListener(new OnCompleteListener<UploadTask.TaskSnapshot>() {
                @Override
                public void onComplete(@NonNull Task<UploadTask.TaskSnapshot> task) {
                    Toast.makeText(getApplicationContext(), "Photo uploaded.", Toast.LENGTH_SHORT).show();
                    progressDialog.dismiss();
                }
            });
            profileIV.setImageURI(imageUri);
            photoActionDialog.dismiss();
        }
    }

    public void updateProfile() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    final String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);

                            String fullName = nameET.getText().toString();
                            String email = emailET.getText().toString();
                            String phone = phoneET.getText().toString();
                            final String address = streetNumET.getText().toString() + ", " + cityET.getText().toString() + ", " + stateET.getText().toString() + " " + zipCodeET.getText().toString();

                            currentUser.setName(fullName);
                            currentUser.setEmailAddress(email);
                            currentUser.setPhoneNumber(phone);

                            editProfileDatabase(address, targetID);

                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public void editProfileDatabase(final String address, final String targetID) {
        String url = "https://maps.googleapis.com/maps/api/geocode/json?address="
                + Uri.encode(address) + "&sensor=true&key=AIzaSyBqgv8PrGCSFVa-Nb2ymE3gGnuv-LgfGps";   //using my own API key here, 2,500 free request per day,
        // which should be fine for development now
        RequestQueue queue = Volley.newRequestQueue(getApplicationContext());
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

                        //Do what you want
                        currentUser.setAddress(new Address(address, latLng));

                        databaseReference.child("Users").child(targetID).setValue(currentUser);

                    } else {
                        //Invalid address. Handle here. For now, let's assume users need to enter a valid address
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
    }

    public Address getAddress(String address) {
        Address newAddress = null;
        Geocoder geocoder = new Geocoder(this);

        try {
            if (Geocoder.isPresent()) {
            List<android.location.Address> addressList = geocoder.getFromLocationName(address, 1);

            if (addressList.size() > 0) {
                android.location.Address location = addressList.get(0);
                location.getLatitude();
                location.getLongitude();
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                newAddress = new Address(address, point);
            }
            }
        } catch (IOException e) {
            //Toast.makeText(getApplicationContext(), R.string.addressInvalid,
                    //Toast.LENGTH_SHORT).show();
        }
        return newAddress;
    }
}
