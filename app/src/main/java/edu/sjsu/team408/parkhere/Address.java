package edu.sjsu.team408.parkhere;

import android.os.Parcel;
import android.os.Parcelable;

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
        return streetAddress + ", " + city + ", " + state + " " + zipCode;
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

    public void formatAddress(String address){
        String addressToken[] = address.split(",");
        setStreetAddress(addressToken[0]);
        setCity(addressToken[1]);
        setState(addressToken[2]);
        setZipCode(addressToken[3]);
    }
}
