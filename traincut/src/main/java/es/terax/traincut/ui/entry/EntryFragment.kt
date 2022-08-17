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
        val inputName = binding.inputName
        val inputTrainNumber = binding.inputTrainNumber
        val inputSeat = binding.inputSeat
        val inputSeatClass = binding.inputSeatClass
        val inputCar = binding.inputCar

        val entryData: EntryModel

        if (args.isEdit) {
            binding.topAppBar.menu.findItem(R.id.actionSave).isEnabled = false
            topAppBar.title = getString(R.string.edit_entry)
            // Now, we load the entry data from the ID provided by the action argument.
            entryData = getEntryData(args.positionId)
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
            inputName.setText(entryData.entryName)
            inputTrainNumber.setText(if (entryData.entryTrainNumber == 0) {
                null
            } else {
                entryData.entryTrainNumber.toString()
            })
            inputSeat.setText(entryData.entrySeat)
            inputSeatClass.setText(entryData.entrySeatClass)
            inputCar.setText(if (entryData.entryCar == 0) {
                null
            } else {
                entryData.entryCar.toString()
            })
        } else {
            entryData = EntryModel()
        }

        topAppBar.setNavigationOnClickListener {
            if (hasChanged(entryData)) {
                MaterialAlertDialogBuilder(requireContext())
                    .setTitle(resources.getString(R.string.entry_unsaved_dialog))
                    .setMessage(resources.getString(R.string.entry_unsaved_dialog_desc))
                    .setPositiveButton(resources.getString(R.string.yes)) { _, _ ->
                        root.findNavController().navigateUp()
                    }
                    .setNeutralButton(resources.getString(R.string.cancel)) { _, _ ->
                        // Abort navigation up.
                    }
                    .show()
            } else {
                root.findNavController().navigateUp()
            }
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
                            inputName.text.toString(),
                            if (inputTrainNumber.text.toString().isEmpty()) {
                                0
                            } else {
                                inputTrainNumber.text.toString().toInt()
                            },
                            inputSeat.text.toString(),
                            inputSeatClass.text.toString(),
                            if (inputCar.text.toString().isEmpty()) {
                                0
                            } else {
                                inputCar.text.toString().toInt()
                            },
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
                            inputName.text.toString(),
                            if (inputTrainNumber.text.toString().isEmpty()) {
                                0
                            } else {
                                inputTrainNumber.text.toString().toInt()
                            },
                            inputSeat.text.toString(),
                            inputSeatClass.text.toString(),
                            if (inputCar.text.toString().isEmpty()) {
                                0
                            } else {
                                inputCar.text.toString().toInt()
                            },
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
            validateInput(binding.fieldOrigin, text)
            hasChanged(entryData)
        }
        inputDestination.doOnTextChanged { text, _, _, _ ->
            validateInput(binding.fieldDestination, text)
            hasChanged(entryData)
        }
        inputDeparture.doOnTextChanged { text, _, _, _ ->
            validateInput(binding.fieldDeparture, text)
            hasChanged(entryData)
        }
        inputDeparture.setOnClickListener{
            showDateTimePickerDialog(binding.fieldDeparture, inputDeparture)
        }
        inputArrival.doOnTextChanged { text, _, _, _ ->
            validateInput(binding.fieldArrival, text)
            hasChanged(entryData)
        }
        inputArrival.setOnClickListener {
            showDateTimePickerDialog(binding.fieldArrival, inputArrival)
        }
        inputName.doOnTextChanged { _, _, _, _ ->
            hasChanged(entryData)
        }
        inputTrainNumber.doOnTextChanged { _, _, _, _ ->
            hasChanged(entryData)
        }
        inputSeat.doOnTextChanged { _, _, _, _ ->
            hasChanged(entryData)
        }
        inputSeatClass.doOnTextChanged { _, _, _, _ ->
            hasChanged(entryData)
        }
        inputCar.doOnTextChanged { _, _, _, _ ->
            hasChanged(entryData)
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
            entryModel.entryName =
                cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_NAME))
            entryModel.entryTrainNumber =
                cursor.getInt(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_TRAIN_NUMBER))
            entryModel.entrySeat =
                cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_SEAT))
            entryModel.entrySeatClass =
                cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_SEAT_CLASS))
            entryModel.entryCar =
                cursor.getInt(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_CAR))
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

    private fun hasChanged(entryData: EntryModel): Boolean {
        val dataInputs = arrayOf(binding.inputOrigin,
            binding.inputDestination,
            binding.inputDeparture,
            binding.inputArrival,
            binding.inputName,
            binding.inputTrainNumber,
            binding.inputSeat,
            binding.inputSeatClass,
            binding.inputCar)
        for (userInput in dataInputs) {
            val format = DateTimeFormatter.ofLocalizedDateTime(FormatStyle.MEDIUM,
                FormatStyle.SHORT)
            val userData = when (userInput) {
                binding.inputOrigin -> entryData.entryOrigin
                binding.inputDestination -> entryData.entryDestination
                binding.inputDeparture -> if (entryData.entryDeparture == 0.toLong()) {
                    ""
                } else {
                    val departDateTime = LocalDateTime.ofEpochSecond(entryData.entryDeparture,0,
                        ZoneOffset.UTC)
                    departDateTime.format(format)
                }
                binding.inputArrival -> if (entryData.entryArrival == 0.toLong()) {
                    ""
                } else {
                    val arrivalDateTime = LocalDateTime.ofEpochSecond(entryData.entryArrival, 0,
                        ZoneOffset.UTC)
                        arrivalDateTime.format(format)
                }
                binding.inputName -> entryData.entryName
                binding.inputTrainNumber -> if (entryData.entryTrainNumber == 0) {
                    ""
                } else {
                    entryData.entryTrainNumber.toString()
                }
                binding.inputSeat -> entryData.entrySeat
                binding.inputSeatClass -> entryData.entrySeatClass
                else -> if (entryData.entryCar == 0) {
                    ""
                } else {
                    entryData.entryCar.toString()
                }
            }
            if (userInput.text.toString() != userData) {
                binding.topAppBar.menu.findItem(R.id.actionSave).isEnabled = true
                return true
            }
        }
        binding.topAppBar.menu.findItem(R.id.actionSave).isEnabled = false
        return false
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
