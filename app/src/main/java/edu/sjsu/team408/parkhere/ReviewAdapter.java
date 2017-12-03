package edu.sjsu.team408.parkhere;

import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.RatingBar;
import android.widget.TextView;

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
import java.util.Locale;

/**
 * Created by MVP on 12/1/2017.
 */

public class ReviewAdapter extends ArrayAdapter<Review> {

    private Context mContext;

    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;

    public ReviewAdapter(@NonNull Context context, ArrayList<Review> reviews) {
        super(context, 0, reviews);
        mContext = context;
    }

    @NonNull
    @Override
    public View getView(int position, @Nullable View convertView, @NonNull ViewGroup parent) {
        //Get the data item for this position
        Review review = getItem(position);

        databaseReference = FirebaseDatabase.getInstance().getReference();  //gets database reference
        firebaseAuth = FirebaseAuth.getInstance();

        // Check if an existing view is being reused, otherwise inflate the view
        if (convertView == null) {
            convertView = LayoutInflater.from(getContext()).inflate(R.layout.review_layout,
                    parent, false);
        }

        // Lookup view for data population
        final RatingBar ratingBar = (RatingBar) convertView.findViewById(R.id.reviewRatingBar);
        final TextView descriptionTV = (TextView) convertView.findViewById(R.id.reviewDescription);
        final TextView reviewWriterNameTV = (TextView) convertView.findViewById(R.id.reviewWriterName);

        ratingBar.setIsIndicator(true);

        final String reviewID = review.getId();

        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                if (dataSnapshot.child("Reviews").hasChild(reviewID)) {
                    final Review r = dataSnapshot.child("Reviews").child(reviewID).getValue(Review.class);

                    if (r != null) {
                        ratingBar.setRating((float)r.getStars());
                        descriptionTV.setText(r.getDescription());

                        //obtaining review writer's name and set to TextView
                        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                String reviewerID = r.getReviewerID();
                                if (dataSnapshot.child("Users").hasChild(reviewerID)) {
                                    User u = dataSnapshot.child("Users").child(reviewerID).getValue(User.class);
                                    reviewWriterNameTV.setText(u.getName());
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        });
                    }

                }

            }

            @Override
            public void onCancelled(DatabaseError databaseError) {
            }
        });




        return convertView;
    }
}
