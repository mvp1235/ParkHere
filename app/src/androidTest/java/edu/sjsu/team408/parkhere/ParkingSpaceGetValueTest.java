package edu.sjsu.team408.parkhere;

import android.support.test.runner.AndroidJUnit4;
import android.test.ActivityUnitTestCase;
import com.google.android.gms.maps.model.LatLng;
import org.junit.Test;
import org.junit.runner.RunWith;
/**
 * Created by DuocNguyen on 11/8/17.
 */

@RunWith(AndroidJUnit4.class)
public class ParkingSpaceGetValueTest extends ActivityUnitTestCase<MainActivity>{
    public ParkingSpaceGetValueTest() {
        super(MainActivity.class);
    }


    @Test
    /**
     * Tests the helper function getValue() in NewListingActivity.java class.
     * This test should return a ParkingSpace with data value.
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

        Address a = new Address(address,point);
        User u = new User();
        u.setName(ownerName);
        ParkingSpace expectedParkingSpace = new ParkingSpace(a, u, parkingImageUrl, specialInstruction, startDate, endDate, startTime, endTime, Double.parseDouble(price));

        assertEquals(expectedParkingSpace, NewListingActivity.getValue(startDate, endDate, startTime, endTime, userID, ownerName, price, address, point ));
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
        assertNull(NewListingActivity.getValue(startDate, endDate, startTime, endTime, userID, ownerName, price, address, point ));
    }

}
