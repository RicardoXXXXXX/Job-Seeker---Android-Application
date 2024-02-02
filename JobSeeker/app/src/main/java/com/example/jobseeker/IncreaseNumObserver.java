package com.example.jobseeker;

import android.text.Html;
import android.widget.TextView;
import android.widget.Toast;

/**
 * The IncreaseNumObserver class represents an observer that is responsible for increasing the number of subscribers.
 * This class implements the Observer interface and provides a method to update the number of subscribers.
 */
public class IncreaseNumObserver implements Observer {
    /**
     * Increments the provided number of subscribers by one and returns the updated count as a string.
     * @author Lingpeng Xiao
     * @param subNumInt The current number of subscribers.
     * @return A string representation of the updated number of subscribers after incrementing by one.
     */
    public String update(int subNumInt) {
        // Increase the number of subscribers
        return String.valueOf(subNumInt + 1);
    }

    /**
     * This method is implemented from the Observer interface but does not perform any action.
     * It serves as a placeholder for future functionality.
     */
    @Override
    public void update() {
        // Placeholder method for future functionality (no action).
    }
}

