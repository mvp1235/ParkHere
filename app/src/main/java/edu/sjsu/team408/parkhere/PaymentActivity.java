package edu.sjsu.team408.parkhere;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.Button;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;

/**
 * Created by DuocNguyen on 11/30/17.
 */

public class PaymentActivity extends AppCompatActivity {
    private DatabaseReference databaseReference;
    private FirebaseAuth firebaseAuth;
    private TextView ownerInfoTV, seekerInfoTV, amountTV;
    private Button makePaymentBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        getPaymentInfo();


    }

    public void getPaymentInfo(){
        Intent intent = getIntent();
        String ownerName = intent.getStringExtra("ownerName");
        String ownerEmail = intent.getStringExtra("ownerEmail");
        String seekerName = intent.getStringExtra("seekerName");
        String seekerEmail = intent.getStringExtra("seekerEmail");
        String amount = intent.getStringExtra("amount");

        ownerInfoTV = (TextView) findViewById(R.id.paymentOwnerTV);
        seekerInfoTV = (TextView) findViewById(R.id.paymentSeekerInfoTV);
        amountTV = (TextView) findViewById(R.id.paymentAmountTV);
        makePaymentBtn = (Button) findViewById(R.id.paymentButton);

        ownerInfoTV.setText("Name: " + ownerName + " \nEmail: " + ownerEmail);
        seekerInfoTV.setText("Name: " + seekerName + " \nEmail: " + seekerEmail);
        amountTV.setText(amount);

    }
}
