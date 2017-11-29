package edu.sjsu.team408.parkhere;

import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.ArrayList;

/**
 * Created by MVP on 10/31/17.
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

    public User(){};

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

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Address getAddress() {
        return address;
    }

    public void setAddress(Address address) {
        this.address = address;
    }

    public String getPhoneNumber() {
        return phoneNumber;
    }

    public void setPhoneNumber(String phoneNumber) {
        this.phoneNumber = phoneNumber;
    }

    public String getEmailAddress() {
        return emailAddress;
    }

    public void setEmailAddress(String emailAddress) {
        this.emailAddress = emailAddress;
    }

    public String getProfileURL() {
        return profileURL;
    }

    public void setProfileURL(String profileURL) {
        this.profileURL = profileURL;
    }

    public void addReservedParking(Listing p) {
        if(myCurrentReservedParkings == null) {
            myCurrentReservedParkings = new ArrayList<Listing>();
        }
        myCurrentReservedParkings.add(p);
    }

    public ArrayList<Listing> getMyCurrentReservedParkings() {
        return myCurrentReservedParkings;
    }

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

    public ArrayList<Listing> getMyListingHistory() {return this.myListingHistory;}

    public void setMyListingHistory(ArrayList<Listing> myListingHistory) {
        this.myListingHistory = myListingHistory;
    }

    public void setMyCurrentReservedParkings(ArrayList<Listing> myCurrentReservedParkings){
        this.myCurrentReservedParkings = myCurrentReservedParkings;
    }

    public ArrayList<Listing> getMyReservationList(){
        return this.myReservationList;
    }

    public void setMyReservationList(ArrayList<Listing> myReservationList){
        this.myReservationList = myReservationList;
    }
    public void addToMyReservetionList(Listing p) {
        if(myReservationList == null) {
            myReservationList = new ArrayList<Listing>();
        }
        myReservationList.add(p);
    }

    public User clone(){
        return new User(id, name, address, phoneNumber, emailAddress,profileURL);
    }

    public String toString () {
        return getName();
    }


    public void addToReviewList(String reviewID) {
        if (myReviews == null)
            myReviews = new ArrayList<>();

        //only add review id if it doesn't exist
        if (!myReviews.contains(reviewID))
            myReviews.add(reviewID);
    }


    public void addToFeedbackList(String reviewID) {
        if (myFeedbacks == null)
            myFeedbacks = new ArrayList<>();

        //only add review id if it doesn't exist
        if (!myFeedbacks.contains(reviewID))
            myFeedbacks.add(reviewID);
    }

    public void addToParkingSpacesList(String pID) {
        if (myParkingSpaces == null)
            myParkingSpaces = new ArrayList<>();

        //only add parkingSpace id if it doesn't exist
        if (!myParkingSpaces.contains(pID))
            myParkingSpaces.add(pID);
    }

    public void deleteFromParkingSpaces(String pID) {
        if (myParkingSpaces == null)
            myParkingSpaces = new ArrayList<>();
        //only add parkingSpace id if it doesn't exist
        if (myParkingSpaces.contains(pID)) {
            myParkingSpaces.remove(pID);
        }
    }

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

    public ArrayList<String> getMyParkingSpaces() {
        if (myParkingSpaces == null)
            myParkingSpaces = new ArrayList<>();
        return myParkingSpaces;
    }

    public void setMyParkingSpaces(ArrayList<String> myParkingSpaces) {
        this.myParkingSpaces = myParkingSpaces;
    }
}
