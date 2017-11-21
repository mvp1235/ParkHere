package edu.sjsu.team408.parkhere;

/**
 * Created by MVP on 11/21/2017.
 */

public class Review {
    private String id;
    private double stars;
    private String reviewerID;
    private String revieweeID;
    private String description;
    private String parkingID;

    public Review() {};

    public Review(String id, double stars, String reviewerID, String revieweeID, String description, String parkingID) {
        this.id = id;
        this.stars = stars;
        this.reviewerID = reviewerID;
        this.revieweeID = revieweeID;
        this.description = description;
        this.parkingID = parkingID;
    }

    public String getParkingID() {
        return parkingID;
    }

    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public double getStars() {
        return stars;
    }

    public void setStars(double stars) {
        this.stars = stars;
    }

    public String getReviewerID() {
        return reviewerID;
    }

    public void setReviewerID(String reviewerID) {
        this.reviewerID = reviewerID;
    }

    public String getRevieweeID() {
        return revieweeID;
    }

    public void setRevieweeID(String revieweeID) {
        this.revieweeID = revieweeID;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }
}
