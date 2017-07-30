package com.example.minh98.diary.register.presenter

import com.example.minh98.diary.register.model.modelRegister
import com.example.minh98.diary.register.view.RegisterActivity
import com.example.minh98.diary.register.view.viewRegister

/**
 * Created by minh98 on 30/07/2017.
 */
class presenterRegister(view: viewRegister, context: RegisterActivity) : IpresenterRegister {
    
    
    val view: viewRegister = view
    val model: modelRegister by lazy { modelRegister(this, context) }
    fun actionRegister(name: String, password: String, repassword: String) {
        when {
            name.isEmpty() -> return view.etNameEmpty()
            password.isEmpty() -> return view.etPassrordEmpty()
            repassword.isEmpty() -> return view.etRePasswordEmpty()
        }
        if (password != repassword) {
            return view.etRePasswordWrong()
        } else {
            //du lieu hop le
            model.actionRegister(name, password)
        }
    }
    
    fun onBackPressed() {
        view.noticeExit()
    }
    
    override fun registerAccountSuccess(username: String, userpass: String) {
        view.registerAccountSuccess(username, userpass)
    }
    
    override fun registerAccountFail() {
        view.registerAccountFail()
    }
    
    override fun registerAccountExists() {
        view.registerAccountExists()
    }
    
}