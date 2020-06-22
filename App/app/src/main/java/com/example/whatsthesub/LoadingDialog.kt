package com.example.whatsthesub

import android.app.ActionBar
import android.app.Activity
import android.app.AlertDialog
import android.graphics.Color
import android.graphics.Rect
import android.graphics.drawable.ColorDrawable
import android.view.LayoutInflater
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.LinearLayout
import androidx.appcompat.app.AppCompatActivity

class LoadingDialog (myActivity : Activity) {

    private val activity  = myActivity
    private lateinit var dialog  : AlertDialog

    fun showDialog()
    {
        //Inflate dialog with custom view
        val dialogView = LayoutInflater.from(activity).inflate(R.layout.custom_dialog, null)


        //AlertDialog builder
        dialog = AlertDialog.Builder(activity, R.style.Theme_Dialog)
            .setView(dialogView)
            .setCancelable(false)
            .create()

        dialog.show()

        dialog.window!!.setLayout(WindowManager.LayoutParams.WRAP_CONTENT, WindowManager.LayoutParams.WRAP_CONTENT)
    }

    fun dismissDialog()
    {
     dialog.dismiss()
    }
}