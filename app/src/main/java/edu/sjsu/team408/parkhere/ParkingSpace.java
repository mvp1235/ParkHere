package edu.sjsu.team408.parkhere;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MVP on 11/28/2017.
 */

public class ParkingSpace implements Parcelable {

    private Address address;
    private User owner;
    private String parkingImageUrl;
    private String specialInstruction;
    private String parkingID;


    protected ParkingSpace(Parcel in) {
        address = in.readParcelable(Address.class.getClassLoader());
        owner = in.readParcelable(User.class.getClassLoader());
        parkingImageUrl = in.readString();
        specialInstruction = in.readString();
        parkingID = in.readString();
    }

    public ParkingSpace() {}

    public ParkingSpace(Address address, User owner, String parkingImageUrl, String specialInstruction, String parkingID, String ownerID) {
        this.address = address;
        this.owner = owner;
        this.parkingImageUrl = parkingImageUrl;
        this.specialInstruction = specialInstruction;
        this.parkingID = parkingID;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public String getParkingImageUrl() {
        return parkingImageUrl;
    }

    public void setParkingImageUrl(String parkingImageUrl) {
        this.parkingImageUrl = parkingImageUrl;
    }

    public String getSpecialInstruction() {
        return specialInstruction;
    }

    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    public String getParkingID() {
        return parkingID;
    }

    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }

    public static final Creator<ParkingSpace> CREATOR = new Creator<ParkingSpace>() {
        @Override
        public ParkingSpace createFromParcel(Parcel in) {
            return new ParkingSpace(in);
        }

        @Override
        public ParkingSpace[] newArray(int size) {
            return new ParkingSpace[size];
        }
    };

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
        dest.writeParcelable(address, flags);
        dest.writeParcelable(owner, flags);
        dest.writeString(parkingImageUrl);
        dest.writeString(specialInstruction);
        dest.writeString(parkingID);
    }

    //Use for displaying parking space names in new listing activity
    @Override
    public String toString() {
        return parkingID;
    }
}
