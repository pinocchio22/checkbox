package com.example.checkbox

import android.annotation.SuppressLint
import android.os.Build
import android.os.Bundle
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.fragment_cal.*
import kotlinx.android.synthetic.main.fragment_cal.view.*
import kotlinx.android.synthetic.main.main_activity.view.*
import java.util.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-20
 * @desc
 */
class DateFragment(val v : AppBarLayout) : Fragment() {

    val ab = v
    private var thisview : View? = null
    private var calendar_allheader : View? = null
    private lateinit var vm : PhotoViewModel

    companion object {
        var CalendarCK : Boolean = false

    }

    @SuppressLint   //검사에서 제외할 항목을 지정
    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        ab.main_toolbar.visibility = View.GONE
        ab.setExpanded(true,false)

        thisview = inflater.inflate(R.layout.fragment_cal, container, false)

        calendar_allheader = thisview?.findViewById(R.id.calendar_allheader) as View
        setView(thisview)
        setHeader(thisview)
        setGridLayout(thisview)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        updateCalendar(thisview, calDate.clone() as Calendar)

        // 상단 스와이프 제스처
        val gestureListener = SwipeGesture(calendar_allheader!!)
        val gestureDetector = GestureDetector(calendar_allheader!!.context, gestureListener)
        calendar_allheader!!.setOnTouchListener{ v, event ->
            return@setOnTouchListener gestureDetector.onTouchEvent(event)
        }

        thisview?.scheduleOn?.setOnClickListener {
            thisview?.scheduleOff!!.visibility = View.VISIBLE
            thisview?.scheduleOn!!.visibility = View.GONE
            CalendarCK = true
            Toast.makeText(context!!, "날짜를 선택하여 일정을 등록해보세요.", Toast.LENGTH_SHORT).show()
        }
        thisview?.scheduleOn?.setOnClickListener {
            thisview?.scheduleOn!!.visibility = View.VISIBLE
            thisview?.scheduleOff!!.visibility = View.GONE
            CalendarCK = false
            Toast.makeText(context!!, "등록이 완료되었습니다.", Toast.LENGTH_SHORT).show()
        }
        return thisview
    }

    
}























