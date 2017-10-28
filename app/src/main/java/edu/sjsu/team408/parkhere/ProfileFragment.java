package edu.sjsu.team408.parkhere;


import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {


    private static final int EDIT_PROFILE_CODE = 10001;
    private static final int MAKE_NEW_LISTING_CODE = 10002;
    private TextView name, email, phone, address;
    private ImageView profile;

    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = (TextView) view.findViewById(R.id.userFullName);
        email = (TextView) view.findViewById(R.id.userEmail);
        phone = (TextView) view.findViewById(R.id.userPhone);
        address = (TextView) view.findViewById(R.id.userAddress);
        profile = (ImageView) view.findViewById(R.id.profilePicture);

        Picasso.with(getContext()).load("https://orig00.deviantart.net/4c5d/f/2015/161/b/6/untitled_by_victoriastylinson-d8wt3ew.png").into(profile);

        //Setting up editing profile button
        Button editBtn = (Button) view.findViewById(R.id.editProfileBtn);
        editBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                editProfile(view);
            }
        });

        //Setting up listing button
        Button listBtn = (Button) view.findViewById(R.id.newListingBtn);
        listBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                makeNewListing(view);
            }
        });


        return view;
    }

    //Applying changes on the edit screen to the actual data
    //2 cases, editing profile or making new listing (different request codes)
    //Implement later@!!!!!!!!!!!!!!!!!!!!!!
    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);

        //Handling result from profile editing activity
        if (requestCode == EDIT_PROFILE_CODE) {
            if (resultCode == RESULT_OK) {

            }
        } else if (requestCode == MAKE_NEW_LISTING_CODE) {  // handling result from making new listing activity
            if (resultCode == RESULT_OK) {

            }
        }
    }

    /**
     * Pass all values from text inputs to new editing activity to prefill
     */
    public void editProfile(View view) {
        String nameString = name.getText().toString();
        String emailString = email.getText().toString();
        String phoneString = phone.getText().toString();
        String addressString = address.getText().toString();
        String profileURL = "https://orig00.deviantart.net/4c5d/f/2015/161/b/6/untitled_by_victoriastylinson-d8wt3ew.png";  //for now just using a certain URL, will be more dynamic later

        Intent intent = new Intent(getContext(), EditProfileActivity.class);
        intent.putExtra("name", nameString);
        intent.putExtra("email", emailString);
        intent.putExtra("phone", phoneString);
        intent.putExtra("address", addressString);
        intent.putExtra("profileURL", profileURL);
        startActivityForResult(intent, EDIT_PROFILE_CODE);

    }

    /**
     *Implement on callback result later
     */
    public void makeNewListing(View view) {
        String nameString = name.getText().toString();

        Intent intent = new Intent(getContext(), NewListingActivity.class);
        intent.putExtra("name", nameString);
        startActivityForResult(intent, MAKE_NEW_LISTING_CODE);
    }



}
