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
 * Model for Address
 */
public class Address implements Parcelable {
    private static final double MILES_TO_METER = 1609.344;

    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;
    private double latitude;
    private double longitude;

    /**
     * Default constructor for Address
     */
    public Address() {
    }

    /**
     * Constructor for Address
     * @param streetAddress street number and name of the address
     * @param city city of the address
     * @param state state of the address
     * @param zipCode zip code of the address
     * @param latitude latitude of the address
     * @param longitude longitude of the address
     */
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

    /**
     * Constructor for Address
     * @param in the Parcelable object which contains all necessary information for Address
     */
    protected Address(Parcel in) {
        streetAddress = in.readString();
        city = in.readString();
        state = in.readString();
        zipCode = in.readString();
        latitude = in.readDouble();
        longitude = in.readDouble();
    }

    /**
     * Constructor for Address
     * @param address full address
     * @param point latitude and longitude of the address
     */
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

    /**
     * Gets the full string representation of the address
     * @return full address, e.g. street number and name, city, state, zip code
     */
    public String toString(){
        if(streetAddress == null || city == null || state == null || zipCode == null) {
            return "";
        }
        String address = streetAddress + ", " + city + ", " + state + " " + zipCode;
        return address;
    }

    /**
     * Sets the street address
     * @param streetAddress the street address to be set
     */
    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    /**
     * Sets the city
     * @param city the city to be set
     */
    public void setCity(String city) {
        this.city = city;
    }

    /**
     * Sets the zip code
     * @param zipCode the zip code to be set
     */
    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    /**
     * Sets the state
     * @param state the state to be set
     */
    public void setState(String state) { this.state = state;}

    /**
     * Sets the latitude
     * @param latitude the latitude to be set
     */
    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    /**
     * Sets the longitude
     * @param longitude the longitude to be set
     */
    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    /**
     * Gets the full Latitude/Longitude coordinate
     * @return a string representation of the coordinate, (lat, lng)
     */
    public String getFullLatLngString() {
        return latitude + ", " + longitude;
    }

    /**
     * Gets the state
     * @return state
     */
    public String getState() { return state;}

    /**
     * Gets the street address
     * @return streetAddress
     */
    public String getStreetAddress() {
        return streetAddress;
    }

    /**
     * Gets the city
     * @return city
     */
    public String getCity() {
        return city;
    }

    /**
     * Gets the zip code
     * @return zipCode
     */
    public String getZipCode() {
        return zipCode;
    }

    /**
     * Gets the latitude
     * @return latitude
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * Gets the longitude
     * @return longitude
     */
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

    /**
     * Format the address to the proper format
     * @param address the full address
     * @param point full coordinate of the address
     * The address should follow this format:
     * One Washington Square, San Jose, CA 95112
     * Token 1 = One Washington Square
     * Token 2 = San Jose
     * Token 3 = CA 95112
     * Separate Token 3 to get state code and zip code
     */
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


    /**
     * Calculates the distance between the current location to the specified location
     * @param thatLocation the other location to be used
     * @return the distace from the current location to the specified location
     */
    public double getDistanceBetweenThisAnd(Location thatLocation) {
        Location thisLocation = new Location("");

        thisLocation.setLatitude(this.latitude);
        thisLocation.setLongitude(this.longitude);

        return thisLocation.distanceTo(thatLocation) / MILES_TO_METER;
    }
}
