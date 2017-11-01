package edu.sjsu.team408.parkhere;

/**
 * Created by MVP on 10/31/17.
 */

public class ParkingSpace {
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
}
