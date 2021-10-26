package com.example.checkbox

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.lifecycle.LiveData
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProviders
import com.example.checkbox.MainActivity.Companion.folder_type
import com.google.android.material.appbar.AppBarLayout
import kotlinx.android.synthetic.main.main_activity.view.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-26
 * @desc
 */
class LocationFragment (v : AppBarLayout) : Fragment() {

    val ab = v
    private var thisview : View? = null
    private lateinit var liveData : LiveData<List<thumbnailData>>
    private lateinit var recyclerAdapter : RecyclerAdapterFolder

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
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

    override fun onPause() {
        super.onPause()
    }




}




