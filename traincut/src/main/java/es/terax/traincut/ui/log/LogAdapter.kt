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

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.navigation.NavController
import androidx.recyclerview.widget.RecyclerView
import es.terax.traincut.EntryModel
import es.terax.traincut.R
import es.terax.traincut.databinding.ListCardBinding
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle
import kotlin.collections.ArrayList

class LogAdapter(private val dataList: ArrayList<EntryModel>,
                 private val context: Context,
                 private val navController: NavController) :
    RecyclerView.Adapter<LogAdapter.ViewHolder>() {

    inner class ViewHolder(view: View) : RecyclerView.ViewHolder(view){
        val viewBinding = ListCardBinding.bind(view)

        fun setListener(dataListed: EntryModel){
            val actionView =
                LogFragmentDirections.actionNavigationLogToViewFragment(dataListed.entryId,
                    dataListed.entryPosition)
            viewBinding.materialCardView.setOnClickListener{
                navController.navigate(actionView)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val view = LayoutInflater.from(context).inflate(R.layout.list_card, parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        val entryModel: EntryModel = dataList[position]

        holder.viewBinding.textOrigin.text = entryModel.entryOrigin
        holder.viewBinding.textDestination.text = entryModel.entryDestination
        // We convert the date into a human readable format.
        val departDateTime = LocalDateTime.ofEpochSecond(entryModel.entryDeparture,0,
            ZoneOffset.UTC)
        val arrivalDateTime = LocalDateTime.ofEpochSecond(entryModel.entryArrival, 0,
            ZoneOffset.UTC)
        val format = DateTimeFormatter.ofLocalizedDate(FormatStyle.FULL)
        val formatted: String = departDateTime.format(format)
        holder.viewBinding.textDate.text = formatted
        val timeFormat = DateTimeFormatter.ofLocalizedTime(FormatStyle.SHORT)
        val departureTime: String = departDateTime.format(timeFormat)
        val arrivalTime: String = arrivalDateTime.format(timeFormat)
        holder.viewBinding.originTime.text = departureTime
        holder.viewBinding.destinationTime.text = arrivalTime
        holder.setListener(entryModel)
    }

    override fun getItemCount(): Int {
        return dataList.size
    }
}
