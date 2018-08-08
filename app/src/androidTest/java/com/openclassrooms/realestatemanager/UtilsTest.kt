package com.openclassrooms.realestatemanager

import android.content.Context
import android.support.test.rule.ActivityTestRule
import android.support.test.runner.AndroidJUnit4
import com.openclassrooms.realestatemanager.activities.MainActivity
import org.junit.After
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import java.text.SimpleDateFormat
import java.util.*
import android.content.Context.CONNECTIVITY_SERVICE
import android.net.ConnectivityManager
import android.net.wifi.WifiManager





/**
 * Created by Mickael Hernandez on 08/08/2018
 * To be tested with Wifi (as we cannot disable data connection)
 * Turns out we also need a permission to change wifi state
 */
/*@RunWith(AndroidJUnit4::class)
class AndroidUtilsTest {

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

    @Test
    fun isInternetAvailable(){
        val wifiManager = mActivity.getSystemService(Context.WIFI_SERVICE) as WifiManager

        // Asserts that internet is available
        assert(Utils.isInternetAvailable(mActivity))

        wifiManager.isWifiEnabled = false

        Thread.sleep(500)

        // Asserts that internet is unavailable
        assert(!Utils.isInternetAvailable(mActivity))

    }

}*/