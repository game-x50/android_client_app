package com.ruslan.hlushan.android.extensions

import android.os.Parcel
import android.os.Parcelable

inline fun <reified T> parcelableCreator(
        crossinline create: (Parcel) -> T
): Parcelable.Creator<T> = object : Parcelable.Creator<T> {
    override fun createFromParcel(source: Parcel): T = create(source)
    override fun newArray(size: Int): Array<T?> = arrayOfNulls(size)
}

inline fun <T> Parcel.writeNullable(value: T?, writer: Parcel.(T) -> Unit) {
    if (value != null) {
        writeByte(1.toByte())
        writer(value)
    } else {
        writeByte(0.toByte())
    }
}

inline fun <T> Parcel.readNullable(reader: Parcel.() -> T): T? =
        if (readByte() == 1.toByte()) {
            reader(this)
        } else {
            null
        }

inline fun <reified T : Parcelable> Parcel.writeParcelableCollection(collection: Collection<T>, flags: Int) {
    writeTypedArray(collection.toTypedArray(), flags)
}

inline fun <reified T : Parcelable> Parcel.readParcelableCollection(creator: Parcelable.Creator<T>): Array<out T>? =
        this.createTypedArray(creator)

fun Parcel.writeBooleanSafety(value: Boolean) = writeByte(if (value) 1.toByte() else 0.toByte())
fun Parcel.readBooleanSafety(): Boolean = readByte() == 1.toByte()

inline fun <reified T : Parcelable> Parcel.readParcelable(): T? = readParcelable(T::class.java.classLoader)