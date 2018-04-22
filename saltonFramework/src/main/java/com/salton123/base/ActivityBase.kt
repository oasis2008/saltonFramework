package com.salton123.base

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.support.v7.app.AppCompatActivity
import android.view.LayoutInflater
import android.view.View
import android.widget.Toast
import com.salton123.util.LogUtils
import com.salton123.util.ViewUtils

/**
 * User: 巫金生(newSalton@outlook.com)
 * Date: 2017/6/14 19:18
 * Time: 19:18
 * Description:
 */
abstract class ActivityBase : AppCompatActivity(), IComponentLife {

    override fun onCreate(savedInstanceState: Bundle?) {
        initVariable(savedInstanceState)
        super.onCreate(savedInstanceState)
        setContentView(getRootView())
        initViewAndData()
        initListener()
    }

    override fun initListener() {

    }

    override fun context(): Context {
        return this
    }

    override fun log(msg: String) {
        LogUtils.d(msg)
    }

    override fun <VT : View> f(id: Int): VT {
        return ViewUtils.f(getRootView(), id)
    }

    override fun getRootView(): View {
        return inflater().inflate(getLayout(), null)
    }

    override fun longToast(toast: String) {
        Toast.makeText(applicationContext, toast, Toast.LENGTH_LONG).show()
    }

    override fun shortToast(toast: String) {
        Toast.makeText(applicationContext, toast, Toast.LENGTH_SHORT).show()
    }


    override fun inflater(): LayoutInflater {
        return LayoutInflater.from(this)
    }

    override fun openActivity(clz: Class<*>, bundle: Bundle?) {
        startActivity(Intent(this, clz).apply { bundle?.let { this@apply.putExtras(it) } })
    }

    override fun openActivityForResult(clz: Class<*>, bundle: Bundle?, requestCode: Int) {
        startActivityForResult(Intent(this, clz).apply { bundle?.let { this@apply.putExtras(it) } }, requestCode)
    }
}