package edu.sjsu.team408.parkhere;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

import java.security.acl.Owner;

/**
 * Created by MVP on 10/31/17.
 */

public class ParkingSpace implements Parcelable{

    private Address address;
    private User owner;
    private String parkingImageUrl;
    private String specialInstruction;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private double price;
    private String parkingID;


    public ParkingSpace(){}

    public ParkingSpace(Address address, User owner, String parkingImageUrl, String specialInstruction,
                        String startDate, String endDate, String startTime, String endTime, double price, String parkingID) {
        this.address = address;
        this.owner = owner;
        this.parkingImageUrl = parkingImageUrl;
        this.specialInstruction = specialInstruction;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.parkingID = parkingID;
    }

    public ParkingSpace(Bundle b) {
        this.address = b.getParcelable(SearchResultActivity.ADDRESS);
        this.owner = b.getParcelable(SearchResultActivity.OWNER);
        this.parkingImageUrl = b.getString(SearchResultActivity.PARKING_IMAGE_URL, "");
        this.specialInstruction = b.getString(SearchResultActivity.SPECIAL_INSTRUCTION, "");
        this.startDate = b.getString(SearchResultActivity.START_DATE, "");
        this.endDate = b.getString(SearchResultActivity.END_DATE, "");
        this.startTime = b.getString(SearchResultActivity.START_TIME, "");
        this.endTime = b.getString(SearchResultActivity.END_TIME, "");
        this.price = b.getDouble(SearchResultActivity.PRICE, 0);
        this.parkingID = b.getString(SearchResultActivity.PARKING_ID, "");
    }

    protected ParkingSpace(Parcel in) {
        address = in.readParcelable(Address.class.getClassLoader());
        owner = in.readParcelable(User.class.getClassLoader());
        parkingImageUrl = in.readString();
        specialInstruction = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        price = in.readDouble();
        parkingID = in.readString();
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

    public String getParkingID() {
        return parkingID;
    }

    public void setParkingID(String parkingID) {
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

    public String getStartDate() {
        return startDate;
    }

    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    public String getEndDate() {
        return endDate;
    }

    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public String getStartTime() {return this.startTime;}

    public void setStartTime(String startTime) {this.startTime = startTime;}

    public String getEndTime() { return this.endTime;}

    public void setEndTime(String endTime) {this.endTime = endTime;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(address, flags);
        dest.writeParcelable(owner, flags);
        dest.writeString(parkingImageUrl);
        dest.writeString(specialInstruction);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeDouble(price);
        dest.writeString(parkingID);
    }

    public String toString() {
        return address.toString() + " " + owner.toString() + " " + startDate + " " + endDate + " "+ startTime + " " + endTime + "" + price;
    }
}
