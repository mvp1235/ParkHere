package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;

public class PaymentDetails extends AppCompatActivity {
    private TextView statusTV;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        statusTV = (TextView) findViewById(R.id.detailPaymentStatus);

        Intent intent = getIntent();

        try {
            JSONObject  jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }


    }

    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            statusTV.setText(response.getString("state"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
