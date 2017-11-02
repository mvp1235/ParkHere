package edu.sjsu.team408.parkhere;

import android.os.Parcel;
import android.os.Parcelable;

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

    public User(){};

    public User(String id, String name, Address address, String phoneNumber, String emailAddress, String profileURL) {
        this.id = id;
        this.name = name;
        this.address = address;
        this.phoneNumber = phoneNumber;
        this.emailAddress = emailAddress;
        this.profileURL = profileURL;
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

}