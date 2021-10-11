package com.cesararellano.possessorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity(), ThingsTableFragment.ThingTableInterface {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if( currentFragment == null ) {
            // val fragment = ThingFragment()
            val fragment = ThingsTableFragment()
            supportFragmentManager.beginTransaction()
                .add( R.id.fragment_container, fragment )
                .commit()
        }
    }

    override fun onSelectedThing(thing: Thing) {
        Toast.makeText(this, "${ thing.thingName } fue seleccionada", Toast.LENGTH_SHORT).show()
        // val fragment = ThingFragment()
        val fragment = ThingFragment.newInstance(thing)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }
}