package com.example.checkbox

import android.database.ContentObserver
import android.os.Handler
import com.example.checkbox.RecyclerAdapterFolder


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