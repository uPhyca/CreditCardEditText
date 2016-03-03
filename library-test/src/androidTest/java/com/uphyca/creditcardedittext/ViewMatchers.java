package com.uphyca.creditcardedittext;

import android.support.test.espresso.matcher.BoundedMatcher;
import android.view.View;
import android.widget.TextView;

import org.hamcrest.Description;
import org.hamcrest.Matcher;

public class ViewMatchers {

    private ViewMatchers() {
    }

    public static Matcher<View> withSelection(final int index) {
        return new BoundedMatcher<View, TextView>(TextView.class) {
            @Override
            public void describeTo(Description description) {
                description.appendText("with selection: " + index);
            }

            @Override
            protected boolean matchesSafely(TextView item) {
                return item.getSelectionStart() == index
                        && item.getSelectionEnd() == index;
            }
        };
    }
}
