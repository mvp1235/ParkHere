package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.net.Uri;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.storage.FirebaseStorage;
import com.google.firebase.storage.StorageReference;
import com.squareup.picasso.Picasso;

public class ProfileInfo extends AppCompatActivity {
    private static final String TIME_TAG = "MyActivity";
    private TextView profileName, profileEmail, profilePhone, profileAddress;
    private ImageView profilePhoto;
    private StorageReference storageReference;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_profile_info);

        storageReference = FirebaseStorage.getInstance().getReference();

        profileName = (TextView) findViewById(R.id.profileFullName);
        profileEmail = (TextView) findViewById(R.id.profileEmail);
        profilePhone = (TextView) findViewById(R.id.profilePhone);
        profileAddress = (TextView) findViewById(R.id.profileAddress);
        profilePhoto= (ImageView) findViewById(R.id.profilePicture);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");
        String id = intent.getStringExtra("id");
        profileName.setText(name);
        profileEmail.setText(email);
        profilePhone.setText(phone);
        profileAddress.setText(address);

        String currentUserID = id;
        //Load image to profile ImageView
        if (currentUserID != null) {
            storageReference.child("userProfilePhotos/" + currentUserID).getDownloadUrl().addOnSuccessListener(new OnSuccessListener<Uri>() {
                @Override
                public void onSuccess(Uri uri) {
                    // Got the download URL for 'users/me/profile.png'
                    Picasso.with(getApplicationContext()).load(uri.toString()).into(profilePhoto);
                }
            }).addOnFailureListener(new OnFailureListener() {
                @Override
                public void onFailure(@NonNull Exception exception) {
                    // Handle any errors
                    Picasso.with(getApplicationContext()).load("https://d30y9cdsu7xlg0.cloudfront.net/png/47205-200.png").into(profilePhoto);
                }
            });
        }
        Log.i(TIME_TAG, "Ending time after payment is done: " + System.currentTimeMillis()/1000 + " Seconds");
    }
}
