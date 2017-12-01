package edu.sjsu.team408.parkhere;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by MVP on 11/28/2017.
 */

public class ParkingSpace implements Parcelable {

    private Address address;
    private User owner;
    private String parkingImageUrl;
    private String specialInstruction;
    private String parkingID;
    private ArrayList<String> reviews;
    private double totalRating;
    private double averageRating;



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
        reviews = new ArrayList<>();
        totalRating = 0;
        averageRating = 0;
    }

    public void addRatingAndCalculate(double newRating) {
        totalRating += newRating;
        averageRating = totalRating/reviews.size();
    }

    public double getTotalRating() {
        return totalRating;
    }

    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    public double getAverageRating() {
        return averageRating;
    }

    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    public void addToReviewList(String reviewID, double newRating, double oldRating) {
        if (reviews == null)
            reviews = new ArrayList<>();

        //only add review id if it doesn't exist
        if (!reviews.contains(reviewID)) {  //adding a brand new review
            reviews.add(reviewID);
            totalRating += newRating;
            averageRating = totalRating/reviews.size();
        } else {    //updating an existing review
            totalRating -= oldRating;    //subtract old rating first
            totalRating += newRating;   //add new rating to total
            averageRating = totalRating/reviews.size(); //calculate new average value
        }
    }

    public ArrayList<String> getReviews() {
        return reviews;
    }

    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
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
