package com.example.checkbox

import android.database.ContentObserver
import android.os.Handler
import com.example.checkbox.Activity.MainActivity
import com.example.checkbox.Adapter.RecyclerAdapterFolder
import com.example.checkbox.db.MediaStore_Dao


class DataBaseObserver(handler: Handler, val adapter : RecyclerAdapterFolder) : ContentObserver(handler) {
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        adapter.setThumbnailList(MediaStore_Dao.getNameDir(adapter.context!!.applicationContext))
    }
}

class ChangeObserver(handler: Handler, val activity : MainActivity) : ContentObserver(handler){
    override fun onChange(selfChange: Boolean) {
        super.onChange(selfChange)
        activity.CheckChangeData()
    }
}