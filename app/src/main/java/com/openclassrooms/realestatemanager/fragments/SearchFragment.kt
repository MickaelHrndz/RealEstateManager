package com.openclassrooms.realestatemanager.fragments

import android.arch.lifecycle.ViewModelProviders
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
import com.openclassrooms.realestatemanager.BR
import com.openclassrooms.realestatemanager.FiltersViewModel
import com.openclassrooms.realestatemanager.R
import kotlinx.android.synthetic.main.fragment_search.*

/**
 * Created by Mickael Hernandez on 03/07/2018.
 */
class SearchFragment : Fragment() {
    private lateinit var viewModel : FiltersViewModel
    private lateinit var binding: ViewDataBinding

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View {
        viewModel = ViewModelProviders.of(this).get(FiltersViewModel::class.java)
        binding = DataBindingUtil.inflate<ViewDataBinding>(inflater, R.layout.fragment_search, container, false)
        binding.setVariable(BR.filters, viewModel)
        binding.setLifecycleOwner(this)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        range_price.setOnRangeSeekbarChangeListener(object : OnRangeSeekbarChangeListener, SeekBar.OnSeekBarChangeListener {
            override fun onProgressChanged(p0: SeekBar?, p1: Int, p2: Boolean) {}
            override fun onStartTrackingTouch(p0: SeekBar?) {}
            override fun onStopTrackingTouch(p0: SeekBar?) {}
            override fun valueChanged(minValue: Number?, maxValue: Number?) {
                viewModel.setPriceBounds(minValue!!.toInt(), maxValue!!.toInt())
            }

        })
        search_edit_type.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(p0: Editable?) {}
            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {
                viewModel.type.value = p0.toString()
            }

        })
        /*.setOnClickListener {
            finish()
        }*/
        search_parent.setOnClickListener {  }
    }

}