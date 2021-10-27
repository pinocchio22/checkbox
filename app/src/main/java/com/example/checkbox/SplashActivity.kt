package com.example.checkbox

import android.R
import android.Manifest
import android.app.ProgressDialog
import android.content.DialogInterface
import android.content.Intent
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import android.os.Handler
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.firebase.ml.common.modeldownload.FirebaseModelDownloadConditions
import com.google.firebase.ml.common.modeldownload.FirebaseModelManager
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateLanguage
import com.google.firebase.ml.naturallanguage.translate.FirebaseTranslateRemoteModel
import kotlin.system.exitProcess

/**
 * @author CHOI
 * @email vviian.2@gmail.com
 * @created 2021-10-27
 * @desc
 */
class SplashActivity : AppCompatActivity() {

    private var progressDialog : ProgressDialog? = null

    @RequiresApi(Build.VERSION_CODES.LOLLIPOP_MR1)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        checkPermission()

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.WRITE_EXTERNAL_STORAGE) == PackageManager.PERMISSION_GRANTED) {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                if (ContextCompat.checkSelfPermission(this, Manifest.permission.ACCESS_MEDIA_LOCATION) == PackageManager.PERMISSION_GRANTED) {
                    translation_api()
                }
            }
            else
                translation_api()
        }
    }

    fun translation_api() {
        val intent = Intent(this, MainActivity::class.java)
        val dlg : AlertDialog.Builder = AlertDialog.Builder(this, R.style.Theme_Material_Light_Dialog_NoActionBar)
        val modelManager = FirebaseModelManager.getInstance()
        val Model = FirebaseTranslateRemoteModel.Builder(FirebaseTranslateLanguage.KO).build()
        val conditions = FirebaseModelDownloadConditions.Builder().requireWifi().build()

        FirebaseModelManager.getInstance().isModelDownloaded(Model).addOnSuccessListener { isDownLoad ->
            if (isDownLoad) {
                startActivity(intent)
                finish()
            } else {
                dlg.setTitle("환영합니다")   // 제목
                dlg.setMessage("추가 파일 서치가 필ㅇ합니다. 와이파이를 연결해주세여. \n\n 다운로드 하시겠습니까? (30mb)")   // 메시지
                dlg.setCancelable(false)
                dlg.setPositiveButton("확인", DialogInterface.OnClickListener{ dialog, which ->
                    loading()
                    modelManager.download(Model, conditions).addOnSuccessListener { modelManager.getDownloadedModels(
                            FirebaseTranslateRemoteModel::class.java).addOnSuccessListener { models ->
                        Toast.makeText(this, "설치가 완료 되었습니다.", Toast.LENGTH_SHORT).show()
                        finish()
                        loadingEnd()
                    }.addOnFailureListener{}
                    }.addOnFailureListener{}
                })
                dlg.setNegativeButton("취소") { _, _ ->
                    exitProcess(0)
                }
                dlg.show()
            }
        }
    }

    fun loading() {
        Handler().postDelayed(
                {
                    progressDialog = ProgressDialog(this)
                    progressDialog!!.setIndeterminate(true)
                    progressDialog!!.setCancelable(false)
                    progressDialog!!.setMessage("필요한 파일을 다운로드 중입니다. \n잠시만 기다려 주세요.")
                    progressDialog!!.show()
                }, 0
        )
    }




}