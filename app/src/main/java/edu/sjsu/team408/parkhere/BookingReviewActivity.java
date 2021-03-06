package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Iterator;

/**
 * Activity which allows user to leave a review on the listing they previously booked
 */
public class BookingReviewActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private String reviewerID, revieweeID, parkingID;
    private RatingBar ratingBar;
    private EditText descriptionET;
    private Button submitBtn;
    private TextView starCountsTV;

    private String currentReviewId;
    private String currentUserId;
    private ArrayList<String> currentUserReviews;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_review);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        currentUserId = firebaseAuth.getCurrentUser().getUid();

        currentReviewId = databaseReference.child("Reviews").push().getKey();
        currentUserReviews = new ArrayList<>();

        Intent i = getIntent();
        reviewerID = i.getStringExtra("reviewerID");
        revieweeID = i.getStringExtra("revieweeID");
        parkingID = i.getStringExtra("parkingID");


        starCountsTV = (TextView) findViewById(R.id.bookintStarCounts);
        ratingBar = (RatingBar) findViewById(R.id.bookingRatingBar);

        ratingBar.setOnRatingBarChangeListener(new RatingBar.OnRatingBarChangeListener() {
            @Override
            public void onRatingChanged(RatingBar ratingBar, float rating, boolean fromUser) {
                starCountsTV.setText(Float.toString(rating));
            }
        });

        descriptionET = (EditText) findViewById(R.id.bookingReviewDescription);
        submitBtn = (Button) findViewById(R.id.bookingReviewSubmitBtn);

        submitBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                saveReviewToDatabase();
            }
        });

        //Get the list of all reviews left by current user, and store in currentUserReviews
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (firebaseAuth.getCurrentUser() != null) {
                    if (dataSnapshot.child("Users").hasChild(currentUserId)) {
                        User user = dataSnapshot.child("Users").child(currentUserId).getValue(User.class);
                        currentUserReviews = user.getMyReviews();
                        if (currentUserReviews == null)
                            currentUserReviews = new ArrayList<>();
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        //If the user has already left a review for the same parking spot before, it will be prefilled
        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (firebaseAuth.getCurrentUser() != null) {
                    for (int i=0; i<currentUserReviews.size(); i++) {   //traverse through the reviews left by the user
                        if (dataSnapshot.child("Reviews").hasChild(currentUserReviews.get(i))) {
                            Review review = dataSnapshot.child("Reviews").child(currentUserReviews.get(i)).getValue(Review.class);
                            if (review != null && review.getParkingID().equalsIgnoreCase(parkingID)) {
                                ratingBar.setRating((float)review.getStars());
                                descriptionET.setText(review.getDescription());
                                starCountsTV.setText(String.valueOf(review.getStars()));

                                break;
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

    /**
     * Saves the review to the Firebase
     */
    private void saveReviewToDatabase() {
        final String description = descriptionET.getText().toString();
        final double star = ratingBar.getRating();

        //Iterate through the existing review database, check first to see if the reviewer has already left a review for the reviewee for that same listing
        databaseReference.child("Reviews").addValueEventListener(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Iterator<DataSnapshot> reviews = dataSnapshot.getChildren().iterator();
                boolean reviewAdded = false;
                while (reviews.hasNext()) {
                    DataSnapshot ds = reviews.next();
                    Review r = ds.getValue(Review.class);

                    String park, reviewer, reviewee;
                    park = r.getParkingID();
                    reviewer = r.getReviewerID();
                    reviewee = r.getRevieweeID();

                    //User has already left a review before, so edit existing one instead of creating new review
                    if (park.equalsIgnoreCase(parkingID) && reviewer.equalsIgnoreCase(reviewerID) && reviewee.equalsIgnoreCase(revieweeID)) {
                        Review review = new Review(r.getId(), star, reviewerID, revieweeID, description, parkingID);
                        databaseReference.child("Reviews").child(r.getId()).setValue(review);
                        reviewAdded = true;
                        addReviewIdToUser(review);

                        //add review ID to the particular parking space
                        //include the old rating to properly update average rating
                        addReviewIdToParkingSpace(review, r.getStars());
                        break;  //stop traversing once an existing review has been found
                    }
                }
                // user leaves the review for the first time
                if (!reviewAdded) {
                    Review review = new Review(currentReviewId, star, reviewerID, revieweeID, description, parkingID);
                    databaseReference.child("Reviews").child(currentReviewId).setValue(review);
                    addReviewIdToUser(review);
                    addReviewIdToParkingSpace(review, 0);
                }
                databaseReference.child("Reviews").removeEventListener(this);
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });

        setResult(RESULT_OK);
        finish();
    }

    /**
     * Adds the review ID to the user's review list on Firebase for references
     * @param r the review to be added
     */
    private void addReviewIdToUser(final Review r) {
        final String reviewerID = r.getReviewerID();
        final String revieweeID = r.getRevieweeID();
        final String reviewID = r.getId();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    //Add review ref ID to reviewer
                    if(!reviewerID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(reviewerID)) {
                            User currentUser = null;
                            currentUser = dataSnapshot.child("Users").child(reviewerID).getValue(User.class);
                            currentUser.addToReviewList(reviewID);
                            databaseReference.child("Users").child(reviewerID).setValue(currentUser);
                        }
                    }

                    //Add review ref ID to reviewee
                    if(!revieweeID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(revieweeID)) {
                            User currentUser = null;
                            currentUser = dataSnapshot.child("Users").child(revieweeID).getValue(User.class);
                            currentUser.addToFeedbackList(reviewID);
                            databaseReference.child("Users").child(revieweeID).setValue(currentUser);
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });
    }

    /**
     * Add an ID reference of the review to the particular parking
     * @param r the review to be added
     * @param oldRating the old rating if the user had previously rated on the parking already, 0 if it is the first time
     */
    private void addReviewIdToParkingSpace(final Review r, final double oldRating) {
        final String parkingID = r.getParkingID();
        final String reviewID = r.getId();
        final double star = r.getStars();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if(firebaseAuth.getCurrentUser() != null) {
                    //Add review ref ID to reviewer
                    if(!parkingID.isEmpty() && !reviewID.isEmpty()) {
                        if (dataSnapshot.child("ParkingSpaces").hasChild(parkingID)) {
                            ParkingSpace parking = null;
                            parking = dataSnapshot.child("ParkingSpaces").child(parkingID).getValue(ParkingSpace.class);
                            if (parking != null) {
                                parking.addToReviewList(reviewID, star, oldRating);
                            }
                            databaseReference.child("ParkingSpaces").child(parkingID).setValue(parking);
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
