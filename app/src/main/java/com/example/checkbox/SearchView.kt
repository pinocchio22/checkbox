package com.example.checkbox

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkbox.MainActivity.Companion.folder_type
import kotlinx.android.synthetic.main.main_photoview.*
import kotlinx.android.synthetic.main.search_view.*
import java.util.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-15
 * @desc
 */
class SearchView : AppCompatActivity() {

    private lateinit var vm : PhotoViewModel
    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerAdapter : RecyclerAdapterFolder
    private var thumbnailList = arrayListOf<thumbnailData>()
    private var mLastClickTime : Long = 0

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.search_view)
        searchview.queryHint = "CHECKBOX 검색"
        searchview.onActionViewExpanded()
        searchview.isIconified = false
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)
        dateQuery()
        searchResult()
    }

    private fun setView(type : String) {
        var inputnum = 0
        when (type) {
            "tag_name" -> {inputnum = 1}
            "date_name" -> {inputnum = 2}
            "location_name" -> {inputnum = 3}
            "file_name" -> {inputnum = 4}
        }
        recyclerView = findViewById(R.id.search_recyclerView)
        recyclerAdapter = RecyclerAdapterFolder(this, thumbnailList, inputnum)
        {thumbnailData ->
        if (SystemClock.elapsedRealtime() - mLastClickTime > 1000) {
            if ((MainActivity.location_type ==0) && type == "location_name") {
                val intent = Intent(this, Main_Map::class.java)
                intent.putExtra("location_name", thumbnailData.data)
                startActivityForResult(intent, 800)
            }
            else if (type == "date_name") {
                val cal : Calendar = Calendar.getInstance()
                cal.set(thumbnailData.data.substring(0, 4).toInt(), thumbnailData.data.substring(6, 8).toInt() - 1, thumbnailData.data.substring(10, 12).toInt(), 0, 0, 0)
                intent.putExtra(type, cal.time)
                startActivityForResult(intent, 201)
            }
            else {
                val intent = Intent(this, MainPhotoView::class.java)
                intent.putExtra(type, thumbnailData.data)
                startActivityForResult(intent, 201)
            }
        }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        recyclerView.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), folder_type)
        recyclerView.layoutManager = lm
    }

    private fun setPhotoSize(row : Int, padding : Int) {
        recyclerView = findViewById<RecyclerView>(R.id.search_recyclerView)
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener( object : ViewTreeObserver.OnGlobalLayoutListener {
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


















