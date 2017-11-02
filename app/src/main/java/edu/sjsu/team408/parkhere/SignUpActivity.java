package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseUser;

import static edu.sjsu.team408.parkhere.MainActivity.mAuth;
import static edu.sjsu.team408.parkhere.ProfileFragment.TAG_SIGN_IN;

public class SignUpActivity extends AppCompatActivity {

    private EditText emailET, passwordET;
    private Button signUpBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sign_up);

        emailET = (EditText) findViewById(R.id.signUpEmail);
        passwordET = (EditText) findViewById(R.id.signUpPassword);
        signUpBtn = (Button) findViewById(R.id.signUpBtn);

        signUpBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                signUp();
            }
        });
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

                                Intent intent = new Intent();
                                setResult(RESULT_OK);
                                finish();
//                            updateUI(user);
                            } else {
                                // If sign in fails, display a message to the user.
                                Log.w(ProfileFragment.TAG_SIGN_UP, "createUserWithEmail:failure", task.getException());
                                Toast.makeText(SignUpActivity.this, "Authentication failed.",
                                        Toast.LENGTH_SHORT).show();
//                            updateUI(null);
                            }

                        }
                    });
        }
    }






}
