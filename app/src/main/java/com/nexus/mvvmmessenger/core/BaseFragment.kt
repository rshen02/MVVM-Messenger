package com.nexus.mvvmmessenger.core

import android.app.AlertDialog
import android.content.Context
import android.content.DialogInterface
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import androidx.databinding.DataBindingUtil
import androidx.databinding.ViewDataBinding
import androidx.fragment.app.Fragment
import androidx.lifecycle.Observer
import com.nexus.mvvmmessenger.R
import com.nexus.mvvmmessenger.utils.ViewModelUtils

abstract class BaseFragment<V : ViewDataBinding, VM : BaseViewModel?> : Fragment() {


    protected var mBinding: V? = null
    protected var mViewModel: VM? = null

    abstract val viewModelClass: Class<VM>
    abstract val layout: Int
    abstract fun onInit()


    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        mBinding = DataBindingUtil.bind(inflater.inflate(layout, container, false))
        mViewModel = ViewModelUtils.getViewModel(activity, viewModelClass)
        // ContentView is the root view of the layout of this activity/fragment
        onInit()
        subscribeObserver()
        return mBinding!!.root
    }

    protected open fun subscribeObserver() {
        mViewModel!!.showErrorEvent.observe(viewLifecycleOwner,
            Observer { message: String? ->
                showErrorMessage(
                    context,
                    message,
                    null
                )
            }
        )
    }

    fun hideSoftKeyBoard(context: Context, view: View) {
        try {
            val imm = context.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, InputMethodManager.HIDE_NOT_ALWAYS)
        } catch (e: Exception) {
            // TODO: handle exception
            e.printStackTrace()
        }

    }

    fun showErrorMessage(
        context: Context?, messageResId: Int,
        onClickListener: DialogInterface.OnClickListener?
    ) {
        AlertDialog.Builder(context)
            .setTitle(R.string.title_error)
            .setMessage(messageResId)
            .setCancelable(false)
            .setNegativeButton(R.string.title_ok, onClickListener)
            .show()
    }

    fun showErrorMessage(
        context: Context?,
        messageString: String?,
        onClickListener: DialogInterface.OnClickListener?
    ) {
        AlertDialog.Builder(context)
            .setTitle(R.string.title_error)
            .setMessage(messageString)
            .setCancelable(false)
            .setNegativeButton(R.string.title_ok, onClickListener)
            .show()
    }

}
