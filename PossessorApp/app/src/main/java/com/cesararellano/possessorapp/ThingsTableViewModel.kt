package com.cesararellano.possessorapp

import androidx.lifecycle.ViewModel
import java.util.*

// Con este ViewModel podemos generar y guardar nuestro listado de posesiones (cosas).
class ThingsTableViewModel: ViewModel() {
    // Atributos del ViewModel
    val inventory = ArrayList<Thing>()
    private val names = arrayOf("Teléfono", "Pan", "Playera")
    private val adjectives = arrayOf("Gris", "Suave", "Cómoda")

    // Generamos aleatoriamente las cosas a presentar en el RecyclerView.
    init {
        for( i in 0 until 100) {
            val thing = Thing()
            val randomName = names.random()
            val randomAdjective = adjectives.random()
            val randomPrice = Random().nextInt(1001)
            thing.thingName = "$randomName - $randomAdjective"
            thing.pesosValue = randomPrice
            inventory += thing
        }
    }
}