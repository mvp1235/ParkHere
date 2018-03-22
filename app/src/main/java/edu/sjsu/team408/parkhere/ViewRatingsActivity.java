package edu.sjsu.team408.parkhere;

import android.app.ListActivity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.lang.reflect.Array;
import java.util.ArrayList;

/**
 * An activity which displays all ratings for a particular chosen parking spot
 */
public class ViewRatingsActivity extends ListActivity {

    private ArrayList<Review> reviews;
    private ArrayList<String> reviewIDs;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_ratings);

        //connect to firebase database
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        reviews = new ArrayList<>();
        reviewIDs = new ArrayList<>();

        Intent intent = getIntent();
        final String parkingID = intent.getStringExtra(SearchResultActivity.PARKING_ID_REF);

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    if(!parkingID.isEmpty()) {
                        if (dataSnapshot.child("ParkingSpaces").hasChild(parkingID)) {
                            ParkingSpace parkingSpace = null;
                            parkingSpace = dataSnapshot.child("ParkingSpaces").child(parkingID).
                                    getValue(ParkingSpace.class);

                            reviewIDs = parkingSpace.getReviews();

                            if (reviewIDs != null) {
                                final ArrayList<Review> rs = new ArrayList<>();
                                databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                                    @Override
                                    public void onDataChange(DataSnapshot dataSnapshot) {
                                        for (int i=0; i<reviewIDs.size(); i++) {
                                            String reviewID = reviewIDs.get(i);
                                            Review r = dataSnapshot.child("Reviews").child(reviewID).getValue(Review.class);
                                            reviews.add(r);
                                        }
                                        ReviewAdapter adapter = new ReviewAdapter(ViewRatingsActivity.this, reviews);
                                        // Attach the adapter to a ListView
                                        setListAdapter(adapter);
                                    }

                                    @Override
                                    public void onCancelled(DatabaseError databaseError) {

                                    }
                                });
                            }



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
