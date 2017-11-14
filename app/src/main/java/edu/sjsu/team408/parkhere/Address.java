package edu.sjsu.team408.parkhere;

/**
 * Parking spaces available on multiple days have the same end date, perhaps set end date to the start date?
 * No State on database
 * Address on detailed parking page is showing null
 */

import android.location.Geocoder;
import android.location.Location;
import android.os.Parcel;
import android.os.Parcelable;
import android.widget.Toast;

import com.google.android.gms.maps.model.LatLng;

import java.io.IOException;
import java.util.List;

/**
 * Created by MVP on 10/31/17.
 */

public class Address implements Parcelable {
    private static final double MILES_TO_METER = 1609.344;

    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private double latitude;
    private double longitude;

    public Address() {
    }

    public Address(String streetAddress, String city, String state, String zipCode,
                   double latitude, double longitude) {
        if(streetAddress.isEmpty() || city.isEmpty() || state.isEmpty() || zipCode.isEmpty()) {
            return;
        }
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
        this.latitude = latitude;
        this.longitude = longitude;
    }

    protected Address(Parcel in) {
        streetAddress = in.readString();
        city = in.readString();
        state = in.readString();
        zipCode = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }


    public Address(String address, LatLng point) {
        formatAddress(address, point);
    }

    public static final Creator<Address> CREATOR = new Creator<Address>() {
        @Override
        public Address createFromParcel(Parcel in) {
            return new Address(in);
        }

        @Override
        public Address[] newArray(int size) {
            return new Address[size];
        }
    };

    public String toString(){
        if(streetAddress == null || city == null || state == null || zipCode == null) {
            return "";
        }
        String address = streetAddress + ", " + city + ", " + state + " " + zipCode;
        return address;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public void setState(String state) { this.state = state;}

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public String getFullLatLngString() {
        return latitude + ", " + longitude;
    }

    public String getState() { return state;}

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }

    public double getLatitude() {
        return latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    /**
     * Describe the kinds of special objects contained in this Parcelable
     * instance's marshaled representation. For example, if the object will
     * include a file descriptor in the output of {@link #writeToParcel(Parcel, int)},
     * the return value of this method must include the
     * {@link #CONTENTS_FILE_DESCRIPTOR} bit.
     *
     * @return a bitmask indicating the set of special object types marshaled
     * by this Parcelable object instance.
     */
    @Override
    public int describeContents() {
        return 0;
    }

    /**
     * Flatten this object in to a Parcel.
     *
     * @param dest  The Parcel in which the object should be written.
     * @param flags Additional flags about how the object should be written.
     *              May be 0 or {@link #PARCELABLE_WRITE_RETURN_VALUE}.
     */
    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(streetAddress);
        dest.writeString(city);
        dest.writeString(state);
        dest.writeString(zipCode);
        dest.writeDouble(latitude);
        dest.writeDouble(longitude);
    }

    //The address should follow this format:
    // One Washington Square, San Jose, CA 95112
    // Token 1 = One Washington Square
    // Token 2 = San Jose
    // Token 3 = CA 95112
    // Separate Token 3 to get state code and zip code
    public void formatAddress(String address, LatLng point){
        if(address.isEmpty() || point == null) {
            return;
        }
        String addressToken[] = address.split(",");
        String stateAndZipCode[] = addressToken[2].trim().split(" ");

        String streetAddress = addressToken[0].trim();
        String city = addressToken[1].trim();
        String state = stateAndZipCode[0].trim();
        String zipcode = stateAndZipCode[1].trim();

        setStreetAddress(streetAddress);
        setCity(city);
        setState(state);
        setZipCode(zipcode);
        setLatitude(point.latitude);
        setLongitude(point.longitude);
    }



    public double getDistanceBetweenThisAnd(Location thatLocation) {
        Location thisLocation = new Location("");

        thisLocation.setLatitude(this.latitude);
        thisLocation.setLongitude(this.longitude);

        return thisLocation.distanceTo(thatLocation) / MILES_TO_METER;
    }
}
