package com.example.minh98.diary.register.view

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
import com.example.minh98.diary.R
import com.example.minh98.diary.enums.ToastMode
import com.example.minh98.diary.main.view.MainActivity
import com.example.minh98.diary.register.presenter.presenterRegister
import es.dmoral.toasty.Toasty

class RegisterActivity : AppCompatActivity(), viewRegister {
    
    lateinit var etUserName: EditText
    lateinit var etUserPass: EditText
    lateinit var etReUserPass: EditText
    lateinit var btnRegister: Button
    val presenter: presenterRegister by lazy { presenterRegister(this, this) }
    
    val register_account_fail: String by lazy { getString(R.string.register_account_fail) }
    val register_account_success: String by lazy { getString(R.string.register_account_success) }
    val accout_exists: String by lazy { getString(R.string.account_exists) }
    val ask_exit_app: String by lazy { getString(R.string.ask_exit_app) }
    val nameEmpty: String by lazy { getString(R.string.username_empty) }
    val passEmpty: String by lazy { getString(R.string.pass_empty) }
    val repassEmpty: String by lazy { getString(R.string.repass_empty) }
    val repassWrong: String by lazy { getString(R.string.repass_wrong) }
    
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
        etUserName = findViewById(R.id.editUserName) as EditText
        etUserPass = findViewById(R.id.editUserPass) as EditText
        etReUserPass = findViewById(R.id.editReUserPass) as EditText
        btnRegister = findViewById(R.id.btnRegister) as Button
        btnRegister.setOnClickListener {
            presenter.actionRegister(etUserName.text.toString(), etUserPass.text.toString(), etReUserPass.text.toString())
        }
    }
    
    
    override fun onBackPressed() {
        presenter.onBackPressed()
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
    
    override fun etNameEmpty() {
        etUserName.error = nameEmpty
    }
    
    override fun etPassrordEmpty() {
        etUserPass.error = passEmpty
    }
    
    override fun etRePasswordEmpty() {
        etReUserPass.error = repassEmpty
    }
    
    override fun etRePasswordWrong() {
        etReUserPass.error = repassWrong
    }
    
    override fun noticeExit() {
        val alert = AlertDialog.Builder(this)
        with(alert) {
            setTitle("thong bao")
            setMessage(ask_exit_app)
            setPositiveButton("thoat", { _, _ -> super.onBackPressed() })
            setNegativeButton("khong", { dialog, _ -> dialog.dismiss() })
            create().show()
            
        }
    }
    
    override fun registerAccountSuccess(username: String, userpass: String) {
        toast(register_account_success, ToastMode.SUCCESS)
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra("username", username)
            putExtra("userpass", userpass)
        })
        finish()
    }
    
    override fun registerAccountFail() {
        toast(register_account_fail, ToastMode.ERROR)
    }
    
    override fun registerAccountExists() {
        toast(accout_exists, ToastMode.ERROR)
    }
}
 
