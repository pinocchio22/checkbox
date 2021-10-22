package com.example.checkbox

import android.graphics.Color
import android.graphics.Typeface
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.TextView
import androidx.fragment.app.FragmentActivity
import androidx.lifecycle.ViewModelProviders
import com.github.chrisbanes.photoview.PhotoView
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
    private var size : Pair<Int, Int>? = size
    private var inputCheck : Int = 0
    private val vm = ViewModelProviders.of(context).get(PhotoViewModel::class.java)
    private var mLastClickTime : Long = 0
    private var inputMonth : Int = inputMonth

    override fun getView(position: Int, convertView: View?, parent: ViewGroup): View {
        var view = convertView
        val calendar = Calendar.getInstance()
        val date = getItem(position)!!

        calendar.time = date
        val month = calendar.get(Calendar.MONTH)
        val week = calendar.get(Calendar.DAY_OF_WEEK)

        if (view == null) view = inflater.inflate(R.layout.calendar_day_layout, parent, false)
        val textView = view!!.findViewById<TextView>(R.id.calendar_day)
        val titleView = view!!.findViewById<TextView>(R.id.calendar_day_title)
        val count = view!!.findViewById<TextView>(R.id.calendar_day_count)

        view.layoutParams.width = size!!.first
        if (view.layoutParams.height != size!!.second) {
            view.layoutParams.height = size!!.second
            view.requestLayout()
        }

        setExtraDay(textView, month, week)

        textView.text = calendar.get(Calendar.DATE).toString()
        titleView.text = ""
        val ckNum = inputCheck
        DBThread.execute {
            val calData = vm.getCalendarData(calendar)
            val title = calData?.title ?: ""    // calData?.title 이 null 이면 "" = if (calData?.title != null) calData?.title else ""
            val size = vm.getDateAmount(context, calendar)
            MainHandler.post {
                if(size != 0) {
                    count.visibility = View.VISIBLE
                } else
                    count.visibility = View.GONE

                view.setOnClickListener {
                    if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
                        itemClick(date, 0)  // 1클릭
                    }
                    mLastClickTime = SystemClock.elapsedRealtime()
                }
                view.setOnClickListener {
                    itemClick(date, 1)
                    true
                }
                titleView.text = title
                count.text = size.toString()
            }
        }
        return view
    }

    fun setDateSize(size : Pair<Int, Int>) {
        this.size = size
        notifyDataSetChanged()
    }

    fun Update(cells : ArrayList<Date>, month: Int) {
        inputCheck++
        clear()
        MainHandler.removeMessages(0)
        addAll(cells)
        this.inputMonth = month
        notifyDataSetChanged()
    }

//    private fun setToday(textView : TextView, year : Int, month : Int, day : Int) {
//        val calendarToday = Calendar.getInstance()
//        if (year == calendarToday.get(Calendar.YEAR) &&
//                month == calendarToday.get(Calendar.MONTH) &&
//                day == calendarToday.get(Calendar.DAY_OF_MONTH)) {
//            textView.setTypeface(null, Typeface.BOLD)   // Typeface 클래스는 글꼴의 서체와 고유 스타일을 지정
//        } else {
//            textView.setTypeface(null, Typeface.NORMAL)
//        }
//    }

    private fun setExtraDay(textView : TextView, month : Int, week : Int) {
        // 다른 달 날짜
        if (month != inputMonth) {
            textView.setTextColor(Color.GRAY)
        }
        // saturday
        else if (week == Calendar.SATURDAY) {
            textView.setTextColor(Color.BLUE)
        }
        // sunday
        else if (week == Calendar.SUNDAY) {
            textView.setTextColor(Color.RED)
        }
        // extra
        else {
            textView.setTextColor(Color.BLACK)
        }
    }
}












