/*
 * Traincut allows you to log information about the trains you travel into.
 *
 * Copyright (C) 2022 Anael González Paz
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */

package es.terax.traincut

import android.content.ContentValues
import android.content.Context
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.database.sqlite.SQLiteOpenHelper

class SQLHelper(context: Context, factory: SQLiteDatabase.CursorFactory?) :
    SQLiteOpenHelper(context, DATABASE_NAME, factory, DATABASE_VERSION){
    override fun onCreate(dataBase: SQLiteDatabase) {
        dataBase.execSQL("CREATE TABLE $TABLE_NAME " +
                "($COLUMN_ID INTEGER PRIMARY KEY, $COLUMN_ORIGIN TEXT, " +
                "$COLUMN_DESTINATION TEXT, $COLUMN_DEPARTURE INT, " +
                "$COLUMN_ARRIVAL INT, $COLUMN_NAME TEXT, $COLUMN_TRAIN_NUMBER INTEGER, " +
                "$COLUMN_SEAT TEXT, $COLUMN_SEAT_CLASS TEXT, $COLUMN_CAR INTEGER, $COLUMN_COST DOUBLE, " +
                "$COLUMN_CURRENCY TEXT, $COLUMN_REAL_DEPART INT, $COLUMN_REAL_ARRIVAL INT, " +
                "$COLUMN_SERIES TEXT, $COLUMN_COMMENTS TEXT)"
        )
    }

    override fun onUpgrade(dataBase: SQLiteDatabase, oldVersion: Int, newVersion: Int) {
        // Implement version upgrade mechanism in case of version change.
        dataBase.execSQL("DROP TABLE IF EXISTS $TABLE_NAME")
        onCreate(dataBase)
    }

    // TODO: Change string for datetime.
    fun insertRow(origin: String, destination: String, departure: Long, arrival: Long,
    name: String, trainNumber: Int, seat: String, seatClass: String, car: Int, cost: Double,
    currency: String, realDeparture: Long, realArrival: Long, series: String, comments: String) {
        val values = ContentValues()
        values.put(COLUMN_ORIGIN, origin)
        values.put(COLUMN_DESTINATION, destination)
        values.put(COLUMN_DEPARTURE, departure)
        values.put(COLUMN_ARRIVAL, arrival)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_TRAIN_NUMBER, trainNumber)
        values.put(COLUMN_SEAT, seat)
        values.put(COLUMN_SEAT_CLASS, seatClass)
        values.put(COLUMN_CAR, car)
        values.put(COLUMN_COST, cost)
        values.put(COLUMN_CURRENCY, currency)
        values.put(COLUMN_REAL_DEPART, realDeparture)
        values.put(COLUMN_REAL_ARRIVAL, realArrival)
        values.put(COLUMN_SERIES, series)
        values.put(COLUMN_COMMENTS, comments)

        val dataBase = this.writableDatabase
        dataBase.insert(TABLE_NAME, null, values)
        dataBase.close()
    }

    // TODO: Change string for datetime.
    fun updateRow(rowId: String, origin: String, destination: String, departure: Long,
                  arrival: Long, name: String, trainNumber: Int, seat: String, seatClass: String,
                  car: Int, cost: Double, currency: String, realDeparture: Long,
                  realArrival: Long, series: String, comments: String) {
        val values = ContentValues()
        values.put(COLUMN_ORIGIN, origin)
        values.put(COLUMN_DESTINATION, destination)
        values.put(COLUMN_DEPARTURE, departure)
        values.put(COLUMN_ARRIVAL, arrival)
        values.put(COLUMN_NAME, name)
        values.put(COLUMN_TRAIN_NUMBER, trainNumber)
        values.put(COLUMN_SEAT, seat)
        values.put(COLUMN_SEAT_CLASS, seatClass)
        values.put(COLUMN_CAR, car)
        values.put(COLUMN_COST, cost)
        values.put(COLUMN_CURRENCY, currency)
        values.put(COLUMN_REAL_DEPART, realDeparture)
        values.put(COLUMN_REAL_ARRIVAL, realArrival)
        values.put(COLUMN_SERIES, series)
        values.put(COLUMN_COMMENTS, comments)

        val dataBase = this.writableDatabase
        dataBase.update(TABLE_NAME, values, "$COLUMN_ID = ?", arrayOf(rowId))
        dataBase.close()
    }

    fun deleteRow(rowId: String) {
        val dataBase = this.writableDatabase
        dataBase.delete(TABLE_NAME, "$COLUMN_ID = ?", arrayOf(rowId))
        dataBase.close()
    }

    fun getAllRow(): Cursor? {
        val dataBase = this.readableDatabase
        return dataBase.rawQuery("SELECT * FROM $TABLE_NAME", null)
    }

    companion object {
        const val DATABASE_VERSION = 1
        const val DATABASE_NAME = "TraincutLog.db"
        const val TABLE_NAME = "log"
        const val COLUMN_ID = "id"
        const val COLUMN_ORIGIN = "origin"
        const val COLUMN_DESTINATION = "destination"
        const val COLUMN_DEPARTURE = "departure"
        const val COLUMN_ARRIVAL = "arrival"
        const val COLUMN_NAME = "name"
        const val COLUMN_TRAIN_NUMBER = "train_number"
        const val COLUMN_SEAT = "seat"
        const val COLUMN_SEAT_CLASS = "seat_class"
        const val COLUMN_CAR = "car"
        const val COLUMN_COST = "cost"
        const val COLUMN_CURRENCY = "currency"
        const val COLUMN_REAL_DEPART = "real_depart"
        const val COLUMN_REAL_ARRIVAL = "real_arrival"
        const val COLUMN_SERIES = "series"
        const val COLUMN_COMMENTS = "comments"
    }
}
