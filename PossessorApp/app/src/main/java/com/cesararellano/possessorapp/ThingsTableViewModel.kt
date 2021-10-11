package com.cesararellano.possessorapp

import androidx.lifecycle.ViewModel
import java.util.*

class ThingsTableViewModel: ViewModel() {
    val inventory = mutableListOf<Thing>()
    private val names = arrayOf("Teléfono", "Pan", "Playera")
    private val adjectives = arrayOf("Gris", "Suave", "Cómoda")

    init {
        for( i in 0 until 100) {
            val thing = Thing()
            val randomName = names.random()
            val randomAdjective = adjectives.random()
            val randomPrice = Random().nextInt(100)
            thing.thingName = "$randomName - $randomAdjective"
            thing.pesosValue = randomPrice
            inventory += thing
        }
    }
}