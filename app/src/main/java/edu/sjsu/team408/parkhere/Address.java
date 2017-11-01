package edu.sjsu.team408.parkhere;

/**
 * Created by MVP on 10/31/17.
 */

public class Address {
    private String streetAddress;
    private String city;
    private String state;
    private String zipCode;

    public Address(){}

    public Address(String streetAddress, String city, String state, String zipCode) {
        this.streetAddress = streetAddress;
        this.city = city;
        this.state = state;
        this.zipCode = zipCode;
    }

    public String getFullAddress(){
        return streetAddress + ", " + city + ", " + state + " " + zipCode;
    }

    public void setStreetAddress(String streetAddress) {
        this.streetAddress = streetAddress;
    }

    public void setCity(String city) {
        this.city = city;
    }

    public void setZipCode(String zipCode) {
        this.zipCode = zipCode;
    }

    public String getStreetAddress() {
        return streetAddress;
    }

    public String getCity() {
        return city;
    }

    public String getZipCode() {
        return zipCode;
    }
}
