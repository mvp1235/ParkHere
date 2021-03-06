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

import static edu.sjsu.team408.parkhere.MainActivity.mAuth;
import static edu.sjsu.team408.parkhere.ProfileFragment.TAG_SIGN_IN;

/**
 * Sign in activity, allowing users to sign in to their ParkHere account
 */
public class SignInActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private Button signInBtn;
    private TextView signInPromptsTV;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_in);

        emailET = (EditText) findViewById(R.id.signInEmail);
        passwordET = (EditText) findViewById(R.id.signInPassword);
        signInBtn = (Button) findViewById(R.id.signInBtn);
        signInPromptsTV = (TextView) findViewById(R.id.signInPrompts);

        signInBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signIn();
            }
        });
    }

    /**
     * Signs the user in
     */
    private void signIn() {
        String email = emailET.getText().toString();
        String password = passwordET.getText().toString();

        Log.d(TAG_SIGN_IN, "signIn:" + email);

        if (email.isEmpty()) {
            Toast.makeText(this, "Email address cannot be empty.",
                    Toast.LENGTH_SHORT).show();
        } else if (password.isEmpty()) {
            Toast.makeText(this, "Password cannot be empty.",
                    Toast.LENGTH_SHORT).show();
        } else {
            // [START sign_in_with_email]
            mAuth.signInWithEmailAndPassword(email, password)
                    .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                        @Override
                        public void onComplete(@NonNull Task<AuthResult> task) {
                            if (task.isSuccessful()) {
                                // Sign in success, update UI with the signed-in user's information
                                Log.d(TAG_SIGN_IN, "signInWithEmail:success");
                                FirebaseUser user = mAuth.getCurrentUser();
                                Toast.makeText(SignInActivity.this, "Signed in successfully as " + user.getEmail(),
                                        Toast.LENGTH_SHORT).show();

                                Intent intent = new Intent();
                                setResult(RESULT_OK);
                                finish();
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(TAG_SIGN_IN, "signInWithEmail:failure", task.getException());
                                if (task.getException().toString().contains("There is no user record corresponding to this identifier. The user may have been deleted.")) {
                                    signInPromptsTV.setText("The email address provided is not associated with any user account.");
                                } else if (task.getException().toString().contains("The password is invalid or the user does not have a password.")) {
                                    signInPromptsTV.setText("Invalid password.");
                                } else if (task.getException().toString().contains("The email address is badly formatted.")) {
                                    signInPromptsTV.setText("The email address is not in valid format.");
                                } else {
                                    signInPromptsTV.setText("There are some problems with the server. Please try again later.");
                                }

                            }
                        }
                    });
        }
    }
}
