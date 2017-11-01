package edu.sjsu.team408.parkhere;

import android.app.DatePickerDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.DatePicker;
import android.widget.EditText;

import java.util.Calendar;

/**
 * Created by DuocNguyen on 11/1/17.
 */

public class ReservationActivity extends AppCompatActivity {

    private final static int FROM_DATE = 0;
    private DatePicker datePicker;
    private Calendar calendar;
    private int year, month, day;

    private EditText searchDate;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_home);

        searchDate = (EditText) findViewById(R.id.searchDatePicker);

        searchDate.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

            }
        });

    }


    DatePickerDialog.OnDateSetListener d = new DatePickerDialog.OnDateSetListener() {
        @Override
        public void onDateSet(DatePicker datePicker, int i, int i1, int i2) {

        }
    };
}
