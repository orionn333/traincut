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

package es.terax.traincut.ui.view

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import es.terax.traincut.EntryModel
import es.terax.traincut.R
import es.terax.traincut.SQLHelper
import es.terax.traincut.databinding.FragmentViewBinding
import java.time.LocalDateTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class ViewFragment: Fragment() {
    private var _binding: FragmentViewBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val args: ViewFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentViewBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val topAppBar = binding.topAppBar
        val databaseHandler = SQLHelper(requireContext(), null)

        val inputOrigin = binding.displayOrigin
        val inputDestination = binding.displayDestination
        val inputDeparture = binding.displayDeparture
        val inputArrival = binding.displayArrival

        val entryData = getEntryData(args.positionId, databaseHandler)
        inputOrigin.text = entryData.entryOrigin
        inputDestination.text = entryData.entryDestination
        // We convert the date into a human readable format.
        val departDateTime = LocalDateTime.ofEpochSecond(entryData.entryDeparture,0,
            ZoneOffset.UTC)
        val arrivalDateTime = LocalDateTime.ofEpochSecond(entryData.entryArrival, 0,
            ZoneOffset.UTC)
        val format = DateTimeFormatter.ofLocalizedDateTime(
            FormatStyle.MEDIUM,
            FormatStyle.SHORT)
        val departFormatted: String = departDateTime.format(format)
        val arrivalFormatted: String = arrivalDateTime.format(format)
        inputDeparture.text = departFormatted
        inputArrival.text = arrivalFormatted
        topAppBar.setNavigationOnClickListener {
            root.findNavController().navigateUp()
        }
        val actionEntry =
            ViewFragmentDirections.actionViewFragmentToEntryFragment(true, args.entryId, args.positionId)
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId)
            {
                R.id.actionEdit -> {
                    root.findNavController().navigate(actionEntry)
                    true
                }
                R.id.actionDelete -> {
                    MaterialAlertDialogBuilder(requireContext())
                        .setTitle(resources.getString(R.string.view_delete_dialog))
                        .setMessage(resources.getString(R.string.view_delete_dialog_desc))
                        .setIcon(R.drawable.ic_baseline_delete_fg_24)
                        .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                            // Abort the entry deletion.
                        }
                        .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                            // Proceed with the entry deletion.
                            databaseHandler.deleteRow(args.entryId.toString())
                            findNavController().navigateUp()
                        }
                        .show()
                    true
                }
                else -> false
            }
        }
        return root
    }

    private fun getEntryData(entryId: Int, databaseHandler: SQLHelper): EntryModel {
        val cursor = databaseHandler.getAllRow()!!
        return if (cursor.moveToPosition(entryId)) {
            val entryModel = EntryModel()
            entryModel.entryId = cursor.getInt(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_ID))
            entryModel.entryOrigin =
                cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_ORIGIN))
            entryModel.entryDestination =
                cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_DESTINATION))
            entryModel.entryDeparture =
                cursor.getLong(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_DEPARTURE))
            entryModel.entryArrival =
                cursor.getLong(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_ARRIVAL))
            entryModel
        } else {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.error_entry_load))
                .setMessage(resources.getString(R.string.error_entry_row_desc))
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    // Do nothing.
                    // TODO: Provide a solution.
                }
                .show()
            findNavController().navigateUp()
            EntryModel()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
