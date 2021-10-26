package com.example.checkbox

import android.os.Build
import android.os.Bundle
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProviders
import kotlinx.android.synthetic.main.main_photoview.*
import kotlinx.android.synthetic.main.search_view.*

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-15
 * @desc
 */
class SearchView : AppCompatActivity() {

    private lateinit var vm : PhotoViewModel

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


}