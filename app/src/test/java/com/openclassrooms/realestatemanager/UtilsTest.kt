package com.openclassrooms.realestatemanager

import org.junit.Test
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mickael Hernandez on 08/08/2018.
 */
class UtilsTest {

    @Test
    fun testConvertDollarsToEuro(){
        assert(Utils.convertDollarToEuro(0.0) == 0.0)
        val rndVal = Random().nextDouble() * 100
        assert(Utils.convertDollarToEuro(rndVal) == rndVal * Utils.dollarEuro)
    }

    @Test
    fun testGetTodayDate(){
        assert(Utils.getTodayDate() == SimpleDateFormat(Utils.dateFormat).format(Date()))
    }

}