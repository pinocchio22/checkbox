package com.example.checkbox

import android.app.DatePickerDialog
import android.view.View
import androidx.fragment.app.DialogFragment

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-25
 * @desc
 */
class YearMonthPickerDialog<Button : View?>(v : View, tag : String) : DialogFragment {
    private var listener : DatePickerDialog.OnDateSetListener? = null

    fun setListener(listener : DatePickerDialog.OnDateSetListener?) {
        this.listener = listener
    }
}