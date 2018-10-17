package com.openclassrooms.realestatemanager

import android.support.design.widget.CoordinatorLayout
import android.support.test.InstrumentationRegistry.getInstrumentation
import android.support.test.espresso.Espresso
import android.support.test.espresso.Espresso.onData
import android.support.test.espresso.Espresso.onView
import android.support.test.espresso.action.ViewActions.click
import android.support.test.espresso.action.ViewActions.replaceText
import android.support.test.espresso.contrib.DrawerActions
import android.support.test.espresso.contrib.NavigationViewActions
import android.support.test.espresso.contrib.RecyclerViewActions
import android.support.test.espresso.matcher.ViewMatchers.withId
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import android.support.v7.widget.RecyclerView
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import com.cielyang.android.clearableedittext.ClearableEditText
import com.google.android.gms.maps.SupportMapFragment
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.adapters.PropertiesListAdapter
import com.openclassrooms.realestatemanager.fragments.EditPropertyFragment
import com.openclassrooms.realestatemanager.fragments.PropertiesMapFragment
import com.openclassrooms.realestatemanager.fragments.PropertyFragment
import com.openclassrooms.realestatemanager.utils.Utils
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.*
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
        Thread.sleep(softDelay)
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
        assertTrue(mActivity.findViewById<RecyclerView>(R.id.recyclerView).adapter.itemCount > 0)
    }

    /** Tests PropertyFragment when clicking on a row */
    @Test
    fun testProperty() {
        // Click on the first item of the list with corresponding id
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PropertiesListAdapter.ViewHolder>(0, click()))

        Thread.sleep(softDelay)

        // Assert that the fragment layout is in the activity view
        assertNotNull(mActivity.findViewById<CoordinatorLayout>(R.id.property_overlay))

        // Assert that the property's type is displayed
        assert(mActivity.findViewById<TextView>(R.id.property_type).text.isNotEmpty())

        // Assert that the property's location is displayed
        assert(mActivity.findViewById<TextView>(R.id.property_location).text.isNotEmpty())

        // Assert that one or more images are displayed
        assert(mActivity.findViewById<LinearLayout>(R.id.pictures_layout).childCount > 0)
    }

    /** Tests PropertyFragment when clicking on a row and then on edit button */
    @Test
    fun testEditProperty() {
        // Click on the first item of the list with corresponding id
        onView(withId(R.id.recyclerView)).perform(RecyclerViewActions.actionOnItemAtPosition<PropertiesListAdapter.ViewHolder>(0, click()))

        Thread.sleep(softDelay)

        // Clicking of the edit button
        onView(withId(R.id.fab_edit)).perform(click())

        // Assert that the fragment layout is in the activity view
        assertNotNull(mActivity.findViewById<CoordinatorLayout>(R.id.editoverlay))

        // Assert that the property's type is displayed
        assert(mActivity.findViewById<TextView>(R.id.editprop_type).text.isNotEmpty())

        // Assert that the property's location is displayed
        assert(mActivity.findViewById<TextView>(R.id.editprop_location).text.isNotEmpty())

        // Assert that one or more images are displayed
        assert(mActivity.findViewById<RecyclerView>(R.id.list_pictures).childCount > 0)
    }

    /** Tests SearchFragment */
    @Test
    fun testSearch() {
        val itemCount = mActivity.findViewById<RecyclerView>(R.id.recyclerView).adapter.itemCount

        // Asserts that there's properties displayed
        assertTrue(itemCount > 0)

        // Clicks on the search button
        onView(withId(R.id.action_search)).perform(click())

        // Add a blocking filter
        onView(withId(R.id.search_edit_type)).perform(replaceText("giokqrgjokrsqgjorsjfofkojq"))

        // Asserts that all properties have been filtered
        assertTrue(mActivity.findViewById<RecyclerView>(R.id.recyclerView).adapter.itemCount == 0)

        // Remove the filter
        onView(withId(R.id.search_edit_type)).perform(replaceText(""))

        // Asserts that all properties have returned to the list
        assertTrue(mActivity.findViewById<RecyclerView>(R.id.recyclerView).adapter.itemCount == itemCount)

    }

    /** Test PropertiesMapFragment */
    @Test
    fun testPropertiesMap() {
        // Open the drawer
        onView(withId(R.id.drawer_layout)).perform(DrawerActions.open())

        // Click on the map action
        onView(withId(R.id.nav_view)).perform(NavigationViewActions.navigateTo(R.id.nav_map))

        // TODO : Find a way to get the map, and test the markers count
    }

    /** Test Internet connection availability */
    @Test
    fun testInternetConnection() {
        assert(Utils.isInternetAvailable(mActivity))
    }

}