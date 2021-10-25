package com.example.checkbox

import android.app.AlertDialog
import android.app.DatePickerDialog
import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
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
    }
}















