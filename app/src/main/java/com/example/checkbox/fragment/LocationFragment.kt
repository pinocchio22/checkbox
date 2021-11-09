package com.example.checkbox.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.SystemClock
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkbox.Activity.MainActivity
import com.example.checkbox.Activity.Main_Map
import com.example.checkbox.Activity.MainActivity.Companion.folder_type
import com.example.checkbox.Activity.MainActivity.Companion.location_type
import com.example.checkbox.Activity.MainPhotoView
import com.example.checkbox.Adapter.RecyclerAdapterFolder
import com.example.checkbox.R
import com.example.checkbox.db.PhotoViewModel
import com.example.checkbox.db.thumbnailData
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.main_activity.view.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-26
 * @desc
 */
class LocationFragment (val v : AppBarLayout) : Fragment() {

    val ab = v
    private var thisview : View? = null
    private lateinit var liveData : LiveData<List<thumbnailData>>
    private lateinit var recyclerAdapter : RecyclerAdapterFolder
    private var mLastClickTime : Long = 0

    override fun onCreateView(
            inflater: LayoutInflater,
            container: ViewGroup?,
            savedInstanceState: Bundle?
    ): View? {
        ab.main_toolbar.visibility = View.VISIBLE
        ab.setExpanded(true, true)

        thisview = inflater.inflate(R.layout.fragment_view, container, false)
        val vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        setView(thisview)
        liveData = vm.getLocationDir()
        liveData.observe(this, Observer { list ->
            val arrayList = ArrayList(list)
            recyclerAdapter.setThumbnailList(arrayList)
        })

        return thisview
    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(this.view!!, folder_type, 10)
    }

//    override fun onPause() {
//        super.onPause()
//    }

    private fun setView(view : View?) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.fragment_RecycleView)
        recyclerAdapter = RecyclerAdapterFolder(activity, ArrayList(), 3)
        {thumbnailData ->
            if (SystemClock.elapsedRealtime() - mLastClickTime > 300) {
                if(location_type == 1) {
                    val intent = Intent(activity, MainPhotoView::class.java)
                    intent.putExtra("location_name", thumbnailData.data)
                    startActivityForResult(intent, 201)
                }
                else {
                    val intent = Intent(activity, Main_Map::class.java)
                    intent.putExtra("location_name", thumbnailData.data)
                    startActivityForResult(intent, 800)
                }
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        recyclerView?.adapter = recyclerAdapter

        val lm = GridLayoutManager(MainActivity(), folder_type)
        recyclerView?.layoutManager = lm
    }

    private fun setPhotoSize(view : View, row : Int, padding : Int) {
        val recyclerView = view.findViewById<RecyclerView>(R.id.fragment_RecycleView)
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




