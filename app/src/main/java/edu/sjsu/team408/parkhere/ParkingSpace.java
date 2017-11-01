package edu.sjsu.team408.parkhere;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

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
    private double price;


    public ParkingSpace(){};

    public ParkingSpace(Address address, User owner, String parkingImageUrl, String specialInstruction, String startDate, String endDate, double price) {
        this.address = address;
        this.owner = owner;
        this.parkingImageUrl = parkingImageUrl;
        this.specialInstruction = specialInstruction;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
    }

    public ParkingSpace(Bundle b) {
        this.address = b.getParcelable(SearchResultActivity.ADDRESS);
        this.owner = b.getParcelable(SearchResultActivity.OWNER);
        this.parkingImageUrl = b.getString(SearchResultActivity.PARKING_IMAGE_URL, "");
        this.specialInstruction = b.getString(SearchResultActivity.SPECIAL_INSTRUCTION, "");
        this.startDate = b.getString(SearchResultActivity.START_DATE, "");
        this.endDate = b.getString(SearchResultActivity.END_DATE, "");
        this.price = b.getDouble(SearchResultActivity.PRICE, 0);
    }

    protected ParkingSpace(Parcel in) {
        address = in.readParcelable(Address.class.getClassLoader());
        owner = in.readParcelable(User.class.getClassLoader());
        parkingImageUrl = in.readString();
        specialInstruction = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        price = in.readDouble();
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

    public Bundle toBundle() {
        Bundle b = new Bundle();


        return b;
    }

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
    }
}
