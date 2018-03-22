package edu.sjsu.team408.parkhere;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

/**
 * User object model
 */

public class User implements Parcelable{
    private String id;
    private String name;
    private Address address;
    private String phoneNumber;
    private String emailAddress;
    private String profileURL;
    private ArrayList<Listing> myCurrentReservedParkings;
    private ArrayList<Listing> myListingHistory;
    private ArrayList<Listing> myReservationList;
    private ArrayList<String> myFeedbacks;        // feedback reviews received from others who rented my parking space
    private ArrayList<String> myReviews;          // reviews left on other listing owners
    private ArrayList<String> myParkingSpaces;

    /**
     * Default constructor for User
     * Instantiate an empty User object
     */
    public User(){};

    /**
     * Constructor for User
     * @param id id of user
     * @param name name of user
     * @param address physical address of user
     * @param phoneNumber phone number of user
     * @param emailAddress email address of user
     * @param profileURL profile URL of user
     */
    public User(String id, String name, Address address, String phoneNumber, String emailAddress, String profileURL) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.profileURL = profileURL;
        this.myCurrentReservedParkings = new ArrayList<>();     //empty first created
        this.myListingHistory = new ArrayList<>();
        this.myReservationList = new ArrayList<>();
        this.myReviews = new ArrayList<>();
        this.myFeedbacks = new ArrayList<>();
        this.myParkingSpaces = new ArrayList<>();
    }

    /**
     * Constructor for User
     * @param in the Parcel object which contains all information necessary for the User
     */
    protected User(Parcel in) {
        id = in.readString();
        name = in.readString();
        address = in.readParcelable(Address.class.getClassLoader());
        phoneNumber = in.readString();
        emailAddress = in.readString();
        profileURL = in.readString();
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(id);
        dest.writeString(name);
        dest.writeParcelable(address, flags);
        dest.writeString(phoneNumber);
        dest.writeString(emailAddress);
        dest.writeString(profileURL);
    }

    @Override
    public int describeContents() {
        return 0;
    }

    public static final Creator<User> CREATOR = new Creator<User>() {
        @Override
        public User createFromParcel(Parcel in) {
            return new User(in);
        }

        @Override
        public User[] newArray(int size) {
            return new User[size];
        }
    };

    /**
     * Gets the user's ID
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Sets the user's id
     * @param id the new id to be set
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Gets the user's name
     * @return name
     */
    public String getName() {
        return name;
    }

    /**
     * Set the user's name
     * @param name the new name to be set
     */
    public void setName(String name) {
        this.name = name;
    }

    /**
     * Gets the user's address
     * @return address
     */
    public Address getAddress() {
        return address;
    }

    /**
     * Sets the user's address
     * @param address the new address to be set
     */
    public void setAddress(Address address) {
        this.address = address;
    }

    /**
     * Gets the user's phone number
     * @return phoneNumber
     */
    public String getPhoneNumber() {
        return phoneNumber;
    }

    /**
     * Sets the user's phone number
     * @param phoneNumber the new phone number to be set
     */
    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    /**
     * Gets the email address
     * @return emailAddress
     */
    public String getEmailAddress() {
        return emailAddress;
    }

    /**
     * Set the user email address
     * @param emailAddress the new email address to be set
     */
    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    /**
     * Gets the user profile URL
     * @return profileURL
     */
    public String getProfileURL() {
        return profileURL;
    }

    /**
     * Set profile URL to a new URL
     * @param profileURL the URL to be set
     */
    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    /**
     * Add a new reserved parking to the myCurrentReservedParkings list
     * @param p the new parking to be added
     */
    public void addReservedParking(Listing p) {
        if(myCurrentReservedParkings == null) {
            myCurrentReservedParkings = new ArrayList<Listing>();
        }
        myCurrentReservedParkings.add(p);
    }

    /**
     * Gets all the current reserved parkings
     * @return myCurrentReservedParkings
     */
    public ArrayList<Listing> getMyCurrentReservedParkings() {
        return myCurrentReservedParkings;
    }

    /**
     * Add a new listing to the listing history
     * @param newList the new list to be added
     */
    public void addToListingHistory(ArrayList<Listing> newList) {
        if(myListingHistory == null) {
            this.myListingHistory = newList;
        } else {
            ArrayList<Listing> newListHistory = newList;
            for (Listing ps : myListingHistory) {
                newList.add(ps);
            }
            this.myListingHistory = newListHistory;
        }
    }

    /**
     * Remove a listing from myListingHistory
     * @param l the listing to be removed, if existed
     */
    public void deleteFromListingHistory(Listing l) {
        if (myListingHistory.contains(l)) {
            myListingHistory.remove(l);
        }
    }

    /**
     * Gets the listing history of the user
     * @return myListingHistory
     */
    public ArrayList<Listing> getMyListingHistory() {return this.myListingHistory;}

    /**
     * Set the listing history to a new list
     * @param myListingHistory the new list to be set
     */
    public void setMyListingHistory(ArrayList<Listing> myListingHistory) {
        this.myListingHistory = myListingHistory;
    }

    /**
     * Set the current reserved parkings list to a new list
     * @param myCurrentReservedParkings the new list to be added
     */
    public void setMyCurrentReservedParkings(ArrayList<Listing> myCurrentReservedParkings){
        this.myCurrentReservedParkings = myCurrentReservedParkings;
    }

    /**
     * Gets the user's reservation list
     * @return myReservationList
     */
    public ArrayList<Listing> getMyReservationList(){
        return this.myReservationList;
    }

    /**
     * Set the user reservation list to a new list
     * @param myReservationList the new list to be added
     */
    public void setMyReservationList(ArrayList<Listing> myReservationList){
        this.myReservationList = myReservationList;
    }

    /**
     * Add a new listing to the user's reservation list
     * @param p the listing to be added
     */
    public void addToMyReservetionList(Listing p) {
        if(myReservationList == null) {
            myReservationList = new ArrayList<Listing>();
        }
        myReservationList.add(p);
    }

    /**
     * Gets a clone of the user object
     * @return a clone of the user object
     */
    public User clone(){
        return new User(id, name, address, phoneNumber, emailAddress,profileURL);
    }

    /**
     * Gets a string representation of the user's information
     * @return the name of the user
     */
    public String toString () {
        return getName();
    }

    /**
     * Add a new review ID to the current review list
     * @param reviewID the new review ID to be added
     */
    public void addToReviewList(String reviewID) {
        if (myReviews == null)
            myReviews = new ArrayList<>();

        //only add review id if it doesn't exist
        if (!myReviews.contains(reviewID))
            myReviews.add(reviewID);
    }


    /**
     * Add a new review ID to the current feedback list
     * @param reviewID the new review ID to be added
     */
    public void addToFeedbackList(String reviewID) {
        if (myFeedbacks == null)
            myFeedbacks = new ArrayList<>();

        //only add review id if it doesn't exist
        if (!myFeedbacks.contains(reviewID))
            myFeedbacks.add(reviewID);
    }

    /**
     * Add a new parking space ID to the current list
     * @param pID the new parking space ID to be added
     */
    public void addToParkingSpacesList(String pID) {
        if (myParkingSpaces == null)
            myParkingSpaces = new ArrayList<>();

        //only add parkingSpace id if it doesn't exist
        if (!myParkingSpaces.contains(pID))
            myParkingSpaces.add(pID);
    }

    /**
     * Delete a certain parking space ID from the list
     * @param pID the parking ID of the parking space to be deleted
     */
    public void deleteFromParkingSpaces(String pID) {
        if (myParkingSpaces == null)
            myParkingSpaces = new ArrayList<>();
        //only add parkingSpace id if it doesn't exist
        if (myParkingSpaces.contains(pID)) {
            myParkingSpaces.remove(pID);
        }
    }

    /**
     * Gets all of the user's feedback from others
     * @return myFeedbacks
     */
    public ArrayList<String> getMyFeedbacks() {
        return myFeedbacks;
    }

    public void setMyFeedbacks(ArrayList<String> myFeedbacks) {
        this.myFeedbacks = myFeedbacks;
    }
    public ArrayList<String> getMyReviews() {
        return myReviews;
    }

    public void setMyReviews(ArrayList<String> myReviews) {
        this.myReviews = myReviews;
    }

    /**
     * Get all parking space IDs created by the user
     * @return myParkingSpaces
     */
    public ArrayList<String> getMyParkingSpaces() {
        if (myParkingSpaces == null)
            myParkingSpaces = new ArrayList<>();
        return myParkingSpaces;
    }

    /**
     * Set a new list of parking space IDs to the user's parking space list
     * @param myParkingSpaces the new list of parking spaces to be set
     */
    public void setMyParkingSpaces(ArrayList<String> myParkingSpaces) {
        this.myParkingSpaces = myParkingSpaces;
    }
}
