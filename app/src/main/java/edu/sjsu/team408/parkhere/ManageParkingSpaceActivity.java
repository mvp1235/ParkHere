package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;
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

/**
 * Models manage parking space activity
 */
public class ManageParkingSpaceActivity extends AppCompatActivity {

    private static final int EDIT_PARKING_SPACE = 5001;
    private TextView parkingAddress, parkingInstruction, parkingIdTV;
    private Button deleteBtn, editBtn;
    private ImageView parkingSpacePhoto;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;

    private String parkingID, instruction, address;
    private String userID;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_parking_space);

        databaseReference = FirebaseDatabase.getInstance().getReference();  //gets database reference
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        Intent intent = getIntent();
        parkingID = intent.getStringExtra("parkingID");
        instruction = intent.getStringExtra("instruction");
        address = intent.getStringExtra("address");


        parkingAddress = (TextView) findViewById(R.id.manageParkingSpaceAddress);
        parkingInstruction = (TextView) findViewById(R.id.manageParkingSpaceSpecialInstruction);
        parkingIdTV = (TextView) findViewById(R.id.manageParkingSpaceID);
        deleteBtn = (Button) findViewById(R.id.manageParkingSpaceDeleteBtn);
        editBtn = (Button) findViewById(R.id.manageParkingSpaceEditBtn);
        parkingSpacePhoto = (ImageView) findViewById(R.id.manageParkingSpacePhoto);

        parkingAddress.setText(address);
        parkingInstruction.setText(instruction);
        parkingIdTV.setText(parkingID);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteParkingSpace();
            }
        });

        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                editParkingSpace();
            }
        });

        storageReference = FirebaseStorage.getInstance().getReference();
        storageReference.child("parkingPhotos/" + parkingID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
            @Override
            public void onSuccess(Uri uri) {
                // Got the download URL
                Picasso.with(getApplicationContext()).load(uri.toString()).into(parkingSpacePhoto);
            }
        }).addOnFailureListener(new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception exception) {
                // Handle any errors
                // In this case, load the default parking photo
                Picasso.with(getApplicationContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(parkingSpacePhoto);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_OK && requestCode == EDIT_PARKING_SPACE) {
            storageReference.child("parkingPhotos/" + parkingID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL
                    Picasso.with(getApplicationContext()).load(uri.toString()).into(parkingSpacePhoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    // In this case, load the default parking photo
                    Picasso.with(getApplicationContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(parkingSpacePhoto);
                }
            });
            setResult(RESULT_OK);
            finish();
        }
    }

    /**
     * Take user to edit parking space view
     */
    private void editParkingSpace() {
        Intent intent = new Intent(ManageParkingSpaceActivity.this, EditParkingSpaceActivity.class);
        ArrayList<String> parsedAddress = parseAddress(address);
        intent.putExtra("streetAddress", parsedAddress.get(0));
        intent.putExtra("city", parsedAddress.get(1));
        intent.putExtra("state", parsedAddress.get(2));
        intent.putExtra("zipCode", parsedAddress.get(3));
        intent.putExtra("specialInstructions", instruction);
        intent.putExtra("parkingID", parkingID);
        startActivityForResult(intent, EDIT_PARKING_SPACE);
    }

    /**
     * Remove parking space from database
     */
    private void deleteParkingSpace() {
        if (userID != null && parkingID != null) {
            //Delete from ParkingSpaces
            databaseReference.child("ParkingSpaces").child(parkingID).removeValue();

            //Delete from User's parking space list
            databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                @Override
                public void onDataChange(DataSnapshot dataSnapshot) {
                    User user = dataSnapshot.child("Users").child(userID).getValue(User.class);
                    if (user != null) {
                        user.deleteFromParkingSpaces(parkingID);
                    }
                    databaseReference.child("Users").child(userID).setValue(user);
                }

                @Override
                public void onCancelled(DatabaseError databaseError) {

                }
            });
            Toast.makeText(getApplicationContext(), "Parking space is deleted...", Toast.LENGTH_SHORT).show();

            setResult(RESULT_OK);
            finish();
        }

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
}
