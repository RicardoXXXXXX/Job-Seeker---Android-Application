package com.example.jobseeker;

import org.junit.Before;
import org.junit.Test;

import static org.junit.Assert.*;

/**
 * This class defines unit tests for the IncreaseNumObserver class.
 */
public class IncreaseNumObserverUnitTest {

    private IncreaseNumObserver observer;

    /**
     * Setup method that initializes the IncreaseNumObserver before each test.
     */
    @Before
    public void setUp() {
        observer = new IncreaseNumObserver();
    }

    /**
     * Test the update method of IncreaseNumObserver by verifying if it increments the input number correctly.
     */
    @Test
    public void testUpdateIncrementsNumber() {
        // Test when the input number is 5
        String result = observer.update(5);
        assertEquals("6", result);

        // Test when the input number is 100
        result = observer.update(100);
        assertEquals("101", result);

        // Test when the input number is 0
        result = observer.update(0);
        assertEquals("1", result);
    }

    /**
     * Test the empty update method of IncreaseNumObserver to ensure no exception is thrown.
     */
    @Test
    public void testEmptyUpdateMethod() {
        // Given the method does nothing, we can just call it to ensure no exception is thrown
        observer.update();
    }
}
