package com.example.checkbox

import android.view.LayoutInflater
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

    private val inflater : LayoutInflater = LayoutInflater.from(context)

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val calendar = Calendar.getInstance()
        val date = getItem(position)!!

        calendar.time = date
        val month = calendar.get(Calendar.MONTH)
        val week = calendar.get(Calendar.DAY_OF_WEEK)

        if (view == null) view = inflater.inflate(R.layout.cal)
    }
}