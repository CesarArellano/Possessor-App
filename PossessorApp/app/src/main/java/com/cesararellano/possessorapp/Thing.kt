package com.cesararellano.possessorapp

import android.os.Parcel
import android.os.Parcelable
import java.text.SimpleDateFormat
import java.util.*

class Thing(): Parcelable {
    var thingName: String = ""
    var pesosValue: Int = 0
    var serialNumber: String = UUID.randomUUID().toString().substring(0,6)
    var creationDate: String = SimpleDateFormat( "dd-MM-yyyy", Locale.getDefault() ).format( Date() )

    // Deserealización
    constructor(parcel: Parcel) : this() {
        thingName = parcel.readString().toString()
        pesosValue = parcel.readInt()
        serialNumber = parcel.readString().toString()
        creationDate = parcel.readString().toString()
    }

    // Serializar
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(thingName)
        dest.writeInt(pesosValue)
        dest.writeString(serialNumber)
        dest.writeString(creationDate)
    }

    companion object CREATOR : Parcelable.Creator<Thing> {
        override fun createFromParcel(source: Parcel): Thing {
            return Thing(source)
        }

        override fun newArray(size: Int): Array<Thing?> {
            return arrayOfNulls(size)
        }
    }
}