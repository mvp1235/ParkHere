package edu.sjsu.team408.parkhere;

import android.app.ListActivity;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.ListView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.google.firebase.storage.StorageReference;

import java.util.ArrayList;

public class ViewParkingSpacesActivity extends ListActivity {

    static final int MANAGE_PARKING_SPACE = 10001;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private StorageReference storageReference;
    private ArrayList<String> parkingSpaceIds;

    private String userID;

    private ParkingSpaceAdapter adapter;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_parking_spaces);

        databaseReference = FirebaseDatabase.getInstance().getReference();  //gets database reference
        firebaseAuth = FirebaseAuth.getInstance();
        userID = firebaseAuth.getCurrentUser().getUid();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(dataSnapshot.child("Users").hasChild(userID)) {
                    User user = dataSnapshot.child("Users").child(userID).getValue(User.class);
                    parkingSpaceIds = user.getMyParkingSpaces();

                    final ArrayList<ParkingSpace> parkingSpaces = new ArrayList<>();
                    databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                        @Override
                        public void onDataChange(DataSnapshot dataSnapshot) {

                            for (int i=0; i<parkingSpaceIds.size(); i++) {
                                ParkingSpace p = dataSnapshot.child("ParkingSpaces").child(parkingSpaceIds.get(i)).getValue(ParkingSpace.class);
                                parkingSpaces.add(p);
                            }

                            adapter = new ParkingSpaceAdapter(ViewParkingSpacesActivity.this, parkingSpaces);
                            setListAdapter(adapter);

                        }

                        @Override
                        public void onCancelled(DatabaseError databaseError) {

                        }
                    });

                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
                //not supported
            }
        });

    }

    @Override
    protected void onListItemClick(ListView l, View v, int position, long id) {
        ParkingSpace parking = (ParkingSpace) getListAdapter().getItem(position);
        Intent intent = new Intent(this, ManageParkingSpaceActivity.class);
        intent.putExtra("address", parking.getAddress().toString());
        intent.putExtra("instruction", parking.getSpecialInstruction());
        intent.putExtra("parkingID", parking.getParkingID());

        startActivityForResult(intent, MANAGE_PARKING_SPACE);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == MANAGE_PARKING_SPACE && resultCode == RESULT_OK) {
            finish();
        }
    }
}
