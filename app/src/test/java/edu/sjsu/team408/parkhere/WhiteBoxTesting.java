package edu.sjsu.team408.parkhere;

import org.junit.Test;

import static edu.sjsu.team408.parkhere.SignUpActivity.usernameFromEmail;
import static org.junit.Assert.*;

/**
 * Created by MVP on 11/9/2017.
 */
public class WhiteBoxTesting {

    /**
     * getDate should compute a date string in the format mm/dd/yyyy
     * When month or day is two digit, then nothing needs to be done
     * In this case, should return '10/16/2018'
     */
    @Test
    public void testStringDateDoubleDigitDate() {
        int year = 2018;
        int month = 10;
        int day = 16;

        String computedString = HomeFragment.getDate(year, month, day);
        String expectedString = "10-16-2018";
        assertEquals(computedString, expectedString);
    }

    /**
     * getDate should compute a date string in the format mm/dd/yyyy
     * When month or day is one digit, a preceding '0' should be added
     * In this case, should return '01/09/2018'
     */
    @Test
    public void testStringDateDoubleSingleDate() {
        int year = 2018;
        int month = 1;
        int day = 9;

        String computedString = HomeFragment.getDate(year, month, day);
        String expectedString = "01-09-2018";
        assertEquals(computedString, expectedString);
    }

    @Test
    public void testUserNameFromEmail() {
        String computedString, expectedString;
        computedString = usernameFromEmail("huy.nguyen123@sjsu.edu");
        expectedString = "huy.nguyen123";
        assertEquals(computedString, expectedString);
    }

    @Test
    public void testUserNameFromEmailInvalidFormat() {
        String computedString, expectedString;
        computedString = usernameFromEmail("huy.nguyen123sjsu.edu");
        expectedString = "huy.nguyen123sjsu.edu";
        assertEquals(computedString, expectedString);
    }

    @Test
    public void getAddressLatLng() {
        String computedString, expectedString;
        Address address = new Address("2000 Senter Rd", "San Jose", "CA", "95111", 37.312646, -121.851722);

        computedString = address.getFullLatLngString();
        expectedString = "37.312646, -121.851722";
        assertEquals(computedString, expectedString);
    }
}
