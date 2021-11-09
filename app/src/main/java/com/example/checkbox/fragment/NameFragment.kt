package com.example.checkbox.fragment

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.os.SystemClock
import android.provider.MediaStore
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.ViewTreeObserver
import androidx.annotation.RequiresApi
import androidx.fragment.app.Fragment
import androidx.lifecycle.ViewModelProviders
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.example.checkbox.Activity.MainActivity
import com.example.checkbox.Activity.MainActivity.Companion.folder_type
import com.example.checkbox.Activity.MainPhotoView
import com.example.checkbox.Adapter.RecyclerAdapterFolder
import com.example.checkbox.DataBaseObserver
import com.example.checkbox.DirectoryThread
import com.example.checkbox.MainHandler
import com.example.checkbox.R
import com.example.checkbox.db.PhotoViewModel
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.main_activity.view.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-15
 * @desc
 */
class NameFragment(val v: AppBarLayout) : Fragment() {
    private lateinit var thisview: View
    private lateinit var recyclerView : RecyclerView
    private lateinit var recyclerAdapter : RecyclerAdapterFolder
    private lateinit var vm : PhotoViewModel
    private lateinit var observer : DataBaseObserver
    private var mLastClickTime: Long = 0
    private val ab = v

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {

        ab.main_toolbar.visibility = View.VISIBLE
        ab.setExpanded(true,true)

        thisview = inflater.inflate(R.layout.fragment_view,container,false)
        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        setView(thisview)
        observer = DataBaseObserver(Handler(), recyclerAdapter)

        return thisview
    }

    override fun onResume() {
        super.onResume()
        setPhotoSize(this.view!!, folder_type, 10)
        DirectoryThread.execute{
            val list = vm.getNameDir(this.context!!)
            MainHandler.post { recyclerAdapter.setThumbnailList(list) }
        }
        this.context!!.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer)
    }

    override fun onPause() {
        super.onPause()
        this.context!!.contentResolver.unregisterContentObserver(observer)
    }

    private fun setView(view : View?) {
        val recyclerView = view?.findViewById<RecyclerView>(R.id.fragment_RecycleView)
        recyclerAdapter = RecyclerAdapterFolder(activity, ArrayList(), 0)
        {thumbnailData ->
            if (SystemClock.elapsedRealtime() - mLastClickTime > 300) {
                val intent = Intent(activity, MainPhotoView::class.java)
                intent.putExtra("dir_name", thumbnailData.data)
                startActivityForResult(intent, 202)
            }
            mLastClickTime = SystemClock.elapsedRealtime()
        }
        recyclerView?.adapter = recyclerAdapter
        val lm = GridLayoutManager(MainActivity(), folder_type)
        recyclerView!!.layoutManager = lm
    }

    private fun setPhotoSize(view : View, row : Int, padding : Int) {
        recyclerView = view.findViewById(R.id.fragment_RecycleView)
        recyclerView.viewTreeObserver.addOnGlobalLayoutListener(object : ViewTreeObserver.OnGlobalLayoutListener{
            @RequiresApi(Build.VERSION_CODES.JELLY_BEAN)
            override fun onGlobalLayout() {
                val width = recyclerView.width
                val size = width / row - 2 * padding
                recyclerAdapter.setPhotoSize(size, padding)
                recyclerView.viewTreeObserver.removeOnGlobalLayoutListener (this)
            }
        })
    }
}



























