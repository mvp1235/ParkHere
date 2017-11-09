package edu.sjsu.team408.parkhere;


import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentTransaction;
import android.text.style.TtsSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;
import com.squareup.picasso.Picasso;

import static android.app.Activity.RESULT_OK;
import static edu.sjsu.team408.parkhere.MainActivity.mAuth;


/**
 * A simple {@link Fragment} subclass.
 */
public class ProfileFragment extends Fragment {

    public static String TAG_SIGN_UP = "Sign Up";
    public static String TAG_SIGN_IN = "Sign In";

    private static final int EDIT_PROFILE_CODE = 10001;
    private static final int MAKE_NEW_LISTING_CODE = 10002;
    private static final int SIGN_UP_CODE = 10003;
    private static final int SIGN_IN_CODE = 10004;

    private TextView name, email, phone, address;
    private ImageView profile;
    private Button signUpBtn, signInBtn, logOutBtn;
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private User currentUser;
    public ProfileFragment() {
        // Required empty public constructor
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        databaseReference = FirebaseDatabase.getInstance().getReference();
        firebaseAuth = FirebaseAuth.getInstance();
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_profile, container, false);

        name = (TextView) view.findViewById(R.id.userFullName);
        email = (TextView) view.findViewById(R.id.userEmail);
        phone = (TextView) view.findViewById(R.id.userPhone);
        address = (TextView) view.findViewById(R.id.userAddress);
        profile = (ImageView) view.findViewById(R.id.profilePicture);
        signUpBtn = (Button) view.findViewById(R.id.profileSignUpBtn);
        signInBtn = (Button) view.findViewById(R.id.profileSignInBtn);
        logOutBtn = (Button) view.findViewById(R.id.profileLogoutBtn);



        databaseReference.addListenerForSingleValueEvent(new ValueEventListener() {
            @Override
            public void onDataChange(DataSnapshot dataSnapshot) {
                Log.d("asd", "asdfa");
                if(firebaseAuth.getCurrentUser() != null) {
                    String targetID = firebaseAuth.getCurrentUser().getUid();
                    if(!targetID.isEmpty()) {
                        if (dataSnapshot.child("Users").hasChild(targetID)) {
                            currentUser = dataSnapshot.child("Users").child(targetID).getValue(User.class);
                            setCurrentUserProfile();
                        }
                    }
                }
            }

            @Override
            public void onCancelled(DatabaseError databaseError) {

            }
        });


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

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SignUpActivity.class);
                startActivityForResult(intent, SIGN_UP_CODE);
            }
        });

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(getContext(), SignInActivity.class);
                startActivityForResult(intent, SIGN_IN_CODE);
            }
        });

        logOutBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signOut();
            }
        });


        //Hiding/Showing elements based on whether user is logged in or not
        FirebaseUser currentUser = mAuth.getCurrentUser();
        if (currentUser != null) {
            signUpBtn.setVisibility(View.GONE);
            signInBtn.setVisibility(View.GONE);
            logOutBtn.setVisibility(View.VISIBLE);
            view.findViewById(R.id.profileLL).setVisibility(View.VISIBLE);
            view.findViewById(R.id.profileNameLL).setVisibility(View.VISIBLE);
            view.findViewById(R.id.profileEmailLL).setVisibility(View.VISIBLE);
            view.findViewById(R.id.profilePhoneLL).setVisibility(View.VISIBLE);
            view.findViewById(R.id.profileAddressLL).setVisibility(View.VISIBLE);

        } else {
            view.findViewById(R.id.profileLL).setVisibility(View.GONE);
            view.findViewById(R.id.profileNameLL).setVisibility(View.GONE);
            view.findViewById(R.id.profileEmailLL).setVisibility(View.GONE);
            view.findViewById(R.id.profilePhoneLL).setVisibility(View.GONE);
            view.findViewById(R.id.profileAddressLL).setVisibility(View.GONE);
            logOutBtn.setVisibility(View.GONE);
            signUpBtn.setVisibility(View.VISIBLE);
            signInBtn.setVisibility(View.VISIBLE);
        }


        
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
        } else if (requestCode == SIGN_UP_CODE) {
            if (resultCode == RESULT_OK) {
                refreshPage();
            }
        } else if (requestCode == SIGN_IN_CODE) {
            if (resultCode == RESULT_OK) {
                refreshPage();
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

    private void signOut() {
        mAuth.signOut();
        refreshPage();
    }

    private void refreshPage() {
        //Refresh profile page
        FragmentTransaction transaction = getFragmentManager().beginTransaction();
        transaction.replace(R.id.content, new ProfileFragment());
        transaction.commit();
    }

    private void setCurrentUserProfile() {

        if(this.currentUser == null) {
            return;
        }

        //String id = currentUser.getId();
        String name = currentUser.getName();
        Address address = currentUser.getAddress();
        String phoneNumber = currentUser.getPhoneNumber();
        String emailAddress = currentUser.getEmailAddress();
        String profileURL = currentUser.getProfileURL();

        this.name.setText(name);
        if(address != null) {
            this.address.setText(address.toString());
        }
        this.phone.setText(phoneNumber);
        this.email.setText(emailAddress);
        Picasso.with(getContext()).load(profileURL).into(profile);

    }

}
