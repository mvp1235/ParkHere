package edu.sjsu.team408.parkhere;


import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.paypal.android.sdk.payments.PayPalConfiguration;
import com.paypal.android.sdk.payments.PayPalPayment;
import com.paypal.android.sdk.payments.PayPalPaymentDetails;
import com.paypal.android.sdk.payments.PayPalService;
import com.paypal.android.sdk.payments.PaymentActivity;
import com.paypal.android.sdk.payments.PaymentConfirmation;


import org.json.JSONException;

import java.math.BigDecimal;


/**
 * Created by DuocNguyen on 11/30/17.
 */

public class BookingPaymentActivity extends AppCompatActivity {
    private static final int REQUEST_CODE_PAYMENT = 1;
    private static final String CONFIG_CLIENT_ID = "AS9nh0d5XoK2B8zesFK8eDCaW1o0SsyOS9psGXFzJHVn0j9OH6yFBVUhDalC6KWLA04Tev9qyqUvsttG";

    private static PayPalConfiguration config = new PayPalConfiguration()
            .environment(PayPalConfiguration.ENVIRONMENT_SANDBOX)
            .clientId(CONFIG_CLIENT_ID);

    private TextView ownerInfoTV, seekerInfoTV, amountTV;
    private Button makePaymentBtn;
    private String ownerName;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment);

        Intent intent = new Intent(this, PayPalService.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        startService(intent);

        getPaymentInfo();

        makePaymentBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                processPayment();
            }
        });

    }

    @Override
    public void onDestroy() {
        stopService(new Intent(this, PayPalService.class));
        super.onDestroy();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(requestCode == REQUEST_CODE_PAYMENT) {
            if(resultCode == RESULT_OK) {
                PaymentConfirmation confirmation = data.getParcelableExtra(PaymentActivity.EXTRA_RESULT_CONFIRMATION);
                if(confirmation != null ) {
                    try {
                        String paymentDetails = confirmation.toJSONObject().toString(4);
                        startActivity(new Intent(this, PaymentDetails.class)
                        .putExtra("PaymentDetails", paymentDetails)
                        .putExtra("PaymentAmount", amountTV.getText().toString())
                        );
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }
            }else if(resultCode == PaymentActivity.RESULT_CANCELED){
                Toast.makeText(this, "Cancel", Toast.LENGTH_LONG).show();
            }
            else if (resultCode == PaymentActivity.RESULT_EXTRAS_INVALID) {
                Toast.makeText(this, "Invalid", Toast.LENGTH_LONG).show();
            }
        }
    }

    private void processPayment() {
        String amount = amountTV.getText().toString();
        String owner = ownerInfoTV.getText().toString();
        PayPalPayment payPalPayment = new PayPalPayment(new BigDecimal(String.valueOf(amount)), "USD", "Pay for Parking to " + ownerName,
                PayPalPayment.PAYMENT_INTENT_SALE);
        Intent intent = new Intent(BookingPaymentActivity.this, PaymentActivity.class);
        intent.putExtra(PayPalService.EXTRA_PAYPAL_CONFIGURATION, config);
        intent.putExtra(PaymentActivity.EXTRA_PAYMENT, payPalPayment);
        startActivityForResult(intent, REQUEST_CODE_PAYMENT);
    }

    public void getPaymentInfo(){
        Intent intent = getIntent();
        ownerName = intent.getStringExtra("ownerName");
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
