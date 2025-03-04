package com.example.wallpaperrotator.ui

import android.app.Activity
import android.app.AlertDialog
import android.content.DialogInterface
import android.view.LayoutInflater
import android.widget.RadioButton
import android.widget.RadioGroup
import com.example.simplywallpapers.R

class IntervalSelector(private val activity: Activity, private val listener: IntervalSelectedListener) {

    private val intervals = listOf("6 hours", "12 hours", "1 day", "1 week")

    fun showIntervalSelector() {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Select Interval")

        val inflater = LayoutInflater.from(activity)
        val view = inflater.inflate(R.layout.interval_selector_dialog, null)
        val radioGroup = view.findViewById<RadioGroup>(R.id.radioGroup)

        intervals.forEachIndexed { index, interval ->
            val radioButton = RadioButton(activity)
            radioButton.text = interval
            radioGroup.addView(radioButton)
            if (index == 0) { // Default to 6 hours
                radioButton.isChecked = true
            }
        }

        builder.setView(view)
            .setPositiveButton("Set") { dialog, which ->
                val selectedId = radioGroup.checkedRadioButtonId
                val selectedRadioButton = view.findViewById<RadioButton>(selectedId)
                val selectedInterval = selectedRadioButton?.text.toString()
                val intervalInHours = when (selectedInterval) {
                    "6 hours" -> 6
                    "12 hours" -> 12
                    "1 day" -> 24
                    "1 week" -> 168
                    else -> 6
                }
                listener.onIntervalSelected(intervalInHours)
            }
            .setNegativeButton("Cancel") { dialog, which ->
                dialog.dismiss()
            }
            .show()
    }

    interface IntervalSelectedListener {
        fun onIntervalSelected(intervalInHours: Int)
    }
}