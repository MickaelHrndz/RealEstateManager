package com.openclassrooms.realestatemanager

import android.support.design.widget.CoordinatorLayout
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.adapters.PropertiesListAdapter
import com.openclassrooms.realestatemanager.fragments.PropertyFragment
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.allOf
import org.hamcrest.CoreMatchers.anything
import org.junit.After
import org.junit.Assert.assertNotNull
import org.junit.Assert.assertTrue
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

/**
 * Created by Mickael Hernandez on 15/07/2018.
 */
@RunWith(AndroidJUnit4::class)

class MainActivityTest {
    val softDelay = 500L
    val hardDelay = 5000L

    @Rule
    @JvmField
    var mActivityTestRule = ActivityTestRule(MainActivity::class.java)
    private lateinit var mActivity: MainActivity

    /** Prepares the activity */
    @Before
    fun setUp() {
        mActivity = mActivityTestRule.activity
    }

    /** Finishes the activity  */
    @After
    @Throws(Exception::class)
    fun tearDown() {
        mActivity.finish()
    }

    /** Assert that properties are shown in the list */
    @Test
    fun testPropertiesInList() {
        Thread.sleep(softDelay)
        assertTrue(mActivity.findViewById<RecyclerView>(R.id.recyclerView).adapter.itemCount > 0)
    }

    /** Test the launch of PropertyActivity upon clicking on a row */
    @Test
    fun testProperty() {
        Thread.sleep(softDelay)

        // Click on the first item of the list with corresponding id
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PropertiesListAdapter.ViewHolder>(0, click()))

        Thread.sleep(softDelay)

        // Assert that the fragment layout is in the activity view
        assertNotNull(mActivity.findViewById<CoordinatorLayout>(R.id.property_overlay))
    }


}