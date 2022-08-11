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

package es.terax.traincut.ui.entry

import android.os.Bundle
import android.text.format.DateFormat.is24HourFormat
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.core.widget.doOnTextChanged
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.navigation.fragment.navArgs
import com.google.android.material.datepicker.MaterialDatePicker
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.textfield.TextInputEditText
import com.google.android.material.textfield.TextInputLayout
import com.google.android.material.timepicker.MaterialTimePicker
import com.google.android.material.timepicker.TimeFormat
import es.terax.traincut.EntryModel
import es.terax.traincut.R
import es.terax.traincut.SQLHelper
import es.terax.traincut.databinding.FragmentEntryBinding
import java.time.LocalDate
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import java.time.format.DateTimeFormatter
import java.time.format.FormatStyle

class EntryFragment : Fragment() {
    private var _binding: FragmentEntryBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!
    private val args: EntryFragmentArgs by navArgs()

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentEntryBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val topAppBar = binding.topAppBar
        val databaseHandler = SQLHelper(requireContext(), null)
        var emptyFields : Boolean

        val inputOrigin = binding.inputOrigin
        val inputDestination = binding.inputDestination
        val inputDeparture = binding.inputDeparture
        val inputArrival = binding.inputArrival
        topAppBar.setNavigationOnClickListener {
            root.findNavController().navigateUp()
        }
        if (args.isEdit) {
            topAppBar.title = getString(R.string.edit_entry)
            // Now, we load the entry data from the ID provided by the action argument.
            val entryData = getEntryData(args.positionId)
            inputOrigin.setText(entryData.entryOrigin)
            inputDestination.setText(entryData.entryDestination)
            // We convert the date into a human readable format.
            val departDateTime = LocalDateTime.ofEpochSecond(entryData.entryDeparture,0,
                ZoneOffset.UTC)
            val arrivalDateTime = LocalDateTime.ofEpochSecond(entryData.entryArrival, 0,
                ZoneOffset.UTC)
            val format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
                FormatStyle.SHORT)
            val departFormatted: String = departDateTime.format(format)
            val arrivalFormatted: String = arrivalDateTime.format(format)
            inputDeparture.setText(departFormatted)
            inputArrival.setText(arrivalFormatted)
        }
        topAppBar.setOnMenuItemClickListener { menuItem ->
            when (menuItem.itemId)
            {
                R.id.actionSave -> {
                    // First of all, we need to check if the entered data is correct,
                    // and if all mandatory fields have been filled.
                    val errorString = getString(R.string.error_mandatory_field)
                    emptyFields = false
                    if (inputOrigin.text.toString().isEmpty()) {
                        emptyFields = true
                        binding.fieldOrigin.error = errorString
                    }
                    if (inputDestination.text.toString().isEmpty()) {
                        emptyFields = true
                        binding.fieldDestination.error = errorString
                    }
                    if (inputDeparture.text.toString().isEmpty()) {
                        emptyFields = true
                        binding.fieldDeparture.error = errorString
                    }
                    if (inputArrival.text.toString().isEmpty()) {
                        emptyFields = true
                        binding.fieldArrival.error = errorString
                    }

                    if (args.isEdit && !emptyFields) {
                        // We convert the date into seconds since epoch.
                        val format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
                            FormatStyle.SHORT)
                        val departDate = LocalDateTime.parse(inputDeparture.text.toString(), format)
                        val arrivalDate = LocalDateTime.parse(inputArrival.text.toString(), format)
                        databaseHandler.updateRow(args.editId.toString(),
                            inputOrigin.text.toString(),
                            inputDestination.text.toString(),
                            departDate.toEpochSecond(ZoneOffset.UTC),
                            arrivalDate.toEpochSecond(ZoneOffset.UTC),
                            "",
                            0,
                            "",
                            "",
                            0,
                            0.00,
                            "",
                            "",
                            "",
                            "",
                            "")
                        root.findNavController().navigateUp()
                    } else if (!emptyFields) {
                        // We convert the date into seconds since epoch.
                        val format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
                            FormatStyle.SHORT)
                        val departDate = LocalDateTime.parse(inputDeparture.text.toString(), format)
                        val arrivalDate = LocalDateTime.parse(inputArrival.text.toString(), format)
                        databaseHandler.insertRow(inputOrigin.text.toString(),
                            inputDestination.text.toString(),
                            departDate.toEpochSecond(ZoneOffset.UTC),
                            arrivalDate.toEpochSecond(ZoneOffset.UTC),
                            "",
                            0,
                            "",
                            "",
                            0,
                            0.0,
                            "",
                            "",
                            "",
                            "",
                            "")
                        root.findNavController().navigateUp()
                    }
                    true
                }
                else -> false
            }
        }

        // We listen for text input in mandatory fields in order to remove any possible
        // empty field error just at the moment the user enters some text.
        inputOrigin.doOnTextChanged { text, _, _, _ ->
            validateInput(binding.fieldOrigin, text)}
        inputDestination.doOnTextChanged { text, _, _, _ ->
            validateInput(binding.fieldDestination, text)}
        inputDeparture.doOnTextChanged { text, _, _, _ ->
            validateInput(binding.fieldDeparture, text)
        }
        inputDeparture.setOnClickListener{
            showDateTimePickerDialog(binding.fieldDeparture, inputDeparture)
        }
        inputArrival.doOnTextChanged { text, _, _, _ ->
            validateInput(binding.fieldArrival, text)
        }
        inputArrival.setOnClickListener {
            showDateTimePickerDialog(binding.fieldArrival, inputArrival)
        }

        return root
    }

    private fun getEntryData(entryId: Int): EntryModel {
        val databaseHandler = SQLHelper(requireContext(), null)
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

    private fun validateInput(textInputLayout: TextInputLayout, text: CharSequence?)
    {
        if (text.toString().isNotEmpty())
        {
            textInputLayout.error = null
        }
    }

    private fun showDateTimePickerDialog(textInputLayout: TextInputLayout,
                                     textInputEditText: TextInputEditText) {
        textInputLayout.isEnabled = false
        val datePicker = MaterialDatePicker.Builder.datePicker()
            .setSelection(if (textInputEditText.text.toString().isEmpty())
            {
                MaterialDatePicker.todayInUtcMilliseconds()
            }
            else
            {
                val format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
                    FormatStyle.SHORT)
                LocalDateTime.parse(textInputEditText.text.toString(), format).toEpochSecond(
                    ZoneOffset.UTC)*1000
            })
            .build()
        val fragmentManager = (requireContext() as FragmentActivity).supportFragmentManager
        datePicker.addOnPositiveButtonClickListener {
            val localDate = LocalDateTime.ofEpochSecond(it/1000,0,
                ZoneOffset.UTC)
                .toLocalDate()
            // Now, we call the time picker dialog.
            showTimePickerDialog(localDate, textInputLayout, textInputEditText)
        }
        datePicker.addOnNegativeButtonClickListener {
            textInputLayout.isEnabled = true
            //Log.d("DatePickerDialog", "Date selection dialog was cancelled.")
        }
        datePicker.addOnCancelListener {
            textInputLayout.isEnabled = true
            //Log.d("DatePickerDialog", "Date selection dialog was cancelled.")
        }
        datePicker.addOnDismissListener {
            textInputLayout.isEnabled = true
            //Log.d("DatePickerDialog", "Date selection dialog was dismissed.")
        }
        datePicker.show(fragmentManager, "DatePickerDialog")
    }

    private fun showTimePickerDialog(localDate: LocalDate, textInputLayout: TextInputLayout,
                                     textInputEditText: TextInputEditText) {
        val isSystem24Hour = is24HourFormat(requireContext())
        val clockFormat = if (isSystem24Hour) TimeFormat.CLOCK_24H else TimeFormat.CLOCK_12H
        val fragmentManager = (requireContext() as FragmentActivity).supportFragmentManager
        val format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
            FormatStyle.SHORT)
        val timePicker = MaterialTimePicker.Builder()
            .setTimeFormat(clockFormat)
            .setHour(if (textInputEditText.text.toString().isEmpty()){
                LocalTime.now().hour
            }else{
                val textValue = LocalDateTime.parse(textInputEditText.text.toString(), format)
                textValue.hour
            })
            .setMinute(if (textInputEditText.text.toString().isEmpty()) {
                LocalTime.now().minute
            } else {
                val textValue = LocalDateTime.parse(textInputEditText.text.toString(), format)
                textValue.minute
            })
            .build()
        timePicker.addOnPositiveButtonClickListener {
            val localDateTime = LocalDateTime.of(localDate,
                LocalTime.of(timePicker.hour,timePicker.minute))
            val formatted: String = localDateTime.format(format)
            textInputEditText.setText(formatted)
            textInputLayout.isEnabled = true
        }
        timePicker.addOnNegativeButtonClickListener {
            textInputLayout.isEnabled = true
            //Log.d("TimePickerDialog", "Time selection dialog was cancelled.")
        }
        timePicker.addOnCancelListener {
            textInputLayout.isEnabled = true
            //Log.d("TimePickerDialog", "Time selection dialog was cancelled.")
        }
        timePicker.addOnDismissListener {
            textInputLayout.isEnabled = true
            //Log.d("TimePickerDialog", "Time selection dialog was dismissed.")
        }
        timePicker.show(fragmentManager, "TimePickerDialog")
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
