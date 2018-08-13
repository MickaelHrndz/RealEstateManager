package com.openclassrooms.realestatemanager.fragments

import android.arch.lifecycle.MutableLiveData
import android.arch.lifecycle.ViewModelProviders
import android.content.Context
import android.content.SharedPreferences
import android.databinding.DataBindingUtil
import android.databinding.ViewDataBinding
import android.os.Bundle
import android.support.v4.app.Fragment
import android.text.Editable
import android.text.TextWatcher
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RadioButton
import android.widget.SeekBar
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.openclassrooms.realestatemanager.BR
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import com.openclassrooms.realestatemanager.viewmodels.FiltersViewModel
import kotlinx.android.synthetic.main.fragment_search.*
import org.w3c.dom.Text
import java.text.SimpleDateFormat
import java.util.*

/**
 * Created by Mickael Hernandez on 03/07/2018.
 */
class SearchFragment : Fragment() {
    private lateinit var viewModel : FiltersViewModel
    private lateinit var binding: ViewDataBinding
    private lateinit var sharedPrefs: SharedPreferences

    private val df = SimpleDateFormat(EditPropertyFragment.datePattern, Locale.getDefault())

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        sharedPrefs = context!!.getSharedPreferences(MainActivity.SHARED_PREFS, Context.MODE_PRIVATE)
        viewModel = ViewModelProviders.of(activity!!).get(FiltersViewModel::class.java)
        binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.fragment_search, container, false)
        binding.setVariable(BR.filters, viewModel)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        // Search filter
        val fltr = viewModel.filter

        // Availability
        search_radio_availability.findViewById<RadioButton>(fltr.availability.value!!).isChecked = true
        search_radio_availability.setOnCheckedChangeListener { _, i ->
            fltr.availability.value = i
        }
        fltr.availability.observeForever {
            if(it != null && search_radio_availability != null){
                val radioBtn = search_radio_availability.findViewById<RadioButton>(it)
                if(!radioBtn.isChecked){
                    radioBtn.isChecked = true
                }
            }
        }

        // Type
        search_edit_type.addTextChangedListener(textWatcherWithStringLiveData(fltr.type))

        // Location
        search_edit_location.addTextChangedListener(textWatcherWithStringLiveData(fltr.location))

        // Price
        search_low_price.addTextChangedListener(textWatcherWithIntLiveData(fltr.lowPrice))
        search_high_price.addTextChangedListener(textWatcherWithIntLiveData(fltr.highPrice))

        // Surface
        search_low_surface.addTextChangedListener(textWatcherWithIntLiveData(fltr.lowSurface))
        search_high_surface.addTextChangedListener(textWatcherWithIntLiveData(fltr.highSurface))

        // Rooms
        search_low_rooms.addTextChangedListener(textWatcherWithIntLiveData(fltr.lowRooms))
        search_high_rooms.addTextChangedListener(textWatcherWithIntLiveData(fltr.highRooms))

        // Pictures
        search_low_pictures.addTextChangedListener(textWatcherWithIntLiveData(fltr.lowPictures))
        search_high_pictures.addTextChangedListener(textWatcherWithIntLiveData(fltr.highPictures))

        // Entry date
        search_edit_entry.addTextChangedListener(textWatcherWithDateLiveData(fltr.entryDate))

        // Sale date
        search_edit_sale.addTextChangedListener(textWatcherWithDateLiveData(fltr.saleDate))

        // Filters reset button
        search_reset_button.setOnClickListener {
            fltr.reset()
        }

        // Finish fragment by clicking away
        search_overlay.setOnClickListener {}
        search_parent.setOnClickListener {
            finish()
        }
    }

    /** Removes this fragment */
    private fun finish(){
        //(context as MainActivity).mAdapter.filter(viewModel.filter)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    private fun textWatcherWithIntLiveData(ld: MutableLiveData<Int>?) : TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                val str = charSeq.toString()
                if(str.isNotEmpty()){
                    ld?.value = str.toInt()
                }
            }
        }
    }

    private fun textWatcherWithStringLiveData(ld: MutableLiveData<String>?) : TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ld?.value = charSeq.toString()
            }

        }
    }

    private fun textWatcherWithDateLiveData(ld: MutableLiveData<Date>?) : TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                try {
                    val date = df.parse(charSeq.toString())
                    ld?.value = date
                } catch (e : Exception){
                    e.printStackTrace()
                    ld?.value = null
                }
            }

        }
    }

}