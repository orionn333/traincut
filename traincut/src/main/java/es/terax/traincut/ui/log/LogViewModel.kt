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

package es.terax.traincut.ui.log

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel

class LogViewModel : ViewModel() {

    private val _destination = MutableLiveData<String>().apply {
        value = "Destination"
    }
    val destination: LiveData<String> = _destination

    private val _originTime = MutableLiveData<String>().apply {
        value = "09:30"
    }
    val originTime: LiveData<String> = _originTime

    private val _destinationTime = MutableLiveData<String>().apply {
        value = "09:55"
    }
    val destinationTime: LiveData<String> = _destinationTime
}
