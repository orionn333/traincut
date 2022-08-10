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

class EntryModel {
    var entryId: Int = 0
    var entryOrigin: String = ""
    var entryDestination: String = ""
    var entryDeparture: String = ""
    var entryArrival: String = ""
    var entryName: String = ""
    var entryTrainNumber: Int = 0
    var entrySeat: String = ""
    var entrySeatClass: String = ""
    var entryCar: Int = 0
    var entryCost: Double = 0.00
    var entryCurrency: String = ""
    var entryRealDepart: String = ""
    var entryRealArrival: String = ""
    var entrySeries: String = ""
    var entryComments: String = ""
}
