package com.example.checkbox

import android.app.Dialog
import android.os.Build
import android.os.Bundle
import android.view.View
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.AppCompatTextView
import androidx.fragment.app.DialogFragment

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
    }



}



