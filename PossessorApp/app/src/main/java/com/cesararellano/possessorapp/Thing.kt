package com.cesararellano.possessorapp

import android.os.Parcel
import android.os.Parcelable
import java.util.*

class Thing(): Parcelable {
    var thingName: String = ""
    var pesosValue: Int = 0
    var serialNumber: String = UUID.randomUUID().toString().substring(0,6)
    var creationDate: Date = Date()

    // Deserealización
    constructor(parcel: Parcel) : this() {
        thingName = parcel.readString().toString()
        pesosValue = parcel.readInt()
        serialNumber = parcel.readString().toString()
        creationDate = parcel.readSerializable() as Date
    }

    // Serializar
    override fun describeContents(): Int {
        return 0
    }

    override fun writeToParcel(dest: Parcel, flags: Int) {
        dest.writeString(thingName)
        dest.writeInt(pesosValue)
        dest.writeString(serialNumber)
        dest.writeSerializable(creationDate)
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