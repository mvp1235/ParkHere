package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

public class ManageParkingSpaceActivity extends AppCompatActivity {

    private TextView parkingAddress, parkingInstruction, parkingIdTV;
    private Button deleteBtn, editBtn;

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

        parkingAddress.setText(address);
        parkingInstruction.setText(instruction);
        parkingIdTV.setText(parkingID);

        deleteBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                deleteParkingSpace();
            }
        });
    }

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
}
