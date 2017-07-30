package com.example.minh98.diary.login.view

import android.content.Intent
import android.content.pm.ActivityInfo
import android.os.Build
import android.os.Bundle
import android.support.v7.app.AlertDialog
import android.support.v7.app.AppCompatActivity
import android.util.Log
import android.view.View
import android.widget.*

import com.example.minh98.diary.R
import com.example.minh98.diary.enums.ToastMode
import com.example.minh98.diary.login.enums.str
import com.example.minh98.diary.login.presenter.presenterLogin
import com.example.minh98.diary.main.view.MainActivity
import com.example.minh98.diary.register.view.RegisterActivity
import es.dmoral.toasty.Toasty

class LoginActivity : AppCompatActivity(), viewLogin {
    
    
    lateinit var etUserName: EditText
    lateinit var etUserPass: EditText
    lateinit var btnLogin: Button
    lateinit var cbLoginOnline: CheckBox
    lateinit var txtRegister: TextView
    var username: String = ""
    var userpass: String = ""
    var loginCondition: Boolean = false
    val presenter: presenterLogin by lazy { presenterLogin(this, this) }
    
    
    fun initView() {
        etUserName = findViewById(R.id.editUserName) as EditText
        etUserPass = findViewById(R.id.editUserPass) as EditText
        btnLogin = findViewById(R.id.btnLogin) as Button
        cbLoginOnline = findViewById(R.id.cb_login_online) as CheckBox
        txtRegister = findViewById(R.id.txtRegister) as TextView
        presenter.initConnected()
        btnLogin.setOnClickListener {
            username = etUserName.text.toString().replace("'", "").trim()
            userpass = etUserPass.text.toString().replace("'", "").trim()
            presenter.actionLogin(username, userpass)
        }
        txtRegister.setOnClickListener {
            presenter.startRegister()
        }
    }
    
    override fun loginSuccess() {
        toast(presenter.getString(str.login_success), ToastMode.SUCCESS)
    }
    
    override fun loginFail() {
        toast(presenter.getString(str.login_fail), ToastMode.ERROR)
    }
    
    override fun startMain(username: String, userpass: String) {
        startActivity(Intent(this, MainActivity::class.java).apply {
            putExtra("username", username)
            putExtra("userpass", userpass)
        })
        finish()
    }
    
    override fun isCheckedCb(): Boolean {
        return cbLoginOnline.isChecked
    }
    
    override fun etUserPassEmpty() {
        etUserPass.error = presenter.getString(str.pass_empty)
    }
    
    override fun etUserNameEmpty() {
        etUserName.error = presenter.getString(str.name_empty)
    }
    
    override fun setLoginConditions(loginCondition: Boolean) {
        this.loginCondition = loginCondition
    }
    
    override fun startRegister() {
        startActivity(Intent(this, RegisterActivity::class.java))
        finish()
    }
    
    override fun initConnectFail() {
        cbLoginOnline.isEnabled = false
        cbLoginOnline.text = presenter.getString(str.login_offline)
    }
    
    override fun showAskContinueLogin() {
        val alert: AlertDialog.Builder = AlertDialog.Builder(this)
        
        val v: View = layoutInflater.inflate(R.layout.layout_dialog_login, null)
        val txtTitle = v.findViewById(R.id.txtTitle) as TextView
        val editUserPass = v.findViewById(R.id.editUserPass) as EditText
        val btnChangeAccount = v.findViewById(R.id.btnChangeAccount) as Button
        val btnOk = v.findViewById(R.id.btnOk) as Button
        
        txtTitle.text = "${presenter.getString(str.continue_use_account)}: $username"
        
        val alertdialog: AlertDialog
        //set properties
        alert.setView(v)
        alert.setCancelable(false)
        
        alertdialog = alert.create()
        
        btnChangeAccount.setOnClickListener {
            alertdialog?.dismiss()
            presenter.setUsername("")//reset username
        }
        
        btnOk.setOnClickListener {
            //tien hanh dang nhap offline , truy xuat sqlite
            val userpass = editUserPass.text.toString().replace("'", "").trim()
            if (userpass.isEmpty()) {
                editUserPass.error = presenter.getString(str.pass_empty)
                return@setOnClickListener
            } else {
                if (presenter.login(username, userpass)) {
                    toast(presenter.getString(str.pass_empty), ToastMode.SUCCESS)
                    
                    alertdialog.dismiss()
                    val it = Intent(this@LoginActivity, MainActivity::class.java)
                    //truyen user,pass cho mainactivity va thoat activity hien tai
                    it.putExtra("username", username)
                    it.putExtra("userpass", userpass)
                    startActivity(it)
                    finish()
                } else {
                    editUserPass.error = presenter.getString(str.pass_wrong)
                    toast(presenter.getString(str.login_fail), ToastMode.ERROR)
                    
                }
            }
        }
        alertdialog.show()
    }
    
    override fun noticeExit() {
        val alert = AlertDialog.Builder(this)
        with(alert) {
            setTitle("Thông báo")
            setMessage(presenter.getString(str.ask_exit_app))
            setPositiveButton("Thoát", { _, _ -> super.onBackPressed() })
            setNegativeButton("Cancel", { dialog, _ -> dialog.dismiss() })
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
    
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_login)
        
        requestedOrientation = ActivityInfo.SCREEN_ORIENTATION_PORTRAIT
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
            val window = window
            window.statusBarColor = resources.getColor(R.color.colorPrimaryDark)
        }
        
        
        initView()
        presenter.checkAccount()
    }
    
    override fun onBackPressed() {
        presenter.onBackPressed()
    }
}
