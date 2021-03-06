package com.example.checkbox.dialog

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.NumberPicker
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.DialogFragment
import com.example.checkbox.fragment.DateFragment
import com.example.checkbox.R
import kotlinx.android.synthetic.main.search_view.view.*
import java.util.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-25
 * @desc
 */
class YearMonthPickerDialog<Button : View?>(v : View, tag : String) : DialogFragment() {
    private var listener : DatePickerDialog.OnDateSetListener? = null
    val tagname : String = tag
    val v = v
    var cal = Calendar.getInstance()
    private var MAX_YEAR = 2099
    private var MIN_YEAR = 1980

    fun setListener(listener : DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }

    var btnConfirm : Button? = null
    var btnCancel : Button? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        val builder = AlertDialog.Builder(activity)
        val inflater = activity!!.layoutInflater
        val dialog : View = inflater.inflate(R.layout.year_month_picker, null).also {
            btnConfirm = it.findViewById<Button>(R.id.btn_confirm)
            btnCancel = it.findViewById<Button>(R.id.btn_cancel)
        }

        val monthPicker = dialog.findViewById<View>(R.id.picker_month) as NumberPicker
        val yearPicker = dialog.findViewById<View>(R.id.picker_year) as NumberPicker
        btnConfirm?.setOnClickListener(View.OnClickListener {
            if (tagname == "calendar") {
                val y = DateFragment.calDate.get(Calendar.YEAR)
                val m = DateFragment.calDate.get(Calendar.MONTH)
                DateFragment.calDate.add(Calendar.MONTH, 12 * (yearPicker.value.toInt() - y) + (monthPicker.value.toInt() - m))
                val month_left_button = v.findViewById<AppCompatImageButton>(R.id.cal_month_left)
                month_left_button?.performClick()
            } else if (tagname == "search") {
                if (monthPicker.value < 10) v.searchview.setQuery("${yearPicker.value}" + "" + "0${monthPicker.value}", true)
                else v.searchview.setQuery("${yearPicker.value}" + "" + "${monthPicker.value}", true)
            }
            dismiss()
        })
        btnCancel?.setOnClickListener(View.OnClickListener {
            dismiss()
        })
        monthPicker.minValue = 1
        monthPicker.maxValue = 12
        monthPicker.value = cal[Calendar.MONTH] + 1
        val year = cal[Calendar.YEAR]
        yearPicker.minValue = MIN_YEAR
        yearPicker.maxValue = MAX_YEAR
        yearPicker.value = year
        builder.setView(dialog)

        return builder.create()
    }
}















