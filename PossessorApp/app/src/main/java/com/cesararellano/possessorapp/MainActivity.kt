package com.cesararellano.possessorapp

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.widget.Toast

class MainActivity : AppCompatActivity(), ThingsTableFragment.ThingTableInterface {
    private var thingSelected = Thing()
    private var thingsTableViewModel: ThingsTableViewModel? = null
    private var currentThingPosition: Int = 0

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        // supportFragmentManager nos ayudará a pintar el Fragment de tabla de cosas
        val currentFragment = supportFragmentManager.findFragmentById(R.id.fragment_container)
        if( currentFragment == null ) {
            val fragment = ThingsTableFragment()
            supportFragmentManager.beginTransaction()
                .add( R.id.fragment_container, fragment )
                .commit()
        }
    }

    // Este método es implementado por la interfaz ThingTableInterface, esta recibe la posesión (cosa) seleccionada por el usuario
    // este muestra un Toast del nombre de la cosa seleccionada y con este objeto despliega el nuevo fragment con más detalles.
    override fun onSelectedThing(thing: Thing, thingsTable: ThingsTableViewModel) {
        Toast.makeText(this, "${ thing.thingName } fue seleccionada", Toast.LENGTH_SHORT).show()
        thingSelected = thing
        thingsTableViewModel = thingsTable
        currentThingPosition = thingsTable.getIndexOfSection( thing.pesosValue )
        val fragment = ThingFragment.newInstance(thing)
        supportFragmentManager
            .beginTransaction()
            .replace(R.id.fragment_container, fragment)
            .addToBackStack(null)
            .commit()
    }

    override fun onBackPressed() {
        thingsTableViewModel?.reorderArrays(thingSelected, currentThingPosition)
        super.onBackPressed()
    }
}