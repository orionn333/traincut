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
import android.widget.TextView
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProvider
import es.terax.traincut.databinding.FragmentLogBinding

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
        val logViewModel =
            ViewModelProvider(this)[LogViewModel::class.java]

        _binding = FragmentLogBinding.inflate(inflater, container, false)
        val root: View = binding.root

        val textDestination: TextView = binding.textDestination
        logViewModel.destination.observe(viewLifecycleOwner) {
            textDestination.text = it
        }
        val textDate: TextView = binding.textDate
        logViewModel.tripDate.observe(viewLifecycleOwner) {
            textDate.text = it
        }
        val originTime = binding.originTime
        logViewModel.originTime.observe(viewLifecycleOwner) {
            originTime.text = it
        }
        val destinationTime = binding.destinationTime
        logViewModel.destinationTime.observe(viewLifecycleOwner) {
            destinationTime.text = it
        }
        return root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
