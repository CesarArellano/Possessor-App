package com.cesararellano.possessorapp

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

// Esta clase describe principalmente los atributos que puede tener una cosa, sin embargo también esta implementa la interfaz Parcelable
// esta interfaz nos ayudará a poder serializar y deserializar la info entre fragments.
class Thing(): Parcelable {
    var thingName: String = "Nueva Cosa"
    var pesosValue: Int = 0
    var serialNumber: String = UUID.randomUUID().toString().substring(0, 8) // Se limita a 8 dígitos el UUID random
    var creationDate: String = SimpleDateFormat( "dd-MM-yyyy", Locale.getDefault() ).format( Date() ) // Con esta instrucción formateamos la fecha de la siguiente forma "día-mes-año".
    var originalCreationDate: Date = Date() // El tener este parámetro de tipo Date, nos ayudará a poder ordenarlos de manera ascendente o descendente los elementos.
    var thingId = UUID.randomUUID().toString().substring(0,6)

    // Deserializar data
    constructor(parcel: Parcel) : this() {
        thingName = parcel.readString().toString()
        pesosValue = parcel.readInt()
        serialNumber = parcel.readString().toString()
        creationDate = parcel.readString().toString()
        thingId = parcel.readString().toString()
    }

    override fun describeContents(): Int {
        return 0
    }

    // Serializar data
    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(thingName)
        dest.writeInt(pesosValue)
        dest.writeString(serialNumber)
        dest.writeString(creationDate)
        dest.writeString(thingId)
    }

    // Este companion object creará un objeto Thing a partir de un Parcel.
    companion object CREATOR : Parcelable.Creator<Thing> {
        override fun createFromParcel(source: Parcel): Thing {
            return Thing(source)
        }

        override fun newArray(size: Int): Array<Thing?> {
            return arrayOfNulls(size)
        }
    }
}