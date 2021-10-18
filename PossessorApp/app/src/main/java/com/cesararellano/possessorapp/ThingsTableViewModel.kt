package com.cesararellano.possessorapp

import androidx.lifecycle.ViewModel
import java.util.*

// Con este ViewModel podemos generar y guardar nuestro listado de posesiones (cosas).
class ThingsTableViewModel: ViewModel() {
    // Atributos del ViewModel
    val inventory = ArrayList<Thing>()

    fun addNewThing(newThing: Thing) {
        inventory.add(newThing)
    }
}