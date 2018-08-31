package com.salton123.base

import android.content.Context
import android.os.Bundle
import android.support.v4.app.DialogFragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup


/**
 * User: newSalton@outlook.com
 * Date: 2018/5/5 下午12:00
 * ModifyTime: 下午12:00
 * Description:
 * set your style onCreate setStyle(DialogFragment.STYLE_NO_FRAME, R.style.FullScreenDialog)
 *
 *
 *
 */
abstract class BaseSupportPopupFragment : DialogFragment(), IComponentLife {
    private lateinit var mDelegate: FragmentDelegate

    override fun onCreate(savedInstanceState: Bundle?) {
        mDelegate = FragmentDelegate(this)
        mDelegate.onCreate(savedInstanceState)
        super.onCreate(savedInstanceState)
    }

    override fun onCreateView(inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?): View? {
        return mDelegate.onCreateView()
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        mDelegate.onViewCreated()
    }

    override fun onDestroy() {
        mDelegate.onDestroy()
        super.onDestroy()
    }

    override fun initListener() {

    }

    override fun context(): Context {
        return this.context
    }

    override fun log(msg: String) {
        mDelegate.log(msg)
    }


    override fun longToast(toast: String) {
        mDelegate.longToast(toast)
    }

    override fun shortToast(toast: String) {
        mDelegate.shortToast(toast)
    }

    override fun <VT : View> f(id: Int): VT {
        return mDelegate.f(id)
    }

    override fun getRootView(): View {
        return mDelegate.getRootView()
    }

    override fun inflater(): LayoutInflater {
        return mDelegate.inflater()
    }

    override fun openActivity(clz: Class<*>, bundle: Bundle?) {
        mDelegate.openActivity(clz, bundle)
    }

    override fun openActivityForResult(clz: Class<*>, bundle: Bundle?, requestCode: Int) {
        mDelegate.openActivityForResult(clz, bundle, requestCode)
    }

}