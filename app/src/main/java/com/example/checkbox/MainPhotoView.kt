package com.example.checkbox

import android.content.DialogInterface
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.main_photoview.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-14
 * @desc
 */
class MainPhotoView : AppCompatActivity() {

    private lateinit var recyclerAdapter: RecyclerAdapterPhoto
    private lateinit var recyclerView : RecyclerView
    var radiobtck: Boolean = false
    private lateinit var vm : PhotoViewModel

    companion object {
        var list = arrayListOf<thumbnailData>()
        var checkboxList = arrayListOf<checkboxData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_photoview)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        radiobtck = radiobt.isChecked
        SetHeader()
        recyclerView = findViewById(R.id.photo_recyclerView)
        setView(arrayListOf())
        getExtra()

        updown_Listener(recyclerView)
        updownEvent()

        photolist_delete.setOnClickListener{
            btck(1)
        }
        photolist_deletecancel.setOnClickListener {
            btck(0)
        }
        radiobt.setOnClickListener {
            btck2()
        }
        photolist_deleteok.setOnClickListener {
            deletePhotoDlg()
        }
    }

    fun deletePhotoDlg() {
        val warningBuilder : androidx.appcompat.app.AlertDialog.Builder = androidx.appcompat.app.AlertDialog.Builder(
            this, // 경고 다이얼로그
        android.R.style.Theme_DeviceDefault_Light_Dialog_NoActionBar_MinWidth
        )
        warningBuilder.setTitle("알림")   //제목
        warningBuilder.setMessage("체크된 사진들을 모두 삭제합니다. \n정말 삭제하시겠습니까?")  //메세지
        warningBuilder.setCancelable(false)
        warningBuilder.setPositiveButton(
            "확인", DialogInterface.OnClickListener { dialog, which ->
                var i = 0
                var j = 0
                while (checkboxList.size != 0 && i < checkboxList.size) {
                    if (checkboxList[i].checked) {
                        val temp = checkboxList[i].id
                        list.removeAt(i)
                        checkboxList.removeAt(i)
                        recyclerAdapter.notifyDataSetChanged()
                        j = 1
                    }
                    else
                        i++
                }
                if (j == 1)
                    Toast.makeText(this, "사진이 삭제 완료 되었습니다.", Toast.LENGTH_SHORT)
                else
                    Toast.makeText(this, "삭제할 사진을 체크해주세요", Toast.LENGTH_SHORT)
            }
        )
        warningBuilder.setNegativeButton(
            "취소", DialogInterface.OnClickListener{ dialog, which ->
                dialog.cancel()
            })
        val dlgWarning = warningBuilder.create()
        dlgWarning.show()
    }

    private fun btck(n : Int) {
        appbar2.visibility = View.VISIBLE
        appbar2.setExpanded(true,true)
        recyclerAdapter.updateCheckbox(n)
        recyclerAdapter.notifyDataSetChanged()

        if (n==1) {
            photolist_delete.visibility = View.GONE
            photolist_deleteok.visibility = View.VISIBLE
            photolist_deletecancel.visibility = View.VISIBLE
            radio.visibility = View.VISIBLE
        }
        else {
            photolist_delete.visibility = View.VISIBLE
            photolist_deleteok.visibility = View.GONE
            photolist_deletecancel.visibility = View.GONE
            radio.visibility = View.GONE
        }
    }

    private fun btck2() {
        if(radiobtck == true) {
            recyclerAdapter.setCheckAll(false)
            radio.isChecked = false
            radiobtck = false
        }
        else {
            recyclerAdapter.setCheckAll(true)
            radiobtck = true
        }
    }
}