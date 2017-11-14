package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import static edu.sjsu.team408.parkhere.MainActivity.mAuth;
import static edu.sjsu.team408.parkhere.ProfileFragment.TAG_SIGN_IN;

public class SignUpActivity extends AppCompatActivity {


    static String EMAIL_EXIST_PROMPT = "The email address is already in use. Please choose another email.";
    static String SERVER_PROBLEM_PROMPT = "There are some server problems. Please try again later.";
    static String INVALID_EMAIL_FORMAT_PROMPT = "The email address is not in valid format.";
    private EditText emailET, passwordET;
    private TextView signUpPromptsTV;
    private Button signUpBtn;
    private DatabaseReference databaseReference;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailET = (EditText) findViewById(R.id.signUpEmail);
        passwordET = (EditText) findViewById(R.id.signUpPassword);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);
        signUpPromptsTV = (TextView) findViewById(R.id.signUpPrompts);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });

        databaseReference = FirebaseDatabase.getInstance().getReference();
    }

    private void signUp() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        if (email.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Email address cannot be empty.",
                    Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(SignUpActivity.this, "Password cannot be empty.",
                    Toast.LENGTH_SHORT).show();
        } else {
            //Sign Up New User
            mAuth.createUserWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(ProfileFragment.TAG_SIGN_UP, "createUserWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(SignUpActivity.this, "Signed up successfully.", Toast.LENGTH_SHORT).show();

                                //stores userID in database
                                storeUserInDatabase();

                                Intent intent = new Intent();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(ProfileFragment.TAG_SIGN_UP, "createUserWithEmail:failure", task.getException());
                                if (task.getException().toString().contains("The email address is already in use by another account")) {
                                    signUpPromptsTV.setText(EMAIL_EXIST_PROMPT);
                                } else if (task.getException().toString().contains("The email address is badly formatted.")) {
                                    signUpPromptsTV.setText(INVALID_EMAIL_FORMAT_PROMPT);
                                } else {
                                    signUpPromptsTV.setText(SERVER_PROBLEM_PROMPT);
                                }
                            }

                        }
                    });
        }
    }


    private void storeUserInDatabase() {
        User newUser = new User();
        String email = mAuth.getCurrentUser().getEmail();
        String ID = mAuth.getCurrentUser().getUid();
        String name = usernameFromEmail(email);
        String defaullProfileURL = "http://www.havoca.org/wp-content/uploads/2016/03/icon-user-default-300x300.png";
        newUser.setEmailAddress(email);
        newUser.setId(ID);
        newUser.setName(name);
        newUser.setProfileURL(defaullProfileURL);       // add a default user profile picture for the user. He/she can edit it later on the edit profile screen
        databaseReference.child("Users").child(ID).setValue(newUser);
    }

    public static String usernameFromEmail(String email) {
        if (email.contains("@")) {
            return email.split("@")[0];
        } else {
            return email;
        }
    }






}
