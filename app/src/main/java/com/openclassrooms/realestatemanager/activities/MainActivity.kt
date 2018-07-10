package com.openclassrooms.realestatemanager.activities

import android.arch.lifecycle.ViewModelProviders
import android.content.Intent
import android.content.res.Configuration
import android.os.Bundle
import android.support.v4.app.Fragment
import android.support.v7.app.ActionBarDrawerToggle
import android.support.v7.app.AppCompatActivity
import android.support.v7.widget.DividerItemDecoration
import android.support.v7.widget.LinearLayoutManager
import android.support.v7.widget.RecyclerView
import android.support.v7.widget.Toolbar
import android.view.Menu
import android.view.MenuItem
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FirebaseFirestoreException
import com.google.firebase.firestore.QuerySnapshot
import com.openclassrooms.realestatemanager.FiltersViewModel
import com.openclassrooms.realestatemanager.R
import com.openclassrooms.realestatemanager.adapters.PropertiesListAdapter
import com.openclassrooms.realestatemanager.fragments.EditPropertyFragment
import com.openclassrooms.realestatemanager.fragments.PropertyFragment
import com.openclassrooms.realestatemanager.fragments.SearchFragment
import com.openclassrooms.realestatemanager.models.Property
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*


open class MainActivity : AppCompatActivity() {

    companion object {
        //lateinit var instance: AppDatabase
        var colRef = FirebaseFirestore.getInstance().collection("properties")
        const val SEARCH_CODE = 123
    }

    /** List of workmates */
    private var propertiesList = ArrayList<Property>()

    /** Adapter between workmates list and ListView */
    lateinit var mAdapter: PropertiesListAdapter

    /** RecyclerView */
    private lateinit var mRecyclerView: RecyclerView

    private lateinit var viewModel : FiltersViewModel

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        setSupportActionBar(findViewById(R.id.toolbar))
        title = ""

        viewModel = ViewModelProviders.of(this).get(FiltersViewModel::class.java)

        // Sign in Firebase Auth anonymously (useful for Storage upload)
        if(FirebaseAuth.getInstance().currentUser == null){
            FirebaseAuth.getInstance().signInAnonymously()
        }

        mAdapter = PropertiesListAdapter(this, R.layout.row_property, propertiesList)
        val llm = LinearLayoutManager(this)
        llm.orientation = LinearLayoutManager.VERTICAL

        mRecyclerView = findViewById(R.id.recyclerView)
        mRecyclerView.layoutManager = llm
        mRecyclerView.adapter = mAdapter


        // Row separator
        mRecyclerView.addItemDecoration(DividerItemDecoration(mRecyclerView.context, llm.orientation))

        /*colRef.get().addOnCompleteListener {
            if(it.isSuccessful) {
                val res = it.result.documents
                for(doc in res){
                    val prop = doc.toObject(Property::class.java)
                    if(prop != null){
                        propertiesList.add(prop)
                        mAdapter.notifyDataSetChanged()
                    }
                }
            }
        }*/

        colRef.addSnapshotListener(object: EventListener, com.google.firebase.firestore.EventListener<QuerySnapshot> {
            override fun onEvent(p0: QuerySnapshot?, p1: FirebaseFirestoreException?) {
                if(p0 != null){
                    propertiesList.clear()
                    val res = p0.documents
                    for(doc in res){
                        val prop = doc.toObject(Property::class.java)
                        if(prop != null){
                            // Add properties to the list
                            propertiesList.add(prop)
                            mAdapter.notifyDataSetChanged()
                        }
                    }
                }
            }

        })

        // Drawer configuration
        val toggle = ActionBarDrawerToggle(
                this, drawer_layout, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close)
        drawer_layout.addDrawerListener(toggle)
        toggle.syncState()

        fab.setOnClickListener {
            displayFragment(EditPropertyFragment.newInstance(Property()))
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)

    }

    override fun onOptionsItemSelected(item: MenuItem?): Boolean {
        when(item?.itemId){
            R.id.action_search -> {
                displayFragment(SearchFragment())
            }
        }
        return super.onOptionsItemSelected(item)
    }

    // Creates the toolbar menu
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)
        return true
    }

    fun displayFragment(frag: Fragment){
        if((resources.configuration.screenLayout
                        .and(Configuration.SCREENLAYOUT_SIZE_MASK)) ==
                Configuration.SCREENLAYOUT_SIZE_LARGE) {

            // Add the fragment to the 'fragment_container' FrameLayout
            supportFragmentManager.beginTransaction()
                    .add(R.id.fragment_container, frag).commit()
        } else {
            val transaction = supportFragmentManager.beginTransaction()
            // Replace whatever is in the fragment_container view with this fragment,
            // and add the transaction to the back stack so the user can navigate back
            transaction.replace(R.id.fragment_container, frag)
            transaction.addToBackStack(null)
            // Commit the transaction
            transaction.commit()

            /*if(mRecyclerView.visibility == View.VISIBLE){
                mRecyclerView.visibility = View.GONE
            }*/
        }
    }
}
