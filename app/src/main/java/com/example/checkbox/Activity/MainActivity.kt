package com.example.checkbox.Activity

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.content.Intent
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.os.Handler
import android.provider.MediaStore
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.Toolbar
import androidx.core.content.FileProvider
import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentManager
import androidx.fragment.app.FragmentTransaction
import androidx.lifecycle.ViewModelProviders
import com.example.checkbox.*
import com.example.checkbox.db.MediaStore_Dao
import com.example.checkbox.db.PhotoViewModel
import com.example.checkbox.db.TagData
import com.example.checkbox.fragment.DateFragment
import com.example.checkbox.fragment.LocationFragment
import com.example.checkbox.fragment.NameFragment
import com.example.checkbox.fragment.TagFragment
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.firebase.ml.naturallanguage.FirebaseNaturalLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslatorOptions
import com.google.firebase.ml.vision.FirebaseVision
import com.google.firebase.ml.vision.common.FirebaseVisionImage
import kotlinx.android.synthetic.main.main_activity.*
import java.io.File
import java.io.IOException
import java.text.SimpleDateFormat
import java.util.*
import java.util.concurrent.LinkedBlockingQueue
import java.util.concurrent.ThreadPoolExecutor
import java.util.concurrent.TimeUnit


class MainActivity : AppCompatActivity(), BottomNavigationView.OnNavigationItemSelectedListener {

    private lateinit var exitView: View
    private val REQUEST_TAKE_PHOTO = 200
    private var FINISH_INTERVAL_TIME: Long = 1500
    private var backPressedTime: Long = 0
    private var init : Boolean = false
    private lateinit var vm : PhotoViewModel
    private lateinit var observer: ChangeObserver
    lateinit var mCurrentPhotoPath: String

    companion object{
        var folder_type = 3
        var photo_type: Int = 3
        var location_type: Int = 0
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.main_activity)
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        bnv.setOnNavigationItemSelectedListener(this)
        exitView = layoutInflater.inflate(R.layout.exit_layout, null)

        SetHeader()
        init()

        vm = ViewModelProviders.of(this).get(PhotoViewModel::class.java)

        DBThread.execute {
            CheckChangeData()
        }

        observer = ChangeObserver(Handler(), this)
        this.contentResolver.registerContentObserver(MediaStore.Images.Media.EXTERNAL_CONTENT_URI, false, observer)

        val go_search = findViewById<ImageView>(R.id.main_search_button)
        go_search.setOnClickListener {
            val intent = Intent(this, SearchView::class.java)
            startActivity(intent)
        }

        val go_camera = findViewById<ImageView>(R.id.main_camera_button)
        go_camera.setOnClickListener {
            captureCamera()
            Toast.makeText(this, "?????? ??????", Toast.LENGTH_SHORT).show()
        }
    }

    fun CheckAppFirstExecute():Boolean {
        val pref = getSharedPreferences("IsFirst", Activity.MODE_PRIVATE)
        val isFirst = pref.getBoolean("isFirst", false)
        if (!isFirst)
        { //?????? ????????? true ??????
            val editor = pref.edit()
            editor.putBoolean("isFirst", true)
            editor.apply()
        }
        return !isFirst
    }

    private fun SetHeader() {
        val toolbar = findViewById<Toolbar>(R.id.main_toolbar)
        setSupportActionBar(toolbar)
        supportActionBar?.title = null
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.main_menu, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        when(item.itemId) {
            R.id.favorite -> {
                val intent = Intent(this, MainPhotoView::class.java)
                intent.putExtra("favorite", "favorite")
                startActivityForResult(intent, 300)
            }
            R.id.location_type -> {
                val selectitem = arrayOf<String>("????????? ??????", "????????? ??????")
                var select = location_type
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this)

                dlg.setTitle("????????? ?????? ??????")
                dlg.setSingleChoiceItems(selectitem, location_type) { dialog, i ->
                    when (i) {
                        0 -> select = 0
                        1 -> select = 1
                    }
                }
                dlg.setIcon(R.drawable.ic_tag)
                dlg.setPositiveButton("??????") { _, _ ->
                    Toast.makeText(this, "?????? ???????????????.", Toast.LENGTH_SHORT).show()
                    if (location_type != select) {
                        location_type = select
                    }
                }
                dlg.setNeutralButton("??????") { _, _ -> }
                dlg.show()
            }
            R.id.folder_type -> {
                val selectitem = arrayOf<String>("2?????? ??????", "3?????? ??????", "4?????? ??????")
                var select = folder_type
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this)
                dlg.setTitle("?????? ?????? ??????")
                dlg.setSingleChoiceItems(selectitem, folder_type - 2) { dialog, i ->
                    when (i) {
                        0 -> select = 2
                        1 -> select = 3
                        2 -> select = 4
                    }
                }
                dlg.setIcon(R.drawable.ic_folder)
                dlg.setPositiveButton("??????") { _, _ ->
                    Toast.makeText(this, "?????? ???????????????.", Toast.LENGTH_SHORT).show()
                    if (folder_type != select) {
                        folder_type = select
                        for (fragment: Fragment in supportFragmentManager.fragments) {
                            if (fragment.isVisible) {
                                val tag = fragment.tag
                                lateinit var frag: Fragment
                                val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
                                supportFragmentManager.popBackStackImmediate(tag, FragmentManager.POP_BACK_STACK_INCLUSIVE)
                                when (tag) {
                                    "name" -> {
                                        frag = NameFragment(appbar)
                                    }
                                    "tag" -> {
                                        frag = TagFragment(appbar)
                                    }
                                    "location" -> {
                                        frag = LocationFragment(appbar)
                                    }
                                }
                                transaction.replace(R.id.frame_layout, frag, tag)
                                transaction.addToBackStack(tag)
                                transaction.commit()
                                transaction.isAddToBackStackAllowed
                                break
                            }
                        }

                    }
                }
                dlg.setNeutralButton("??????") { _, _ -> }
                dlg.show()
            }
            R.id.photo_type -> {
                val selectitem = arrayOf<String>("2?????? ??????", "3?????? ??????", "4?????? ??????", "5?????? ??????", "6?????? ??????")
                var select = photo_type
                val dlg: AlertDialog.Builder = AlertDialog.Builder(this)
                dlg.setTitle("?????? ?????? ??????")
                dlg.setSingleChoiceItems(selectitem, photo_type - 2) { dialog, i ->
                    when (i) {
                        0 -> select = 2
                        1 -> select = 3
                        2 -> select = 4
                        3 -> select = 5
                        4 -> select = 6
                    }
                }
                dlg.setIcon(R.drawable.ic_image)
                dlg.setPositiveButton("??????") { _, _ ->
                    Toast.makeText(this, "?????? ???????????????.", Toast.LENGTH_SHORT).show()
                    if (photo_type != select) {
                        photo_type = select
                    }
                }
                dlg.setNeutralButton("??????") { _, _ -> }
                dlg.show()
            }
        }

        return super.onOptionsItemSelected(item)
    }

    override fun onNavigationItemSelected(item: MenuItem): Boolean {
        val fm = supportFragmentManager
        val transaction: FragmentTransaction = fm.beginTransaction()

        when(item.itemId){
            R.id.menu_name -> {
                fm.popBackStackImmediate("name", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentA = NameFragment(appbar)
                transaction.replace(R.id.frame_layout, fragmentA, "name")
                transaction.addToBackStack("name")
            }
            R.id.menu_tag -> {
                fm.popBackStackImmediate("tag", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentB = TagFragment(appbar)
                transaction.replace(R.id.frame_layout, fragmentB, "tag")
                transaction.addToBackStack("tag")
            }
            R.id.menu_cal -> {
                fm.popBackStackImmediate("cal", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentC = DateFragment(appbar)
                transaction.replace(R.id.frame_layout, fragmentC, "cal")
                transaction.addToBackStack("cal")
            }
            R.id.menu_location -> {
                fm.popBackStackImmediate("location", FragmentManager.POP_BACK_STACK_INCLUSIVE)
                val fragmentD = LocationFragment(appbar)
                transaction.replace(R.id.frame_layout, fragmentD, "location")
                transaction.addToBackStack("location")
            }
        }
        //transaction.setTransition(FragmentTransaction.TRANSIT_FRAGMENT_FADE)
        transaction.commit()
        transaction.isAddToBackStackAllowed

        return true
    }

    override fun onBackPressed() {
        if(supportFragmentManager.backStackEntryCount == 1 && supportFragmentManager.findFragmentByTag("name")!!.isVisible) {
            val tempTime = System.currentTimeMillis()
            val intervalTime = tempTime - backPressedTime
            if (!(0 > intervalTime || FINISH_INTERVAL_TIME < intervalTime)) {
                finishAffinity()
                System.runFinalization()
                System.exit(0)
            } else {
                backPressedTime = tempTime
                Toast.makeText(this, "'??????' ????????? ??? ??? ??? ????????? ???????????????.", Toast.LENGTH_SHORT).show()
                return
            }
        }
        super.onBackPressed()
        val bnv = findViewById<View>(R.id.bottomNavigationView) as BottomNavigationView
        updateBottomMenu(bnv)
    }

    private fun updateBottomMenu(navigation: BottomNavigationView) {
        val tag1: Fragment? = supportFragmentManager.findFragmentByTag("name")
        val tag2: Fragment? = supportFragmentManager.findFragmentByTag("tag")
        val tag3: Fragment? = supportFragmentManager.findFragmentByTag("cal")
        val tag4: Fragment? = supportFragmentManager.findFragmentByTag("location")

        if (tag1 != null && tag1.isVisible) {navigation.menu.findItem(R.id.menu_name).isChecked =true}
        if (tag2 != null && tag2.isVisible) {navigation.menu.findItem(R.id.menu_tag).isChecked =true}
        if (tag3 != null && tag3.isVisible) {navigation.menu.findItem(R.id.menu_cal).isChecked =true}
        if (tag4 != null && tag4.isVisible) {navigation.menu.findItem(R.id.menu_location).isChecked =true}

    }

    fun init(): Boolean{
        if(!init) {
            val transaction: FragmentTransaction = supportFragmentManager.beginTransaction()
            val fragmentA = NameFragment(appbar)
            transaction.add(R.id.frame_layout, fragmentA, "name")
            transaction.addToBackStack("name")
            transaction.commit()
            transaction.isAddToBackStackAllowed
            init = true
        }

        if (CheckAppFirstExecute() == true) {
            val dlg: AlertDialog.Builder = AlertDialog.Builder(this)
            dlg.setTitle("???????????????") //??????
            dlg.setMessage("Check BOx ??? ???????????????????\n??????, ?????? ????????? ?????? ???????????? ???????????????.\n?????? ??????, ?????? ??? ?????? ???????????? ???????????? ???????????? ?????? ??? ????????????.") // ?????????
            dlg.setCancelable(false)
            dlg.setPositiveButton("??????", DialogInterface.OnClickListener { dialog, which ->
            })
            dlg.show()
        }
        return true
    }

    private fun captureCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        if (takePictureIntent.resolveActivity(packageManager) != null ) {
            try {
                val photoFile = createImageFile()
                if (photoFile != null) {
                    val providerURI = FileProvider.getUriForFile(this, packageName, photoFile)
                    takePictureIntent.putExtra(MediaStore.EXTRA_OUTPUT, providerURI)
                    startActivityForResult(takePictureIntent, REQUEST_TAKE_PHOTO)
                }
            } catch (ex: IOException) {
                Log.e("captureCamera Error", ex.toString())
                return
            }
        }
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        when (requestCode) {
            REQUEST_TAKE_PHOTO -> {
                Log.i("REQUEST_TAKE_PHOTO", "${Activity.RESULT_OK}" + " " + "${resultCode}")
                if (resultCode == RESULT_OK) {
                    try {
                        galleryAddPic()
                    } catch (e: Exception) {
                        Log.e("REQUEST_TAKE_PHOTO", e.toString())
                    }
                } else {
                    Toast.makeText(this@MainActivity, "??????????????? ??????????????????.", Toast.LENGTH_SHORT).show()
                }
            }
        }
    }

    @Throws(IOException::class) //IOException ???????????? ???????????? ????????????
    fun createImageFile(): File? {
        val timeStamp : String = SimpleDateFormat("yyyyMMdd_HHmmss", Locale.getDefault()).format(Date())
        val imageFileName = "JPEG_$timeStamp.jpg"
        val storageDir = File(
                Environment.getExternalStorageDirectory().toString() + "/Pictures", "Cbox"
        )
        if (!storageDir.exists()) {
            Log.i("mCurrentPhotoPath1", storageDir.toString())
            storageDir.mkdirs() //File.mkdir = ???????????? ?????? ??????????????? ?????? ??????????????? ???????????? ?????? ??????, ?????? ?????? | File.mkdirs = ???????????? ?????? ??????????????? ?????? ??????????????? ???????????? ?????? ??????, ?????? ?????????????????? ??????
        }
        val imageFile = File(storageDir, imageFileName)
        mCurrentPhotoPath = imageFile.absolutePath
        return imageFile
    }

    private fun galleryAddPic() {
        Log.i("galleryAddPic", "Call")
        val mediaScanIntent = Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE) // ?????? ????????? ??????????????? ?????? ?????? ????????? ????????? ?????????
        val f = File(mCurrentPhotoPath)
        val contentUri : Uri = Uri.fromFile(f)
        mediaScanIntent.data = contentUri
        sendBroadcast(mediaScanIntent)
        Toast.makeText(this, "????????? ????????? ?????????????????????.", Toast.LENGTH_SHORT).show()
    }


    fun CheckChangeData() {
        ChangeCheckThread = ThreadPoolExecutor(1, 3, 0L, TimeUnit.MILLISECONDS, LinkedBlockingQueue())
        while (!NetworkIsValid(this)) { }
        ChangeCheckThread.execute {
            CheckAddedPhoto()
            CheckDeletedPhoto()
        }
    }

    private fun CheckAddedPhoto() {
        val pref = getSharedPreferences("pref", Context.MODE_PRIVATE)
        val editor = pref.edit()
        var lastAddedDate = pref.getLong("lastAddedDate", Long.MIN_VALUE)
        val cursor = vm.getNewlySortedCursor(this, 0)

        if (MediaStore_Dao.cursorIsValid(cursor)) {
            do {
                Log.d("????????????: ", lastAddedDate.toString())

                val id = cursor!!.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns._ID))
                // ???????????? ?????? ??? ??????

                while (!NetworkIsValid(this)) { }
                ChangeCheckThread.execute {
                    vm.getFullLocation(this, id)
                    vm.getFavorite(id)
                    AddTagsByApi(this, id) // ??????
                }

                lastAddedDate = cursor.getLong(cursor.getColumnIndex(MediaStore.Images.ImageColumns.DATE_ADDED))
                editor.putLong("lastAddedDate", lastAddedDate)
                editor.apply()
            } while (cursor!!.moveToNext())
            cursor.close()
        }
    }

    private fun CheckDeletedPhoto() {
        val idCursor = vm.getIdCursor()
        if (MediaStore_Dao.cursorIsValid(idCursor)) {
            do {
                vm.CheckIdCursorValid(this, idCursor!!)

            } while (idCursor!!.moveToNext())
            idCursor.close()
        }
    }

    @Suppress("DEPRECATION")    // 'DEPRECATION'??? ?????? ?????? ??????
    private fun NetworkIsValid(context: Context) : Boolean {
        var result = false
        val cm = getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            val networkCapabilities = cm.activeNetwork ?: return false
            val actNw = cm.getNetworkCapabilities(networkCapabilities) ?: return false
            result = when {
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_WIFI) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_CELLULAR) -> true
                actNw.hasTransport(NetworkCapabilities.TRANSPORT_ETHERNET) -> true
                else -> false
            }
        } else {
            cm.run {
                cm.activeNetworkInfo?.run {
                    result = when(type) {
                        ConnectivityManager.TYPE_WIFI -> true
                        ConnectivityManager.TYPE_MOBILE -> true
                        ConnectivityManager.TYPE_ETHERNET -> true
                        else -> false
                    }
                }
            }
        }
        return result
    }

    private fun AddTagsByApi(context: Context, id: Long) {
        val options = FirebaseTranslatorOptions.Builder()
                .setSourceLanguage(FirebaseTranslateLanguage.EN)
                .setTargetLanguage(FirebaseTranslateLanguage.KO)
                .build()
        val translator = FirebaseNaturalLanguage.getInstance().getTranslator(options)

        val bitmap = MediaStore_Dao.LoadThumbnailById(context, id) ?: return
        val image = FirebaseVisionImage.fromBitmap(bitmap)
        val labeler = FirebaseVision.getInstance().onDeviceImageLabeler
        labeler.processImage(image)
                .addOnSuccessListener { labels ->
                    translator.downloadModelIfNeeded()
                            .addOnSuccessListener {
                                for (label in labels) {
                                    translator.translate(label.text)
                                            .addOnSuccessListener { translatedText ->
                                                if (label.confidence >= 0.88) {
                                                    DBThread.execute {
                                                        vm.Insert(TagData(id, translatedText))
                                                    }
                                                }
                                            }
                                            .addOnFailureListener { e -> e.stackTrace}
                                }
                            }
                            .addOnFailureListener { e -> e.stackTrace}
                }
                .addOnFailureListener { e -> e.stackTrace}
    }
}