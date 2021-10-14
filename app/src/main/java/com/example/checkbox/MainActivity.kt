package com.example.checkbox

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView

class MainActivity : AppCompatActivity() {

    private lateinit var recyclerAdapter: RecyclerAdapterPhoto
    private lateinit var recyclerView : RecyclerView

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        recyclerView = findViewById(R.id.photo_recyclerView)
        recyclerAdapter = RecyclerAdapterPhoto(this, lst)
        recyclerView.adapter = recyclerAdapter
        list = recyclerAdapter.getThumbnailList()
        val lm = GridLayoutManager(Main_PhotoView(), photo_type)
        recyclerView.layoutManager = lm
    }
}