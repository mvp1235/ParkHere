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

public class BookingReviewActivity extends AppCompatActivity {

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    private String reviewerID, revieweeID, parkingID;
    private RatingBar ratingBar;
    private EditText descriptionET;
    private Button submitBtn;
    private TextView starCountsTV;

    private String currentReviewId;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_booking_review);

        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();

        currentReviewId = databaseReference.child("Reviews").push().getKey();

        Intent i = getIntent();
        reviewerID = i.getStringExtra("reviewerID");
        revieweeID = i.getStringExtra("revieweeID");
        parkingID = i.getStringExtra("parkingID");


        starCountsTV = (TextView) findViewById(R.id.bookintStarCounts);
        ratingBar = (RatingBar) findViewById(R.id.bookingRatingBar);
        ratingBar.setStepSize(1);   //number of stars can only be integer (1-5)

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




    }

    private void saveReviewToDatabase() {
        String description = descriptionET.getText().toString();
        double star = ratingBar.getRating();
        Review review = new Review(currentReviewId, star, reviewerID, revieweeID, description, parkingID);

        databaseReference.child("Reviews").child(currentReviewId).setValue(review);


        setResult(RESULT_OK);
        finish();

    }
}
