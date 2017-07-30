package com.example.minh98.diary.login.presenter

/**
 * Created by minh98 on 30/07/2017.
 */
interface IpresenterLogin {
    fun setLoginConditions(loginCondition: Boolean)
    fun startMain(name: String, pass: String)
    fun loginFail()
    fun loginSuccess()
    
}