package com.example.fitnessapp.Model

import android.os.Parcel
import android.os.Parcelable

data class Exercise (
    var name: String = "",
    var createdBy: String = "",
    val duration: String = "",
    var id: Long = 0,
    val image: String = "",
    var isCompleted: Boolean = false,
    var isSelected: Boolean = false

): Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong()!!,
        parcel.readString()!!,
        parcel.readByte() != 0.toByte(),
        parcel.readByte() != 0.toByte())

    override fun writeToParcel(parcel: Parcel, flags: Int) {
        parcel.writeString(name)
        parcel.writeString(createdBy)
        parcel.writeString(duration)
        parcel.writeLong(id)
        parcel.writeString(image)
        parcel.writeByte(if (isCompleted) 1 else 0)
        parcel.writeByte(if (isSelected) 1 else 0)
    }

    fun setIsCompleted(isCompleted: Boolean) {
        this.isCompleted = isCompleted
    }

    fun getIsCompleted() : Boolean {
        return isCompleted
    }

    fun getIsSelected() : Boolean {
        return isSelected
    }

    fun setIsSelected(isSelected: Boolean) {
        this.isSelected = isSelected
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<Exercise> {
        override fun createFromParcel(parcel: Parcel): Exercise {
            return Exercise(parcel)
        }

        override fun newArray(size: Int): Array<Exercise?> {
            return arrayOfNulls(size)
        }
    }
}