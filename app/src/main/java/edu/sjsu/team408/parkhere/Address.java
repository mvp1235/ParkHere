package edu.sjsu.team408.parkhere;

/**
 * Parking spaces available on multiple days have the same end date, perhaps set end date to the start date?
 * No State on database
 * Address on detailed parking page is showing null
 */

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

/**
 * Created by MVP on 10/31/17.
 */

public class Address implements Parcelable {
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;

    public Address(){}

    public Address(String streetAddress, String city, String state, String zipCode) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }


    protected Address(Parcel in) {
        streetAddress = in.readString();
        city = in.readString();
        state = in.readString();
        zipCode = in.readString();
    }

    public Address(String address) {
        formatAddress(address);
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
        String address = streetAddress + ", " + city + ", " + state + " " + zipCode;        // state is null for some reason
        Log.i("TEST", state + " SDSDS");    //FIX
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
    }

    //The address should follow this format:
    // One Washington Square, San Jose, CA 95112
    // Token 1 = One Washington Square
    // Token 2 = San Jose
    // Token 3 = CA 95112
    // Separate Token 3 to get state code and zip code
    public void formatAddress(String address){
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


    }
}
