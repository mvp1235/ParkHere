package edu.sjsu.team408.parkhere;

/**
 * Created by robg on 12/4/17.
 */

import android.content.Intent;
import android.location.Location;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.widget.LinearLayout;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.Dash;
import com.google.android.gms.maps.model.Dot;
import com.google.android.gms.maps.model.Gap;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PatternItem;
import com.google.android.gms.maps.model.Polygon;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.firebase.database.DataSnapshot;
import com.google.firebase.database.DatabaseError;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;
import com.google.firebase.database.ValueEventListener;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

/**
 * An activity that displays a Google map with a marker (pin) to indicate a particular location.
 */
public class SearchInMapActivity extends AppCompatActivity
        implements OnMapReadyCallback,
        GoogleMap.OnPolygonClickListener {
//    private ArrayList<Listing> foundListings;
//    private ArrayList<String> foundListingsParkingID;
//    private Location mLocation;
    private DatabaseReference mDatabase;

    private static final float ONE_MILES_RANGE = 1609.34f;
    private static final int COLOR_BLACK_ARGB = 0xff000000;
    private static final int COLOR_WHITE_ARGB = 0xffffffff;
    private static final int COLOR_RED_ARGB = 0xffff0000;
    private static final int COLOR_YELLOW_ARGB = 0xffffff00;
    private static final int COLOR_ORANGE_ARGB = 0xffF57F17;
    private static final int COLOR_GREEN_ARGB = 0xff388E3C;
    private static final int COLOR_PURPLE_ARGB = 0xff81C784;
    private static final int COLOR_BLUE_ARGB = 0xffF9A825;

    private static final int POLYLINE_STROKE_WIDTH_PX = 12;
    private static final int POLYGON_STROKE_WIDTH_PX = 8;
    private static final int PATTERN_DASH_LENGTH_PX = 20;
    private static final int PATTERN_GAP_LENGTH_PX = 20;
    private static final PatternItem DOT = new Dot();
    private static final PatternItem DASH = new Dash(PATTERN_DASH_LENGTH_PX);
    private static final PatternItem GAP = new Gap(PATTERN_GAP_LENGTH_PX);

    // Create a stroke pattern of a gap followed by a dot.
    private static final List<PatternItem> PATTERN_POLYLINE_DOTTED = Arrays.asList(GAP, DOT);

    // Create a stroke pattern of a gap followed by a dash.
    private static final List<PatternItem> PATTERN_POLYGON_ALPHA = Arrays.asList(GAP, DASH);

    // Create a stroke pattern of a dot followed by a gap, a dash, and another gap.
    private static final List<PatternItem> PATTERN_POLYGON_BETA =
            Arrays.asList(DOT, GAP, DASH, GAP);


//    private boolean locationIsWithinInRange(LatLng latLng) {
//        float[] distance = new float[2];
//        Location.distanceBetween(mLocation.getLatitude(), mLocation.getLongitude(),
//                latLng.latitude, latLng.longitude, distance);
//        return distance[0] <= ONE_MILES_RANGE;
//    }
//
//    private boolean theParkingIsInFoundListings(String parkingId) {
//        return foundListingsParkingID.contains(parkingId);
//    }


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

//        Intent data = getIntent();
//        Bundle bundle = data.getExtras();
//        foundListings =
//                (ArrayList<Listing>) bundle.getSerializable("foundLists");
//        foundListingsParkingID = new ArrayList<>();
//        for (Listing listing : foundListings) {
//            foundListingsParkingID.add(listing.getId());
//        }
//        mLocation = (Location) bundle.getParcelable("location");

        // Retrieve the content view that renders the map.
        setContentView(R.layout.activity_maps);
        LinearLayout mapLegend = findViewById(R.id.mapLegend);
        mapLegend.bringToFront();

        mDatabase = FirebaseDatabase.getInstance().getReference();

        // Get the SupportMapFragment and request notification
        // when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
    }

    /**
     * Styles the polygon, based on type.
     * @param polygon The polygon object that needs styling.
     * @param haveBeenBookedCount the count of the parking space having been booked
     */
    private void stylePolygon(Polygon polygon, int haveBeenBookedCount) {
        String type = "";
        // Get the data object stored with the polygon.
        if (polygon.getTag() != null) {
            type = polygon.getTag().toString();
        }

        List<PatternItem> pattern = null;
        int strokeColor = COLOR_BLACK_ARGB;
        int fillColor;
        int countTier1 = 10;
        int countTier2 = 20;
        int countTier3 = 50;
        if (haveBeenBookedCount < countTier1)
            fillColor = COLOR_WHITE_ARGB;
        else if (haveBeenBookedCount < countTier2)
            fillColor = COLOR_YELLOW_ARGB;
        else if (haveBeenBookedCount < countTier3)
            fillColor = COLOR_ORANGE_ARGB;
        else
            fillColor = COLOR_RED_ARGB;

        polygon.setStrokePattern(null);
        polygon.setStrokeWidth(POLYGON_STROKE_WIDTH_PX);
        polygon.setStrokeColor(strokeColor);
        polygon.setFillColor(fillColor);
    }

    /**
     * Manipulates the map when it's available.
     * The API invokes this callback when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user receives a prompt to install
     * Play services inside the SupportMapFragment. The API invokes this method after the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(final GoogleMap googleMap) {
        // Add a marker in Sydney, Australia,
        // and move the map's camera to the same location.

        mDatabase
                .child("ParkingSpaces")
//                    .child(listing.getParkingIDRef())
                .addValueEventListener(
                        new ValueEventListener() {
                            @Override
                            public void onDataChange(DataSnapshot dataSnapshot) {
                                for (DataSnapshot data : dataSnapshot.getChildren()) {
                                    Address address = data.child("address").getValue(Address.class);
//                                    if (theParkingIsInFoundListings(data.getKey())) {
                                    int haveBeenBookedCount;
                                    try {
                                        haveBeenBookedCount = data.child("haveBeenBookedCount").getValue(int.class);
                                    } catch (NullPointerException e) {
                                        haveBeenBookedCount = 0;
                                    }
                                    LatLng latLng = new LatLng(address.getLatitude(), address.getLongitude());
                                    double latitude = address.getLatitude();
                                    double longitude = address.getLongitude();
                                    double increment = 0.0003 / 2;

//                                        if (locationIsWithinInRange(latLng)) {
                                    // Add polygons to indicate areas on the map.
                                    Polygon polygon1 = googleMap.addPolygon(new PolygonOptions()
                                            .clickable(true)
                                            .add(
                                                    new LatLng(latitude - increment, longitude + increment),
                                                    new LatLng(latitude + increment, longitude + increment),
                                                    new LatLng(latitude + increment, longitude - increment),
                                                    new LatLng(latitude - increment, longitude - increment)));
                                    // Style the polygon.
                                    stylePolygon(polygon1, haveBeenBookedCount);


                                    googleMap.addMarker(
                                            new MarkerOptions().position(latLng)
                                                    .title(address.getStreetAddress()));
                                    googleMap.moveCamera(
                                            CameraUpdateFactory.newLatLngZoom(latLng, 17.0f));
//                                        }
//                                    }
                                }
                            }

                            @Override
                            public void onCancelled(DatabaseError databaseError) {

                            }
                        }
                );
//                    .child("address")
//                    .addValueEventListener(
//                            new ValueEventListener() {
//                                @Override
//                                public void onDataChange(DataSnapshot dataSnapshot) {
//                                    Address address = dataSnapshot.getValue(Address.class);
//                                    LatLng latLng =
//                                            new LatLng(address.getLatitude(),
//                                                    address.getLongitude());
//                                    googleMap.addMarker(
//                                            new MarkerOptions().position(latLng)
//                                            .title(address.getStreetAddress()));
//                                    googleMap.moveCamera(
//                                            CameraUpdateFactory.newLatLng(latLng));
//                                }
//
//                                @Override
//                                public void onCancelled(DatabaseError databaseError) {
//
//                                }
//                            }
//                    );
//        }
//        LatLng sydney = new LatLng(-33.852, 151.211);
//        googleMap.addMarker(new MarkerOptions().position(sydney)
//                .title("Marker in Sydney"));
//        googleMap.moveCamera(CameraUpdateFactory.newLatLng(sydney));
    }

    @Override
    public void onPolygonClick(Polygon polygon) {

    }
}