package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.location.Geocoder;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.List;

public class EditProfileActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private User currentUser;
    private EditText nameET, emailET, phoneET, addressET;
    private ImageView profileIV;
    private Button saveButton;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");

        nameET = (EditText) findViewById(R.id.editUserFullName);
        emailET = (EditText) findViewById(R.id.editUserEmail);
        phoneET = (EditText) findViewById(R.id.editUserPhone);
        addressET = (EditText) findViewById(R.id.editUserAddress);
        profileIV = (ImageView) findViewById(R.id.editProfilePicture);
        saveButton = (Button) findViewById(R.id.saveButton);
        saveButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View view) {
                updateProfile();
                finish();
            }
        });



        nameET.setText(name);
        emailET.setText(email);
        phoneET.setText(phone);
        addressET.setText(address);
        Picasso.with(getApplicationContext()).load("https://orig00.deviantart.net/4c5d/f/2015/161/b/6/untitled_by_victoriastylinson-d8wt3ew.png").into(profileIV);



    }

    public void updateProfile() {
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);

                            String fullName = nameET.getText().toString();
                            String email = emailET.getText().toString();
                            String phone = phoneET.getText().toString();
                            String address = addressET.getText().toString();

                            currentUser.setName(fullName);
                            currentUser.setEmailAddress(email);
                            currentUser.setPhoneNumber(phone);
                            currentUser.setAddress(getAddress(address));

                            databaseReference.child("Users").child(targetID).setValue(currentUser);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

    }

    public Address getAddress(String address) {
        Address newAddress = null;
        Geocoder geocoder = new Geocoder(this);

        try {
            List<android.location.Address> addressList = geocoder.getFromLocationName(address, 1);
            if (addressList.size() > 0) {
                android.location.Address location = addressList.get(0);
                location.getLatitude();
                location.getLongitude();
                LatLng point = new LatLng(location.getLatitude(), location.getLongitude());
                newAddress = new Address(address, point);
            }
        } catch (IOException e) {
            //Toast.makeText(getApplicationContext(), R.string.addressInvalid,
                    //Toast.LENGTH_SHORT).show();
        }
        return newAddress;
    }
}
