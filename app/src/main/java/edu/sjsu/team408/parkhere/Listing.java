package edu.sjsu.team408.parkhere;

import android.os.Bundle;
import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by MVP on 10/31/17.
 * Models listing object
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

    /**
     * Construct an empty listing
     */
    public Listing(){}

    /**
     * Construct a new listing
     * @param id    Listing ID
     * @param address   Listing address
     * @param owner Listing Ower
     * @param parkingImageUrl Listing Parking Image
     * @param specialInstruction Spectial instruction for listing
     * @param startDate Listing start date
     * @param endDate Listing end date
     * @param startTime Listing start Time
     * @param endTime Listing end time
     * @param price Price to reserve parking space
     * @param parkingIDRef Parking ID ref from Database
     */
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

    /**
     * Create listing from bundle input
     * @param b Bundle containing listing parameters to create new listing
     */
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

    /**
     * Listing parcelable object
     * @param in Parcelable object
     */
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

    /**
     * Creates new listing
     */
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

    /**
     * Get listing ID
     * @return Listing ID
     */
    public String getId() {
        return id;
    }

    /**
     * Set listing ID
     * @param id Listing ID
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets parking space ID ref after splitting listing
     * @return The parking ID ref
     */
    public String getParkingIDRef() {
        return parkingIDRef;
    }

    /**
     * Set parking space ID ref after splitting listing
     * @param parkingIDRef
     */
    public void setParkingIDRef(String parkingIDRef) {
        this.parkingIDRef = parkingIDRef;
    }

    /**
     * Gets the listing address
     * @return Listing address object
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Set the listing address
     * @param address The Listing address object
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Get listing owner
     * @return The owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Set listing owner
     * @param owner The owner
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Get parking image url
     * @return image url
     */
    public String getParkingImageUrl() {
        return parkingImageUrl;
    }

    /**
     * Set parking image url
     * @param parkingImageUrl The image url
     */
    public void setParkingImageUrl(String parkingImageUrl) {
        this.parkingImageUrl = parkingImageUrl;
    }

    /**
     * Get special instruction of parking space
     * @return
     */
    public String getSpecialInstruction() {
        return specialInstruction;
    }

    /**
     * Set special instruction of listing
     * @param specialInstruction Special instruction
     */
    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    /**
     * Get the start date of listing
     * @return The start date
     */
    public String getStartDate() {
        return startDate;
    }

    /**
     * Set start date of listing
     * @param startDate the start date
     */
    public void setStartDate(String startDate) {
        this.startDate = startDate;
    }

    /**
     * Get end date of listing
     * @return The end date
     */
    public String getEndDate() {
        return endDate;
    }

    /**
     * Set end date of listing
     * @param endDate The end date
     */
    public void setEndDate(String endDate) {
        this.endDate = endDate;
    }

    /**
     * Get the listing price
     * @return The price
     */
    public double getPrice() {
        return price;
    }

    /**
     * Set the listing price
     * @param price the price to set
     */
    public void setPrice(double price) {
        this.price = price;
    }

    /**
     * Get the start time of of listing
     * @return The start time
     */
    public String getStartTime() {return this.startTime;}

    /**
     * Set the start time of listing
     * @param startTime The start time to set
     */
    public void setStartTime(String startTime) {this.startTime = startTime;}

    /**
     * Get the end time of listing
     * @return The end time
     */
    public String getEndTime() { return this.endTime;}

    /**
     * Set the end time of listing
     * @param endTime The end time to set
     */
    public void setEndTime(String endTime) {this.endTime = endTime;}

    /**
     * Set the owner parking ID
     * @param id Owner ID to set
     */
    public void setOwnerParkingID(String id) {this.ownerParkingID = id;}

    /**
     * Get the owner parking ID
     * @return The owner ID
     */
    public String getOwnerParkingID() {return this.ownerParkingID;}

    /**
     * Set listing reserved by
     * @param seeker The seeker to reserve listing
     */
    public void setReservedBy(User seeker) {
        this.reservedBy = seeker;
    }

    /**
     * Get the user that reserve the listing
     * @return The seeker that reserve listing
     */
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

    /**
     * Clone the listing
     * @return The clone of listing
     */
    public Listing clone() {
        Listing p = new Listing(id, address, owner, parkingImageUrl, specialInstruction, startDate, endDate, startTime,
                endTime, price, parkingIDRef);
        p.setOwnerParkingID(this.ownerParkingID);
        p.setReservedBy(this.reservedBy);
        return p;
    }

    /**
     * Describe listing in a string.
     * @return
     */
    public String toString() {
        return address.toString() + " " + owner.toString() + " " + startDate + " " + endDate + " "+ startTime + " " + endTime + "" + price;
    }

    @Override
    public boolean equals(Object obj) {
        if (obj != null && obj instanceof Listing) {
            Listing l = (Listing) obj;
            return this.id.equalsIgnoreCase(l.getId());
        } else {
            return false;
        }
    }

    @Override
    public int hashCode() {
        return id.hashCode();
    }
}
