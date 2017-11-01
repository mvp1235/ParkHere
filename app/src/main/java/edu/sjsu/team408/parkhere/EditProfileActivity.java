package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.EditText;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

public class EditProfileActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_edit_profile);

        Intent intent = getIntent();
        String name = intent.getStringExtra("name");
        String email = intent.getStringExtra("email");
        String phone = intent.getStringExtra("phone");
        String address = intent.getStringExtra("address");

        EditText nameET = (EditText) findViewById(R.id.editUserFullName);
        EditText emailET = (EditText) findViewById(R.id.editUserEmail);
        EditText phoneET = (EditText) findViewById(R.id.editUserPhone);
        EditText addressET = (EditText) findViewById(R.id.editUserAddress);
        ImageView profileIV = (ImageView) findViewById(R.id.editProfilePicture);

        nameET.setText(name);
        emailET.setText(email);
        phoneET.setText(phone);
        addressET.setText(address);
        Picasso.with(getApplicationContext()).load("https://orig00.deviantart.net/4c5d/f/2015/161/b/6/untitled_by_victoriastylinson-d8wt3ew.png").into(profileIV);

    }
}
