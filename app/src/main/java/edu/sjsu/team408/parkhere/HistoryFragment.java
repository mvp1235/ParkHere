package edu.sjsu.team408.parkhere;


import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;


/**
 * A simple {@link Fragment} subclass.
 */
public class HistoryFragment extends Fragment {
    private Button bookingBtn, listingBtn;

    /**
     * Required empty public constructor.
     */
    public HistoryFragment() {
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_history, container, false);

        bookingBtn = (Button) view.findViewById(R.id.historyReservedBtn);
        listingBtn = (Button) view.findViewById(R.id.historyListingBtn);

        bookingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewBookings();
            }
        });

        listingBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                viewListings();
            }
        });


        return view;
    }

    /**
     * Start BookingHistoryActivity.
     */
    public void viewBookings() {
        Intent intent = new Intent(getContext(), BookingHistoryActivity.class);
        startActivity(intent);
    }

    /**
     * Start ListingHistoryActivity.
     */
    public void viewListings() {
        Intent intent = new Intent(getContext(), ListingHistoryActivity.class);
        startActivity(intent);
    }

}
