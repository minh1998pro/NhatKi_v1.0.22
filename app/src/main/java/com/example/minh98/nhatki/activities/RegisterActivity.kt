package com.example.minh98.nhatki.activities

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.widget.Button
import android.widget.EditText
import android.widget.Toast
import com.example.minh98.nhatki.R
import com.example.minh98.nhatki.database.sqlite
import com.example.minh98.nhatki.enums.ToastMode
import es.dmoral.toasty.Toasty

class RegisterActivity : AppCompatActivity() {
    
    lateinit var editUserName: EditText
    lateinit var editUserPass: EditText
    lateinit var editReUserPass: EditText
    lateinit var btnRegister: Button
    val sqlite: sqlite by lazy {
        sqlite(this, "NhatKi.sqlite", null, 1)
    }
    val register_account_fail: String by lazy { getString(R.string.register_account_fail) }
    val register_account_success: String by lazy { getString(R.string.register_account_success) }
    val accout_exists: String by lazy { getString(R.string.account_exists) }
    val ask_exit_app: String by lazy { getString(R.string.ask_exit_app) }
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_register)
        
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }
        
        initView()
    }
    
    
    fun initView() {
        editUserName = findViewById(R.id.editUserName) as EditText
        editUserPass = findViewById(R.id.editUserPass) as EditText
        editReUserPass = findViewById(R.id.editReUserPass) as EditText
        btnRegister = findViewById(R.id.btnRegister) as Button
        btnRegister.setOnClickListener {
            if (!editUserName.text.isEmpty()
                    && !editUserPass.text.isEmpty()
                    && editUserPass.text.toString() == editReUserPass.text.toString()) {
                actionRegister(editUserName.text.toString().replace("'", "").trim(), editUserPass.text.toString().replace("'", "").trim())
            }
        }
    }
    
    private fun actionRegister(username: String, userpass: String) {
        //register offline,truy xuat sqlite
        if (!sqlite.check1TK(username)) {
            //neu nhu chua ton tai tai khoan trong sqlite
            if (sqlite.insertTK(username, userpass)) { //thanh cong
                
                toast(register_account_success, ToastMode.SUCCESS)
                startActivity(Intent(this, MainActivity::class.java).apply {
                    putExtra("username", username)
                    putExtra("userpass", userpass)
                })
                finish()
            } else {
                toast(register_account_fail, ToastMode.ERROR)
            }
        } else {
            toast(accout_exists, ToastMode.INFO)
        }
        
    }
    
    override fun onBackPressed() {
        val alert = AlertDialog.Builder(this)
        with(alert) {
            setTitle("thong bao")
            setMessage(ask_exit_app)
            setPositiveButton("thoat", { _, _ -> super.onBackPressed() })
            setNegativeButton("khong", { dialog, _ -> dialog.dismiss() })
            create().show()
            
        }
    }
    
    private fun toast(s: String, mode: ToastMode) {
        when (mode) {
            ToastMode.SUCCESS -> Toasty.success(this, s, Toast.LENGTH_LONG, true).show()
            ToastMode.ERROR -> Toasty.error(this, s, Toast.LENGTH_LONG, true).show()
            ToastMode.INFO -> Toasty.info(this, s, Toast.LENGTH_LONG, true).show()
            ToastMode.WARNING -> Toasty.warning(this, s, Toast.LENGTH_LONG, true).show()
        }
        Log.e("LOG:", s)
    }
    
}
