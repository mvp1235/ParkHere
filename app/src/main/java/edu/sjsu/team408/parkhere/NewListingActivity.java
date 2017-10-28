package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

public class NewListingActivity extends AppCompatActivity {

    private TextView owner;
    private EditText address, price, startDate, endDate;
    private Button saveListingBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new_listing);

        Intent intent = getIntent();

        owner = (TextView) findViewById(R.id.listingOwner);

        address = (EditText) findViewById(R.id.listingAddress);
        price = (EditText) findViewById(R.id.listingPrice);
        startDate = (EditText) findViewById(R.id.listingStartDate);
        endDate = (EditText) findViewById(R.id.listingEndDate);

        saveListingBtn = (Button) findViewById(R.id.saveListingBtn);
        saveListingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                saveNewListing();
            }
        });

        String ownerName = intent.getStringExtra("name");
        owner.setText(ownerName);

    }

    public void saveNewListing() {
        Intent i = new Intent();
        String addressString = address.getText().toString();
        String priceString = price.getText().toString();
        String startDateString = startDate.getText().toString();
        String endDateString = endDate.getText().toString();

        i.putExtra("address", addressString);
        i.putExtra("price", Double.parseDouble(priceString));
        i.putExtra("startDate", startDateString);
        i.putExtra("endDate", endDateString);


        setResult(RESULT_OK, i);
        finish();
    }
}
