package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

import org.json.JSONException;
import org.json.JSONObject;
import org.w3c.dom.Text;

public class PaymentDetails extends AppCompatActivity {
    private TextView statusTV, transactionIDTV, amountTV;
    private Button finishbtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_payment_details);

        statusTV = (TextView) findViewById(R.id.detailPaymentStatus);
        transactionIDTV = (TextView) findViewById(R.id.detailPaymentTransactionID);
        amountTV = (TextView) findViewById(R.id.detailPaymentAmountTV);
        finishbtn = (Button) findViewById(R.id.detailPaymentFinishButton);

        Intent intent = getIntent();

        try {
            JSONObject  jsonObject = new JSONObject(intent.getStringExtra("PaymentDetails"));
            showDetails(jsonObject.getJSONObject("response"), intent.getStringExtra("PaymentAmount"));
        } catch (JSONException e) {
            e.printStackTrace();
        }

        finishbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                setResult(RESULT_OK);
                finish();
            }
        });
    }

    /**
     * Show detailed payment information
     * @param response  Ojbect containing payment response information
     * @param paymentAmount Transaction amount
     */
    private void showDetails(JSONObject response, String paymentAmount) {
        try {
            statusTV.setText("Status: " + response.getString("state"));
            transactionIDTV.setText("Transaction ID: " + response.getString("id"));
            amountTV.setText("Amount: " + paymentAmount);

        } catch (JSONException e) {
            e.printStackTrace();
        }
    }
}
