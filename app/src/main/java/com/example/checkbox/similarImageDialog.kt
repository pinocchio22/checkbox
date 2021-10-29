package com.example.checkbox

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.RecyclerView
import com.example.checkbox.MainPhotoView.Companion.list
import com.example.checkbox.Main_Map.Companion.latLngList
import com.example.checkbox.Main_Map.Companion.removelist
import kotlinx.android.synthetic.main.similar_image_layout.view.*
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
    private var removenum = 0

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

    private fun saveSimilarPhoto(dlg : androidx.appcompat.app.AlertDialog) {
        v.similar_cancel.setOnClickListener {
            dlg.cancel()
        }
        v.similar_ok.setOnClickListener {
            if (checkboxSet.size == 0) {
                Toast.makeText(context!!, "체크된 사진이 없습니다.", Toast.LENGTH_SHORT).show()
            } else {
                val warninBuilder : androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder ( context!!)    // 경고 다이얼로그
                warninBuilder.setTitle("알림")    // 제목
                warninBuilder.setMessage("체크된 사진들을 삭제합니다. \n정말 삭제하시겠습니까?\n\n (체크된 사진 : ${checkboxSet.size} 개")  // 메시지
                warninBuilder.setCancelable(false)
                warninBuilder.setPositiveButton("확인", DialogInterface.OnClickListener { dialog, which ->
                    for (ckbox in checkboxSet) {
                        DeleteThread.execute { vm.Delete(context!!, ckbox) }
                        val index = list.indexOfFirst { it.photo_id == ckbox }
                        if (index >= 0) {
                            if (latLngList.isNotEmpty() && latLngList[index].id == list[index].photo_id) {
                                removelist.add(latLngList[index])
                                latLngList.removeAt(index)
                            }
                            list.removeAt(index)
                        }
                        removenum++
                    }
                    dialog.cancel()
                    dlg.cancel()
                    val pager : Activity = context!! as Activity    // 액티비티 종료

                    pager.finish()
                    Toast.makeText(context!!, "${removenum} 개의 사진이 삭제 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                })
                warninBuilder.setNegativeButton("취소", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
                val dlgWarning = warninBuilder.create()
                dlgWarning.show()
            }
        }
    }



}













