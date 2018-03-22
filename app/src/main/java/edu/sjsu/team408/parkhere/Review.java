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

    /**
     * An empty constructor.
     */
    public Review() {};

    /**
     * Construct a Review object.
     * @param id the unique id of the review
     * @param stars the star count of the review
     * @param reviewerID the unique id of the reviewer
     * @param revieweeID the unique id of the reviewee
     * @param description the description/content of the review
     * @param parkingID the parkingID associated with the review
     */
    public Review(String id, double stars, String reviewerID, String revieweeID, String description, String parkingID) {
        this.id = id;
        this.stars = stars;
        this.reviewerID = reviewerID;
        this.revieweeID = revieweeID;
        this.description = description;
        this.parkingID = parkingID;
    }

    /**
     * Get the parkingID associated with the review.
     * @return parkingID
     */
    public String getParkingID() {
        return parkingID;
    }

    /**
     * Get the parkingID associated with the review.
     * @param parkingID a unique id of a parking
     */
    public void setParkingID(String parkingID) {
        this.parkingID = parkingID;
    }

    /**
     * Get the unique id of the review.
     * @return id
     */
    public String getId() {
        return id;
    }

    /**
     * Set the unique id of the review.
     * @param id the unique id of the review
     */
    public void setId(String id) {
        this.id = id;
    }

    /**
     * Get the star count of the review.
     * @return stars
     */
    public double getStars() {
        return stars;
    }

    /**
     * Set the star count of the review.
     * @param stars the star count of the review
     */
    public void setStars(double stars) {
        this.stars = stars;
    }

    /**
     * Get the unique id of the reviewer.
     * @return reviewerID
     */
    public String getReviewerID() {
        return reviewerID;
    }

    /**
     * Set the unique id of the reviewer.
     * @param reviewerID the unique id of the reviewer
     */
    public void setReviewerID(String reviewerID) {
        this.reviewerID = reviewerID;
    }

    /**
     * Get the unique id of the reviewee.
     * @return revieweeID
     */
    public String getRevieweeID() {
        return revieweeID;
    }

    /**
     * Set the unique id of the reviewee.
     * @param revieweeID the unique id of the reviewee
     */
    public void setRevieweeID(String revieweeID) {
        this.revieweeID = revieweeID;
    }

    /**
     * Get the description/content of the review.
     * @return a string containing description
     */
    public String getDescription() {
        return description;
    }

    /**
     * Set the description/content of the review.
     * @param description the description/content of the review
     */
    public void setDescription(String description) {
        this.description = description;
    }
}
