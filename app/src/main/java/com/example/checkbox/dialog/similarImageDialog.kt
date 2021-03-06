package com.example.checkbox.dialog

import android.app.Activity
import android.app.Dialog
import android.content.DialogInterface
import android.os.Build
import android.os.Bundle
import android.view.View
import android.view.ViewTreeObserver
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkbox.*
import com.example.checkbox.Activity.MainPhotoView.Companion.list
import com.example.checkbox.Activity.Main_Map.Companion.latLngList
import com.example.checkbox.Activity.Main_Map.Companion.removelist
import com.example.checkbox.Adapter.RecyclerAdapterDialog
import com.example.checkbox.db.PhotoViewModel
import com.example.checkbox.db.thumbnailData
import kotlinx.android.synthetic.main.similar_image_layout.view.*
import kotlinx.android.synthetic.main.similar_image_select.view.*
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
class similarImageDialog (v : View, vm : PhotoViewModel, location : String, date : String, index : Int) : DialogFragment() {

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

        val maindlgBuilder : androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(   // ?????? ???????????????
            context!!)
        maindlgBuilder.setView(v)

        val dlg = maindlgBuilder.create()
        saveSimilarPhoto(dlg)
        return dlg
    }

    private fun stringToCalendar() {
        val formatter = SimpleDateFormat("yyyy??? MM??? dd??? (E) / HH:mm:ss", Locale.getDefault())
        val tempDate = formatter.parse(date)
        calendar.setTime(tempDate)
    }

    private fun saveSimilarPhoto(dlg : androidx.appcompat.app.AlertDialog) {
        v.similar_cancel.setOnClickListener {
            dlg.cancel()
        }
        v.similar_ok.setOnClickListener {
            if (checkboxSet.size == 0) {
                Toast.makeText(context!!, "????????? ????????? ????????????.", Toast.LENGTH_SHORT).show()
            } else {
                val warningBuilder : androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder ( context!!)    // ?????? ???????????????
                warningBuilder.setTitle("??????")    // ??????
                warningBuilder.setMessage("????????? ???????????? ???????????????. \n?????? ?????????????????????????\n\n (????????? ?????? : ${checkboxSet.size} ???")  // ?????????
                warningBuilder.setCancelable(false)
                warningBuilder.setPositiveButton("??????", DialogInterface.OnClickListener { dialog, which ->
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
                    val pager : Activity = context!! as Activity    // ???????????? ??????

                    pager.finish()
                    Toast.makeText(context!!, "${removenum} ?????? ????????? ?????? ?????? ???????????????.", Toast.LENGTH_SHORT).show()
                })
                warningBuilder.setNegativeButton("??????", DialogInterface.OnClickListener { dialog, which ->
                    dialog.cancel()
                })
                val dlgWarning = warningBuilder.create()
                dlgWarning.show()
            }
        }
    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(2, 2)
    }

    private fun setView(list : ArrayList<thumbnailData>) {
        recyclerAdapter = RecyclerAdapterDialog(activity, list) {thumbnailData ->
            val similarImageSelectView : View = layoutInflater.inflate(R.layout.similar_image_select, null)
            ImageLoder.execute(ImageLoad(context!!, similarImageSelectView.select_photo, thumbnailData.photo_id, 0))
            val dlgBuilder : AlertDialog.Builder = AlertDialog.Builder(   // ?????? ???????????????
                context!!)

            dlgBuilder.setView(similarImageSelectView)
            val dlgselect = dlgBuilder.create()

            dlgselect.show()
            similarImageSelectView.select_cancel.setOnClickListener {
                dlgselect.cancel()
            }
        }
        recyclerView.adapter = recyclerAdapter
        val lm = GridLayoutManager(context, 2)
        recyclerView.layoutManager = lm
    }

    private fun setPhotoSize(row : Int, padding : Int) {
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener {
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                val width = recyclerView.width
                val size = width / row - 2 * padding
                recyclerAdapter.setPhotoSize(size, padding)
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener(this)
            }
        })
    }
}













