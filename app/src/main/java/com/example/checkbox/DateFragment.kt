package com.example.checkbox

import android.annotation.SuppressLint
import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.util.DisplayMetrics
import android.view.GestureDetector
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.GridView
import android.widget.LinearLayout
import android.widget.TextView
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatImageButton
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import com.google.android.material.appbar.AppBarLayout
import com.google.android.material.bottomnavigation.BottomNavigationView
import kotlinx.android.synthetic.main.fragment_cal.*
import kotlinx.android.synthetic.main.fragment_cal.view.*
import kotlinx.android.synthetic.main.main_activity.view.*
import java.time.YearMonth
import java.util.*
import kotlin.collections.ArrayList

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
    private lateinit var gridView : GridView
    private var size : Pair<Int, Int>? = null
    private var count = 0

    companion object {
        var CalendarCK : Boolean = false
        var calDate : Calendar = Calendar.getInstance()

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

    fun setView(view : View?) {
        gridView = view!!.findViewById(R.id.cal_grid)
    }

    fun setHeader(view : View?) {
        val month_left_button = view!!.findViewById<AppCompatImageButton>(R.id.cal_month_left)
        val month_right_button = view.findViewById<AppCompatImageButton>(R.id.cal_month_right)
        val month_text = view.findViewById<TextView>(R.id.cal_month_text)

        month_left_button.setOnClickListener {
            calDate.add(Calendar.MONTH, -1)
            updateCalendar(view, calDate.clone() as Calendar)
            setHeader(month_text)
        }

        month_right_button.setOnClickListener {
            calDate.add(Calendar.MONTH, 1)
            updateCalendar(view, calDate.clone() as Calendar)
            setHeader(month_text)
        }

        month_text.setOnClickListener(object : View.OnClickListener{
            override fun onClick(p0: View?) {
                val pd : YearMonthPicKerDialog<View> = YearMonthPickerDialog(view, "calendar")
                pd.show(childFragmentManager, "YearMonthPicKerTest")
            }
        })
        setHeaderDate(month_text)
    }

    private fun updateCalendar(view : View?, inputCalendar : Calendar) {
        val cells = ArrayList<Date>()

        // 해당 달의 1일으로 설정
        inputCalendar.set(Calendar.DAY_OF_MONTH, 1)
        val month = inputCalendar.get(Calendar.MONTH)

        // 월의 시작 요일 계산
        val monthBeginningCell = inputCalendar.get(Calendar.DAY_OF_WEEK) -1
        inputCalendar.add(Calendar.DAY_OF_MONTH, -monthBeginningCell)

        var count = 0
        do {
            for (i in 1..7) {
                cells.add(inputCalendar.time)
                inputCalendar.add(Calendar.DAY_OF_MONTH, 1)
            }
            ++count
        } while (inputCalendar.get(Calendar.MONTH) == month)

        if (gridView.adapter == null) {
            gridView.adapter = DateAdapter(activity!!, size, cells, month) { Date, num ->
                val cal = Calendar.getInstance()
                cal.time = Date(Date.time)
                if (!CalendarCK && num == 0) {
                    val intent = Intent(activity, MainPhotoView::class.java)
                    intent.putExtra("date_name", cal.time)
                    startActivityForResult(intent, 204)
                }
                else {
                    val scheduleDlg : View = layoutInflater.inflate(R.layout.schedule_insert, null)
                    val dlg = scheduleDialog(object  : scheduleDialog.dialogLister {
                        override fun refresh() {
                            updateCalendar(thisview, calDate.clone() as Calendar)
                        }
                    })
                    dlg.isCancelable = false
                    dlg.show(fragmentManager!!, "scheduleDialog")
                }
            }
        }
        else {
            val gridAdapter = gridView.adapter as DateAdapter
            gridAdapter.Update(cells, month)
        }

        // 이전 달과 주 개수가 같으면 실행할 필요 x
        if (this.count != count) {
            setColumnSize(view!!, count)
            this.count = count
        }

    }
    private fun setGridLayout(view : View?) {
        val gridViewWrapper = view?.findViewById<LinearLayout>(R.id.cal_grid_wrapper)
        val header = view?.findViewById<LinearLayout>(R.id.calendar_allheader)
        val statusBar = resources.getIdentifier("status_bar_height", "dimen", "android")
        val statusBarHeight = resources.getDimensionPixelSize(statusBar)

        val displayMetrics = DisplayMetrics()
        super.getActivity()!!.windowManager.defaultDisplay.getMetrics(displayMetrics)
        val bnv = super.getActivity()!!.findViewById<BottomNavigationView>(R.id.bottomNavigationView)

        val padding = header!!.paddingTop + header.paddingBottom + gridViewWrapper!!.paddingTop + gridViewWrapper.paddingBottom
        gridViewWrapper.layoutParams!!.height = displayMetrics.heightPixels - (header.layoutParams.height + bnv.height + statusBarHeight + padding)
    }

    private fun setColumnSize(view : View, count : Int) {
        val displayMetrics = DisplayMetrics() // 크기, 밀도 및 글꼴 크기와 같은 디스플레이에 대한 일반 정보를 설명하는 구조
        super.getActivity()!!.windowManager.defaultDisplay.getMetrics(displayMetrics)

        val gridViewWrapper = view.findViewById<LinearLayout>(R.id.cal_grid_wrapper)
        val density = context!!.resources.displayMetrics.density
        val width = (displayMetrics.widthPixels - gridViewWrapper.paddingLeft - gridViewWrapper.paddingRight - (10*density).toInt()) / 7 - 1*density.toInt()
        val height = (gridViewWrapper.layoutParams.height - 1*density.toInt()) / count - 1*density.toInt()

        val gridAdapter = gridView.adapter as DateAdapter
        size = Pair(width, height)
        gridAdapter.setDateSize(size as Pair<Int, Int>)
    }

    




}























