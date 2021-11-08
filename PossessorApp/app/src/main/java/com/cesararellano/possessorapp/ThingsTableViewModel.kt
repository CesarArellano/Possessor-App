package com.cesararellano.possessorapp

import androidx.lifecycle.ViewModel
import java.util.*

// Con este ViewModel podemos generar y guardar nuestro listado de posesiones (cosas).
class ThingsTableViewModel: ViewModel() {

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

    private val priceRanges = arrayListOf(0..99, 100..199, 200..299, 300..399, 400..499, 500..599, 600..699, 700..799, 800..899, 900..999, 1000..10000000)

    val listOfSections = ArrayList<Sections>()

    init {
        for(section in sections) {
            listOfSections.add(Sections(section, arrayListOf()))
        }
    }

    fun getTotalThings(): Int {
        var totalThings = 0
        for(sections in listOfSections){
            totalThings += sections.list.size
        }
        return totalThings
    }

    fun getTotalPrices(): Int {
        var totalPrices = 0
        for(sections in listOfSections) {
            for(thing in sections.list) {
                totalPrices+= thing.pesosValue
            }
        }

        return totalPrices
    }

    fun addNewThing(thing: Thing) {
        val index = getIndexOfSection(thing.pesosValue)
        listOfSections[index].list.add(thing)
    }

    fun reorderArrays(thing: Thing, prevSectionIndex: Int) {
        val newIndex = getIndexOfSection(thing.pesosValue)
        if(newIndex != prevSectionIndex) {
            val filteredValues = listOfSections[prevSectionIndex].list.filter { it.pesosValue in priceRanges[prevSectionIndex] }
            listOfSections[prevSectionIndex].list.clear()
            listOfSections[prevSectionIndex].list.addAll(filteredValues)
            listOfSections[newIndex].list.add(thing)
        }
    }

    fun getIndexOfSection(price: Int): Int {
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
}