package edu.sjsu.team408.parkhere;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MVP on 10/31/17.
 */

public class Listing implements Parcelable{

    private String id;
    private Address address;
    private User owner;
    private String parkingImageUrl;
    private String specialInstruction;
    private String startDate;
    private String endDate;
    private String startTime;
    private String endTime;
    private double price;
    private String parkingIDRef;
    private String ownerParkingID;
    private User reservedBy;


    public Listing(){}

    public Listing(String id, Address address, User owner, String parkingImageUrl, String specialInstruction,
                   String startDate, String endDate, String startTime, String endTime, double price, String parkingIDRef) {
        this.id = id;
        this.address = address;
        this.owner = owner;
        this.parkingImageUrl = parkingImageUrl;
        this.specialInstruction = specialInstruction;
        this.startDate = startDate;
        this.endDate = endDate;
        this.price = price;
        this.startTime = startTime;
        this.endTime = endTime;
        this.parkingIDRef = parkingIDRef;
    }

    public Listing(Bundle b) {
        this.id = b.getString(SearchResultActivity.LISTING_ID, "");
        this.address = b.getParcelable(SearchResultActivity.ADDRESS);
        this.owner = b.getParcelable(SearchResultActivity.OWNER);
        this.parkingImageUrl = b.getString(SearchResultActivity.PARKING_IMAGE_URL, "");
        this.specialInstruction = b.getString(SearchResultActivity.SPECIAL_INSTRUCTION, "");
        this.startDate = b.getString(SearchResultActivity.START_DATE, "");
        this.endDate = b.getString(SearchResultActivity.END_DATE, "");
        this.startTime = b.getString(SearchResultActivity.START_TIME, "");
        this.endTime = b.getString(SearchResultActivity.END_TIME, "");
        this.price = b.getDouble(SearchResultActivity.PRICE, 0);
        this.parkingIDRef = b.getString(SearchResultActivity.PARKING_ID_REF, "");
        this.ownerParkingID = b.getString(SearchResultActivity.OWNER_PARKING_ID, "");
        this.reservedBy = b.getParcelable(SearchResultActivity.RESERVE_BY);
    }

    protected Listing(Parcel in) {
        id = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        owner = in.readParcelable(User.class.getClassLoader());
        parkingImageUrl = in.readString();
        specialInstruction = in.readString();
        startDate = in.readString();
        endDate = in.readString();
        price = in.readDouble();
        parkingIDRef = in.readString();
    }

    public static final Creator<Listing> CREATOR = new Creator<Listing>() {
        @Override
        public Listing createFromParcel(Parcel in) {
            return new Listing(in);
        }

        @Override
        public Listing[] newArray(int size) {
            return new Listing[size];
        }
    };

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getParkingIDRef() {
        return parkingIDRef;
    }

    public void setParkingIDRef(String parkingIDRef) {
        this.parkingIDRef = parkingIDRef;
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

    public void setOwnerParkingID(String id) {this.ownerParkingID = id;}

    public String getOwnerParkingID() {return this.ownerParkingID;}

    public void setReservedBy(User seeker) {
        this.reservedBy = seeker;
    }

    public User getReservedBy(){return this.reservedBy;}

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeParcelable(address, flags);
        dest.writeParcelable(owner, flags);
        dest.writeString(parkingImageUrl);
        dest.writeString(specialInstruction);
        dest.writeString(startDate);
        dest.writeString(endDate);
        dest.writeDouble(price);
        dest.writeString(parkingIDRef);
    }

    public Listing clone() {
        Listing p = new Listing(id, address, owner, parkingImageUrl, specialInstruction, startDate, endDate, startTime,
                endTime, price, parkingIDRef);
        p.setOwnerParkingID(this.ownerParkingID);
        p.setReservedBy(this.reservedBy);
        return p;
    }

    public String toString() {
        return address.toString() + " " + owner.toString() + " " + startDate + " " + endDate + " "+ startTime + " " + endTime + "" + price;
    }
}
