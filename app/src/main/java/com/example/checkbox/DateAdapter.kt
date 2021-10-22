package com.example.checkbox

import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import androidx.fragment.app.FragmentActivity
import java.util.*
import kotlin.collections.ArrayList

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-22
 * @desc
 */
class DateAdapter(context : FragmentActivity, size : Pair<Int, Int>?, days : ArrayList<Date>, inputMonth : Int, val itemClick : (Date, Int) -> Unit) :
        ArrayAdapter<Date>(context, R.layout.fragment_cal, days) {

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        
        return super.getView(position, convertView, parent)
    }
}