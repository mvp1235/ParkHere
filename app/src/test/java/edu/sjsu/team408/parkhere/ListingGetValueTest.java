package edu.sjsu.team408.parkhere;

import com.google.android.gms.maps.model.LatLng;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * Created by DuocNguyen on 11/8/17.
 */
public class ListingGetValueTest {
    @Test
    /**
     * Tests the helper function getValue() in NewListingActivity.java class.
     * This test should return a Listing with data value.
     */
    public void getParkingSpaceDataValue1() throws Exception {
        String startDate = "10-10-2017";
        String endDate = "10-10-2017";
        String startTime = "12:00";
        String endTime = "15:00";
        String userID = "4DJped4PuEf5OR2gD2We8gECfD82";
        String ownerName = "Duoc Nguyen";
        String price = "100";
        String address = "2770 Bristol Dr, San Jose, CA 95127";
        LatLng point = null; //don't really care about this because JUnit test is isolate. Not going to use Google API
        String parkingImageUrl = "https://media-cdn.tripadvisor.com/media/photo-s/0f/ae/73/2f/private-parking-right.jpg";   //default for testing
        String specialInstruction = ""; //null default
        String parkingID = "-Kz-f056Hj-V47CpDlPM"; //a random value

        Address a = new Address(address,point);
        User u = new User();
        u.setName(ownerName);
        Listing expectedListing = new Listing(a, u, parkingImageUrl, specialInstruction, startDate, endDate, startTime, endTime, Double.parseDouble(price), parkingID);
        String expected = expectedListing.toString();

        assertEquals(expected, NewListingActivity.getValue(startDate, endDate, startTime, endTime, userID, ownerName, price, address, point, parkingID).toString());
    }

    @Test
    /**
     * Tests the helper function getValue() in NewListingActivity.java class.
     * This test should return Null.
     */
    public void getParkingSpaceDataValue2() throws Exception {
        String startDate = "";
        String endDate = "";
        String startTime = "";
        String endTime = "";
        String userID = "";
        String ownerName = "";
        String price = "";
        String address = "";
        LatLng point = null;
        String parkingID = "";
        assertNull(NewListingActivity.getValue(startDate, endDate, startTime, endTime, userID, ownerName, price, address, point, parkingID));
    }

}
