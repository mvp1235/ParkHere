package edu.sjsu.team408.parkhere;

import android.os.Parcel;
import android.os.Parcelable;

import java.util.ArrayList;

/**
 * Created by MVP on 11/28/2017.
 * Models a Parking Space Object
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


    /**
     * Parking Space constructor for parcelable
     * @param in Parcel object to construct parking space
     */
    protected ParkingSpace(Parcel in) {
        address = in.readParcelable(Address.class.getClassLoader());
        owner = in.readParcelable(User.class.getClassLoader());
        parkingImageUrl = in.readString();
        specialInstruction = in.readString();
        parkingID = in.readString();
    }

    /**
     * Construct an empty parking space.
     */
    public ParkingSpace() {}


    /**
     * Construct a parking space
     * @param address   Parking space address
     * @param owner     Parking space owner
     * @param parkingImageUrl   Parking space image
     * @param specialInstruction    Special instruction for parking space
     * @param parkingID Parking space ID
     * @param ownerID   Parking space owner ID
     */
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

    /**
     * Calculate parking space rating
     * @param newRating New rating on parking space
     */
    public void addRatingAndCalculate(double newRating) {
        totalRating += newRating;
        averageRating = totalRating/reviews.size();
    }

    /**
     * Gets the total number of rating on parking space
     * @return  Total number of ratings on parking space
     */
    public double getTotalRating() {
        return totalRating;
    }

    /**
     * Set the total number of rating on parking space
     * @param totalRating Number of rating to set.
     */
    public void setTotalRating(double totalRating) {
        this.totalRating = totalRating;
    }

    /**
     * gets the average rating on parking space
     * @return The average rating
     */
    public double getAverageRating() {
        return averageRating;
    }

    /**
     * Set the average rating on parking space
     * @param averageRating The average rating to set
     */
    public void setAverageRating(double averageRating) {
        this.averageRating = averageRating;
    }

    /**
     * Add review rating to parking space
     * @param reviewID  The review ID
     * @param newRating The new rating on parking space
     * @param oldRating The old rating on parking space
     */
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

    /**
     * Gets an arraylist of reviews on parking space
     * @return An arraylist of reviews
     */
    public ArrayList<String> getReviews() {
        return reviews;
    }

    /**
     * Set reviews on parking space
     * @param reviews An list of all reviews
     */
    public void setReviews(ArrayList<String> reviews) {
        this.reviews = reviews;
    }

    /**
     * Gets the parking space address
     * @return Parking space address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Set parking space address
     * @param address Address to set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Get the owner of the parking space
     * @return The owner
     */
    public User getOwner() {
        return owner;
    }

    /**
     * Set the owner of parking space
     * @param owner The owner
     */
    public void setOwner(User owner) {
        this.owner = owner;
    }

    /**
     * Get parking space image url
     * @return The url to parking image
     */
    public String getParkingImageUrl() {
        return parkingImageUrl;
    }

    /**
     * Set parking image url to parking space
     * @param parkingImageUrl Url to parking image
     */
    public void setParkingImageUrl(String parkingImageUrl) {
        this.parkingImageUrl = parkingImageUrl;
    }

    /**
     * Gets the special instruction of parking space
     * @return Special instruction
     */
    public String getSpecialInstruction() {
        return specialInstruction;
    }

    /**
     * Sets the special instruction to parking space
     * @param specialInstruction Special instruction
     */
    public void setSpecialInstruction(String specialInstruction) {
        this.specialInstruction = specialInstruction;
    }

    /**
     * Gets parking ID of parking space
     * @return Parking ID as string
     */
    public String getParkingID() {
        return parkingID;
    }

    /**
     * Set parking ID of parking space
     * @param parkingID Set parking space ID
     */
    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }

    /**
     * Creates parking space
     */
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
