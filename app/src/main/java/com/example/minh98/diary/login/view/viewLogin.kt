package com.example.minh98.diary.login.view

/**
 * Created by minh98 on 30/07/2017.
 */
interface viewLogin {
    fun initConnectFail()
    fun showAskContinueLogin()
    fun noticeExit()
    fun startRegister()
    fun startMain(username: String, userpass: String)
    fun isCheckedCb(): Boolean
    fun etUserPassEmpty()
    fun etUserNameEmpty()
    fun setLoginConditions(loginCondition: Boolean)
    fun loginSuccess()
    fun loginFail()
}