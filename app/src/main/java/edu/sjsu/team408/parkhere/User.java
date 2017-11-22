package edu.sjsu.team408.parkhere;

import android.os.Parcel;
import android.os.Parcelable;

import java.lang.reflect.Array;
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
    private ArrayList<ParkingSpace> myCurrentReservedParkings;
    private ArrayList<ParkingSpace> myListingHistory;
    private ArrayList<ParkingSpace> myReservationList;
    private ArrayList<Review> myReviews;

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

    public void addReservedParking(ParkingSpace p) {
        if(myCurrentReservedParkings == null) {
            myCurrentReservedParkings = new ArrayList<ParkingSpace>();
        }
        myCurrentReservedParkings.add(p);
    }

    public ArrayList<ParkingSpace> getMyCurrentReservedParkings() {
        return myCurrentReservedParkings;
    }

    public void addToListingHistory(ArrayList<ParkingSpace> newList) {
        if(myListingHistory == null) {
            this.myListingHistory = newList;
        } else {
            ArrayList<ParkingSpace> newListHistory = newList;
            for (ParkingSpace ps : myListingHistory) {
                newList.add(ps);
            }
            this.myListingHistory = newListHistory;
        }
    }

    public ArrayList<ParkingSpace> getMyListingHistory() {return this.myListingHistory;}

    public void setMyListingHistory(ArrayList<ParkingSpace> myListingHistory) {
        this.myListingHistory = myListingHistory;
    }

    public void setMyCurrentReservedParkings(ArrayList<ParkingSpace> myCurrentReservedParkings){
        this.myCurrentReservedParkings = myCurrentReservedParkings;
    }

    public ArrayList<ParkingSpace> getMyReservationList(){
        return this.myReservationList;
    }

    public void setMyReservationList(ArrayList<ParkingSpace> myReservationList){
        this.myReservationList = myReservationList;
    }
    public void addToMyReservetionList(ParkingSpace p) {
        if(myReservationList == null) {
            myReservationList = new ArrayList<ParkingSpace>();
        }
        myReservationList.add(p);
    }

    public User clone(){
        return new User(id, name, address, phoneNumber, emailAddress,profileURL);
    }

    public String toString () {
        return getName();
    }

    public ArrayList<Review> getMyReviews() {
        return myReviews;
    }

    public void setMyReviews(ArrayList<Review> myReviews) {
        this.myReviews = myReviews;
    }

    public void addToReviewList(Review review) {
        myReviews.add(review);
    }

}
