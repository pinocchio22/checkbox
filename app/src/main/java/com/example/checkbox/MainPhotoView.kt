package com.example.checkbox

import android.os.Bundle
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import kotlinx.android.synthetic.main.recyclerview.*

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

    companion object {
        var list = arrayListOf<thumbnailData>()
        var checkboxList = arrayListOf<checkboxData>()
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.thumbnail_similarview)

        recyclerView = findViewById(R.id.photo_recyclerView)
        recyclerAdapter = RecyclerAdapterPhoto(this, lst)
        recyclerView.adapter = recyclerAdapter
        list = recyclerAdapter.getThumbnailList()
        val lm = GridLayoutManager(Main_PhotoView(), photo_type)
        recyclerView.layoutManager = lm

        photolist_delete.setOnClickListener{
            btck(1)
        }
        photolist_deletecancel.setOnClickListener {
            btck(0)
        }
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