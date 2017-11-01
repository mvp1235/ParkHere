package edu.sjsu.team408.parkhere;


import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;


/**
 * A simple {@link Fragment} subclass.
 */
public class HomeFragment extends Fragment {


    public HomeFragment() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_home, container, false);

        //Referencing and setting onclick listener for search button
        Button searchBtn = (Button) view.findViewById(R.id.searchBtn);
        searchBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                TextView searchTerm = (TextView) getActivity().findViewById(R.id.searchTerm);
                String searchQuery = searchTerm.getText().toString();
                searchListing(searchQuery);
            }
        });


        return view;
    }

    //Only displaying the value of input text for now
    public void searchListing(String location) {
        Toast.makeText(getContext(), location, Toast.LENGTH_SHORT).show();
    }


}
