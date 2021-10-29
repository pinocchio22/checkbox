package com.example.checkbox

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment
import kotlinx.android.synthetic.main.tag_diaglog.view.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-29
 * @desc
 */
class tagInsertDialog (v : View, vm : PhotoViewModel, index : Int, tag_name : AppCompatTextView) : DialogFragment() {

    private val v = v
    private val vm = vm
    private val index = index
    private val tag_name = tag_name

    @RequiresApi(Build.VERSION_CODES.N)
    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        DBThread.execute {
            val tags = vm.getTagList(MainPhotoView.list[index].photo_id)
            MainHandler.post { tagsInit(v, tags) }
        }

        val dlgBuilder : androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(context!!)
        dlgBuilder.setTitle("태그 삽입")
        dlgBuilder.setMessage("삽입할 사진의 특징을 입력해주세요. \n태그를 수정하거나 삭제할 수도 있습니다.")
        dlgBuilder.setIcon(R.drawable.ic_tag)
        dlgBuilder.setCancelable(false)
        dlgBuilder.setView(v)
        val dlg = dlgBuilder.create()
        insert_tag_click(v, dlg)
        return dlg
    }

    private fun insert_tag_click(view : View, dlg : androidx.appcompat.app.AlertDialog) {
        val editlist = arrayListOf<TextView>(view.tag1_edit, view.tag2_edit, view.tag3_edit, view.tag4_edit, view.tag5_edit)
        tag_addRemove(view, editlist)
        insert_saveCancel(view, dlg, editlist)
    }



}



