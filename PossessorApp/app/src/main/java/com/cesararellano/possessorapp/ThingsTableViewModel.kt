package com.cesararellano.possessorapp

import androidx.lifecycle.ViewModel
import java.util.*

// Con este ViewModel podemos generar y guardar nuestro listado de posesiones (cosas).
class ThingsTableViewModel: ViewModel() {
    // Nombres de las secciones.
    private val sections = arrayOf(
        "$0-$99",
        "$100-$199",
        "$200-$299",
        "$300-$399",
        "$400-$499",
        "$500-$599",
        "$600-$699",
        "$700-$799",
        "$800-$899",
        "$900-$999",
        "+$1000"
    )

    // Variable utilizada para ordenar las secciones.
    private val priceRanges = arrayListOf(0..99, 100..199, 200..299, 300..399, 400..499, 500..599, 600..699, 700..799, 800..899, 900..999, 1000..10000000)

    // ArrayList del inventario.
    val inventory = ArrayList<Sections>()

    init {
        for(section in sections) {
            inventory.add( Sections(section, arrayListOf()) )
        }
    }

    fun addNewThing(thing: Thing) {
        val position = getSectionPosition(thing.pesosValue)
        inventory[position].sectionList.add(thing)
    }

    fun orderSectionList(thing: Thing, prevSectionIndex: Int) {
        val newPosition = getSectionPosition(thing.pesosValue)
        if( newPosition != prevSectionIndex ) {
            val orderedSection = inventory[prevSectionIndex].sectionList.filter { it.pesosValue in priceRanges[prevSectionIndex] }
            inventory[prevSectionIndex].sectionList.clear() // Elimina todas las cosas de la sección previa.
            inventory[prevSectionIndex].sectionList.addAll(orderedSection) // Se añaden las cosas ordenadas en la sección previa.
            inventory[newPosition].sectionList.add(thing) // La cosa se añade a la nueva sección.
        }
    }

    // De acuerdo al precio de la cosa, se establece la posición de la lista de la sección a la que pertenece.
    fun getSectionPosition(price: Int): Int {
        val position = when(price){
            in 0..99 -> 0
            in 100..199 -> 1
            in 200..299 -> 2
            in 300..399 -> 3
            in 400..499 -> 4
            in 500..599 -> 5
            in 600..699 -> 6
            in 700..799 -> 7
            in 800..899 -> 8
            in 900..999 -> 9
            else -> 10
        }

        return position
    }

    // Obtiene el total de cosas que hay en el inventario.
    fun getTotalThings(): Int {
        var totalThings = 0

        for(sections in inventory){
            totalThings += sections.sectionList.size
        }

        return totalThings
    }

    // Obtiene la suma total de los precios de las cosas.
    fun getTotalPriceSum(): Int {
        var totalPrice = 0

        for(sections in inventory) {
            for(thing in sections.sectionList) {
                totalPrice += thing.pesosValue
            }
        }

        return totalPrice
    }

    // Obtiene la suma de precios de la sección que se indique.
    fun getThingPriceSumBySection(sectionList: ArrayList<Thing>): Int {
        var priceCounterBySection = 0

        for(thing in sectionList) {
            priceCounterBySection += thing.pesosValue
        }

        return priceCounterBySection
    }
}