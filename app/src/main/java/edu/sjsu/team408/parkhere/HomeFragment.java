package edu.sjsu.team408.parkhere;


import android.app.DatePickerDialog;
import android.app.TimePickerDialog;
import android.app.Dialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TimePicker;
import android.widget.Toast;
import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private static final int VIEW_PARKINGS_CODE = 123;
    private final static int SEARCH_TIME = 1;

    private Button searchBtn;
    private static EditText searchDateET;
    private EditText locationSearchTerm;
    private static EditText searchTimeET;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);  //starts from 0 for no reason
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            String completeDate = getDate(year, month+1, day);
            setDate(completeDate);  // add one to month to get proper month number (0 for january , ...)
        }
    }

    public static class TimePickerFragment extends DialogFragment
            implements TimePickerDialog.OnTimeSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            return new TimePickerDialog(getContext(), this, 12, 0, false);
        }

        @Override
        public void onTimeSet(TimePicker timePicker, int i, int i1) {
            setSearchTime(i,i1);
        }
    }

    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment

        View view = inflater.inflate(R.layout.fragment_home, container, false);
        locationSearchTerm = (EditText) view.findViewById(R.id.locationSearchTerm);


        searchDateET = (EditText) view.findViewById(R.id.searchDate);
        searchDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });

        //Referencing and setting onclick listener for search button
        searchBtn = (Button) view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                String searchQuery = locationSearchTerm.getText().toString();
                searchListing(searchQuery);
            }
        });

        searchTimeET = (EditText) view.findViewById(R.id.searchTime);
        searchTimeET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showTimePickerDialog(v);
            }
        });

        return view;
    }


    public void searchListing(String location) {
        String searchDate = searchDateET.getText().toString();
        String searchTime = searchTimeET.getText().toString();
        if (searchDate.isEmpty()) {
            Toast.makeText(getContext(), "Please select a date.", Toast.LENGTH_SHORT).show();
            return;
        }
        Intent intent = new Intent(getActivity(), SearchResultActivity.class);
        intent.putExtra("date", searchDate);
        intent.putExtra("location", location);
        intent.putExtra("time", searchTime);
        startActivityForResult(intent, VIEW_PARKINGS_CODE);
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }

    public void showTimePickerDialog(View v) {
        DialogFragment newFragment = new TimePickerFragment();
        newFragment.show(getFragmentManager(), "timePicker");
    }

    public static String getDate(int year, int month, int day) {
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = (month < 10) ? "0" + Integer.toString(month) : Integer.toString(month);
        dayString = (day < 10) ? "0" + Integer.toString(day) : Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        return completeDate;
    }

    public static void setDate(String completeDate) {
        searchDateET.setText(completeDate);
    }

    public static void setSearchTime(int hour, int minute) {
        String hourString, minuteString;
        String ampm = "AM";

        if (hour > 12) {
            hour = hour % 12;
            ampm = "PM";
        } else if (hour == 0) {
            hour = 12;
        } else if (hour == 12) {
            ampm = "PM";
        }

        hourString = Integer.toString(hour);
        minuteString = (minute < 10) ? "0" + Integer.toString(minute) : Integer.toString(minute);
        String completeTime = hourString + ":" + minuteString + " " + ampm;

        searchTimeET.setText(completeTime);
    }
}