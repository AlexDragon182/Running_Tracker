package com.example.runningtracker.database

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import androidx.room.TypeConverter
import java.io.ByteArrayOutputStream

// this file converter is to tell room how to save and get the bitmap
class Converter {

    @TypeConverter// this tells room this is a Type Converter Function
    fun toBitmap(bytes: ByteArray): Bitmap {//this takes the byte array and converted to a bit map
        return BitmapFactory.decodeByteArray(bytes, 0, bytes.size)// decode ByteArray means it will interpret those bytes as an array
    }

    @TypeConverter //the bit map will be saved as a byte array
    fun fromBitmap(bmp: Bitmap): ByteArray { // this takes a bit map converted to byte array and the byte array will be saved in room data base
        val outputStream = ByteArrayOutputStream()//OutputStream is need it to convert a bitmap to its bytes
        bmp.compress(Bitmap.CompressFormat.PNG, 100, outputStream)//call function bitmap.compress so it compress the bit map and pass it to output stream
        return outputStream.toByteArray()//return this output stream converted to byte array
    }
}