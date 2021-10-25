package com.example.checkbox

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.schedule_insert.view.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-25
 * @desc
 */
class scheduleDialog(v : View, vm : PhotoViewModel, cal : Calendar) : DialogFragment() {

    private lateinit var recyclerView : RecyclerView
    private val v = v
    private val calendar = cal
    private val vm = vm
    private var interfaceDlg : dialogListener? = null

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        //stringToCalendar()
        recyclerView = v.findViewById(R.id.schedule_RecycleView)
        setView(ArrayList())
        val formatter = SimpleDateFormat("yyyy년 MM월 dd일", Locale.getDefault())
        val getdate = formatter.format(calendar.time) + "일정"
        var calData : CalendarData? = null

        v.schedule_title.text = getdate

        val maindlgBuilder : androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder( // 메인 다이얼로그
            context!!)
        maindlgBuilder.setView(v)
        val dlg = maindlgBuilder.create()

        DBThread.execute {
            getOpenDirByCursor(vm, vm.getOpenDateDirCursor(context!!, calendar))
            calData = vm.getCalendarData(calendar)
            if (calData?.title != null) v.scheduleTitle_text.setText(calData?.title)
            if (calData?.memo != null) v.scheduleMemo_text.setText(calData?.memo)
            MainHandler.post { delete_schedule(dlg, calData) }
        }

        v.schedule_ok.setOnClickListener {
            val title = v.scheduleTitle_text.text.toString()
            val memo = v.scheduleMemo_text.text.toString()
            if (title.isEmpty())
                Toast.makeText(context!!, "주제를 입력해주세요.", Toast.LENGTH_SHORT).show()
            else {
                DBThread.execute{vm.Insert(Date(calendar.time.time), title, memo) }
                Toast.makeText(context!!, "일정이 등록되었습니다.", Toast.LENGTH_SHORT).show()
                dlg.cancel()
                interfaceDlg!!.refresh()
            }
        }
        v.schedule_cancel.setOnClickListener {
            dlg.cancel()
        }
        return dlg
    }

    
}


















