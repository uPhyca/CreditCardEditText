package com.uphyca.creditcardedittext

import android.view.View
import android.widget.TextView
import androidx.test.espresso.matcher.BoundedMatcher
import org.hamcrest.Description
import org.hamcrest.Matcher

object ViewMatchers {

    @JvmStatic
    fun withSelection(index: Int): Matcher<View> {
        return object : BoundedMatcher<View, TextView>(TextView::class.java) {

            override fun describeTo(description: Description) {
                description.appendText("with selection: $index")
            }

            override fun matchesSafely(item: TextView): Boolean {
                return item.selectionStart == index && item.selectionEnd == index
            }
        }
    }
}
