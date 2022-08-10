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

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.navigation.findNavController
import androidx.navigation.fragment.findNavController
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.floatingactionbutton.FloatingActionButton
import es.terax.traincut.EntryModel
import es.terax.traincut.R
import es.terax.traincut.SQLHelper
import es.terax.traincut.databinding.FragmentLogBinding
import java.lang.Exception

class LogFragment : Fragment() {

    private var _binding: FragmentLogBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        /*val logViewModel =
            ViewModelProvider(this)[LogViewModel::class.java]*/

        _binding = FragmentLogBinding.inflate(inflater, container, false)
        val root: View = binding.root
        val databaseHandler = SQLHelper(requireContext(), null)
        loadIntoList(databaseHandler)

        val floatingActionButton: FloatingActionButton = binding.floatingActionButton
        val actionEntry = LogFragmentDirections.actionNavigationLogToEntryFragment(false)
        floatingActionButton.setOnClickListener {
            root.findNavController().navigate(actionEntry)
        }
        return root
    }

    private fun loadIntoList(databaseHandler: SQLHelper){
        val dataList = ArrayList<EntryModel>()
        val cursor = databaseHandler.getAllRow()!!

        try {
            if (cursor.moveToFirst()) {
                val imageView2 = binding.imageView2
                imageView2.visibility = View.GONE
                do {
                    val entryModel = EntryModel()
                    entryModel.entryId = cursor.getInt(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_ID))
                    entryModel.entryOrigin =
                        cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_ORIGIN))
                    entryModel.entryDestination =
                        cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_DESTINATION))
                    entryModel.entryDeparture =
                        cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_DEPARTURE))
                    entryModel.entryArrival =
                        cursor.getString(cursor.getColumnIndexOrThrow(SQLHelper.COLUMN_ARRIVAL))
                    dataList.add(entryModel)
                } while (cursor.moveToNext())
                val recyclerView: RecyclerView = binding.recyclerView
                recyclerView.layoutManager =
                    LinearLayoutManager(requireContext(), RecyclerView.VERTICAL, false)
                recyclerView.adapter = LogAdapter(dataList, requireContext(), findNavController())
            }
            else {
                binding.txtEmpty.text = getString(R.string.log_empty)
            }
        } catch (e: Exception)
        {
            MaterialAlertDialogBuilder(requireContext())
                .setTitle(resources.getString(R.string.error_entry_load))
                .setMessage(resources.getString(R.string.error_entry_load_desc))
                .setPositiveButton(resources.getString(R.string.accept)) { _, _ ->
                    // Do nothing.
                    // TODO: Provide a solution.
                }
                .show()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
