package edu.sjsu.team408.parkhere;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

public class DetailParkingActivity extends AppCompatActivity {

    private TextView addressTV, ownerTV, specialInstructionTV, dateTV, priceTV;
    private ImageView parkingPhoto;
    private Button reserveBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_detail_parking);

        //Reference to the UI elements
        addressTV = (TextView) findViewById(R.id.detailParkingAddress);
        ownerTV = (TextView) findViewById(R.id.detailParkingOwner);
        specialInstructionTV = (TextView) findViewById(R.id.detailParkingSpecialInstruction);
        dateTV = (TextView) findViewById(R.id.detailParkingDate);
        priceTV = (TextView) findViewById(R.id.detailParkingPrice);
        parkingPhoto = (ImageView) findViewById(R.id.detailParkingPhoto);
        reserveBtn = (Button) findViewById(R.id.reserveBtn);

        Intent intent = getIntent();
        Bundle bundle = intent.getBundleExtra(SearchResultActivity.PARKING_BUNDLE);

        ParkingSpace clickedParking = new ParkingSpace(bundle);

        Picasso.with(getApplicationContext()).load(clickedParking.getParkingImageUrl()).into(parkingPhoto);
        addressTV.setText(clickedParking.getAddress().toString());
        ownerTV.setText(clickedParking.getOwner().getId());
        specialInstructionTV.setText(clickedParking.getSpecialInstruction());

        //If start date and end date are the same, only display the start date
        //otherwise, have it in the format of "startDate - endDate"
        String availableDate = clickedParking.getStartDate();
        if (!clickedParking.getStartDate().equalsIgnoreCase(clickedParking.getEndDate())) {
            availableDate += " - " + clickedParking.getEndDate();
        }
        dateTV.setText(availableDate);

        priceTV.setText("$" + String.valueOf(clickedParking.getPrice()));

        reserveBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                makeReservation();
            }
        });
    }

    /**
     * Implement reservation functionality here
     */
    public void makeReservation() {

    }

}
