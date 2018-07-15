package com.openclassrooms.realestatemanager.fragments

import android.arch.lifecycle.LiveData
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
import android.widget.SeekBar
import com.crystal.crystalrangeseekbar.interfaces.OnRangeSeekbarChangeListener
import com.crystal.crystalrangeseekbar.widgets.CrystalRangeSeekbar
import com.openclassrooms.realestatemanager.BR
import com.openclassrooms.realestatemanager.FiltersViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.activities.MainActivity
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * Created by Mickael Hernandez on 03/07/2018.
 */
class SearchFragment : Fragment() {
    private lateinit var viewModel : FiltersViewModel
    private lateinit var binding: ViewDataBinding
    private lateinit var sharedPrefs: SharedPreferences

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

        val fltr = viewModel.filter

        search_edit_type.addTextChangedListener(textWatcherListenerWithLiveData(fltr.type))
        search_edit_location.addTextChangedListener(textWatcherListenerWithLiveData(fltr.location))

        setUpRangeBar(range_price, fltr.price)
        setUpRangeBar(range_surface, fltr.surface)
        setUpRangeBar(range_rooms, fltr.rooms)
        setUpRangeBar(range_pictures, fltr.pictures)

        /*search_edit_type.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.type.value = p0.toString()
            }

        })*/
        search_overlay.setOnClickListener {}
        search_parent.setOnClickListener {
            finish()
        }
    }

    /** Sets up range bars initial values and listener */
    private fun setUpRangeBar(rangeBar: CrystalRangeSeekbar, ld: MutableLiveData<Pair<Int, Int>>){
        if(ld.value != null){
            rangeBar.setMinStartValue(ld.value?.first!!.toFloat())
            rangeBar.setMaxStartValue(ld.value?.second!!.toFloat())
        }
        rangeBar.setOnRangeSeekbarChangeListener(seekBarListenerWithLiveData(ld))
    }

    /** Removes this fragment */
    private fun finish(){
        //(context as MainActivity).mAdapter.filter(viewModel.filter)
        activity?.supportFragmentManager?.beginTransaction()?.remove(this)?.commit()
    }

    private fun seekBarListenerWithLiveData(ld: MutableLiveData<Pair<Int, Int>>?) : OnRangeSeekbarChangeListener {
        return object : OnRangeSeekbarChangeListener, SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun valueChanged(minValue: Number?, maxValue: Number?) {
                ld?.value = Pair(minValue!!.toInt(), maxValue!!.toInt())
            }

        }
    }

    private fun textWatcherListenerWithLiveData(ld: MutableLiveData<String>?) : TextWatcher {
        return object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}

            override fun onTextChanged(charSeq: CharSequence?, p1: Int, p2: Int, p3: Int) {
                ld?.value = charSeq.toString()
            }

        }
    }

}