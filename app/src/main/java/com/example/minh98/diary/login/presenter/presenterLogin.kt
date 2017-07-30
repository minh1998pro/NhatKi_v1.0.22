package com.example.minh98.diary.login.presenter

import android.content.Context
import android.util.Log
import com.example.minh98.diary.login.enums.str
import com.example.minh98.diary.login.model.modelLogin
import com.example.minh98.diary.login.view.viewLogin

/**
 * Created by minh98 on 30/07/2017.
 */
class presenterLogin(view: viewLogin, context: Context) : IpresenterLogin {
    val view: viewLogin = view
    val model: modelLogin by lazy { modelLogin(this, context) }
    
    fun initConnected() {
        if (model.isConnected()) {
            view.initConnectFail()
        }
    }
    
    fun actionLogin(username: String, userpass: String) {
        when {
            username.isEmpty() -> {
                return view.etUserNameEmpty()
            }
            userpass.isEmpty() -> {
                return view.etUserPassEmpty()
            }
        }
        
        if (view.isCheckedCb()) {
            model.getTKFromServer(username, userpass) //tien hanh dang nhap online
        } else {
            //dang nhap offline, truy xuat sqlite
            val loginCondition = login(username, userpass)
            view.setLoginConditions(loginCondition)
            if (loginCondition) {
                
                //save tai khoan vao sharepreference de lan dang nhap sau co the login tiep
                if (!model.set("username", username)) {
                    //toast(cant_save_sharepreference, ToastMode.WARNING)
                    Log.e("luu tai khoan", model.getString(str.cant_save_sharepreference))
                } else {
                    //toast(save_info_success, ToastMode.SUCCESS)
                    Log.e("luu tai khoan", model.getString(str.save_info_success))
                }
                
                view.loginSuccess()
                //truyen 2 tham so la user,pass cho mainactivity su dung
                view.startMain(username, userpass)
            } else {
                view.loginFail()
            }
        }
        
    }
    
    fun startRegister() {
        view.startRegister()
    }
    
    fun checkAccount() {
        val username: String = model.getUsername()
        //sharePreference.get("username", "").replace("'", "").trim()
        if (username == "") {
            //chua ton tai username, khong lam gi ca
            //toast(not_exists_username, ToastMode.INFO)
            Log.e("check account", model.getString(str.not_exists_username))
        } else {
            //co ton tai username, show dialog hoi co muon dang nhap tiep khong
            //toast(exists_username, ToastMode.INFO)
            Log.e("check account", model.getString(str.not_exists_username))
            view.showAskContinueLogin()
        }
        
    }
    
    fun onBackPressed() {
        view.noticeExit()
    }
    
    fun setUsername(s: String) {
        model.setUsername(s)
    }
    
    fun login(username: String, userpass: String): Boolean {
        return model.login(username, userpass)
    }
    
    fun getString(enums: str): String {
        return model.getString(enums)
    }
    
    override fun setLoginConditions(loginCondition: Boolean) {
        view.setLoginConditions(loginCondition)
    }
    
    override fun startMain(name: String, pass: String) {
        view.startMain(name, pass)
    }
    
    override fun loginFail() {
        view.loginFail()
    }
    
    override fun loginSuccess() {
        view.loginSuccess()
    }
}