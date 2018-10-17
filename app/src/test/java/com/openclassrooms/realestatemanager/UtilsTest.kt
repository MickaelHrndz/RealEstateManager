package com.openclassrooms.realestatemanager

import com.openclassrooms.realestatemanager.utils.Utils
import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mickael Hernandez on 08/08/2018.
 */
class UtilsTest {

    /** Tests the $/€ conversion */
    @Test
    fun testConvertDollarsToEuro(){
        assert(Utils.convertDollarToEuro(0.0) == 0.0)
        val rndVal = Random().nextDouble() * 100
        assert(Utils.convertDollarToEuro(rndVal) == rndVal * Utils.dollarEuro)
    }

    /** Tests the value of today's date */
    @Test
    fun testGetTodayDate(){
        assert(Utils.getTodayDate() == SimpleDateFormat(Utils.dateFormat).format(Date()))
    }

}