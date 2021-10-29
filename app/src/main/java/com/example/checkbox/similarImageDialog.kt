package com.example.checkbox

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.checkbox.MainPhotoView.Companion.list
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList
import kotlin.collections.HashSet

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-29
 * @desc
 */
class similarImageDialog (v : View, vm : PhotoViewModel, location : String, date : String, index : Int) : DialogFragment {

    private lateinit var recyclerView : RecyclerView
    private val v = v
    private val vm = vm
    private val location = location
    private val calendar = Calendar.getInstance()
    private val index = index
    private var checkboxSet : HashSet<Long> = hashSetOf()
    private lateinit var recyclerAdapter : RecyclerAdapterDialog
    private val date = date

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        stringToCalendar()
        recyclerView = v.findViewById(R.id.similar_RecycleView)
        setView(ArrayList())

        val liveData = vm.getOpenLocationDirIdList(location)
        liveData.observe(this, androidx.lifecycle.Observer { idList ->
            DBThread.execute {
                val initlist = vm.getThumbnailListByIdList(context!!, idList, calendar)
                if (initlist.isEmpty())
                    initlist.add(list[index])
                checkboxSet = recyclerAdapter.setThumbnailList(initlist)
            }
        })

        val maindlgBuilder : androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(   // 메인 다이얼로그
            context!!)
        maindlgBuilder.setView(v)

        val dlg = maindlgBuilder.create()
        saveSimilarPhoto(dlg)
        return dlg
    }

    private fun stringToCalendar() {
        val formatter = SimpleDateFormat("yyyy년 MM월 dd일 (E) / HH:mm:ss", Locale.getDefault())
        val tempDate = formatter.parse(date)
        calendar.setTime(tempDate)
    }


}













