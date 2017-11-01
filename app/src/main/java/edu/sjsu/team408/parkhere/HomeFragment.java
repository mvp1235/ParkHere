package edu.sjsu.team408.parkhere;


import android.app.DatePickerDialog;
import android.app.Dialog;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import java.util.Calendar;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {

    private Button searchBtn;
    private static EditText searchDateET;
    private EditText searchTerm;

    public static class DatePickerFragment extends DialogFragment
            implements DatePickerDialog.OnDateSetListener {

        @Override
        public Dialog onCreateDialog(Bundle savedInstanceState) {
            // Use the current date as the default date in the picker
            final Calendar c = Calendar.getInstance();
            int year = c.get(Calendar.YEAR);
            int month = c.get(Calendar.MONTH);
            int day = c.get(Calendar.DAY_OF_MONTH);

            // Create a new instance of DatePickerDialog and return it
            return new DatePickerDialog(getActivity(), this, year, month, day);
        }

        public void onDateSet(DatePicker view, int year, int month, int day) {
            setDate(year, month, day);
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

        //Referencing and setting onclick listener for search button
        searchBtn = (Button) view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                searchTerm = (EditText) getActivity().findViewById(R.id.searchTerm);
                String searchQuery = searchTerm.getText().toString();
                searchListing(searchQuery);
            }
        });

        searchDateET = (EditText) view.findViewById(R.id.searchDate);
        searchDateET.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                showDatePickerDialog(v);
            }
        });


        return view;
    }

    //Only displaying the value of input text for now
    public void searchListing(String location) {
        Toast.makeText(getContext(), location, Toast.LENGTH_SHORT).show();
    }

    public void showDatePickerDialog(View v) {
        DialogFragment newFragment = new DatePickerFragment();
        newFragment.show(getFragmentManager(), "datePicker");
    }


    public static void setDate(int year, int month, int day) {
        String yearString, monthString, dayString;
        yearString = Integer.toString(year);
        monthString = (month < 10) ? "0" + Integer.toString(month) : Integer.toString(month);
        dayString = (day < 10) ? "0" + Integer.toString(day) : Integer.toString(day);

        String completeDate = monthString + "-" + dayString + "-" + yearString;
        searchDateET.setText(completeDate);
    }
}