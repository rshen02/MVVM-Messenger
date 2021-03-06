package com.nexus.mvvmmessenger.ui.main

import android.app.AlertDialog
import android.app.Dialog
import android.content.DialogInterface
import android.util.Log
import androidx.lifecycle.Observer
import com.nexus.mvvmmessenger.R
import com.nexus.mvvmmessenger.core.BaseFragment
import com.nexus.mvvmmessenger.databinding.FragmentMainBinding
import com.nexus.mvvmmessenger.model.MessageModel
import java.util.*
import kotlin.concurrent.schedule

class MainFragment : BaseFragment<FragmentMainBinding, MainViewModel>() {


    val TIMER = "End Of Mins Timer"
    var mTimerDialog: Dialog? = null
    var mTimerTask: TimerTask? = null

    override val viewModelClass: Class<MainViewModel>
        get() = MainViewModel::class.java
    override val layout: Int
        get() = R.layout.fragment_main

    override fun onInit() {

        setupUI()


        mViewModel!!.getMessages()

    }

    override fun subscribeObserver() {
        super.subscribeObserver()

        mViewModel!!.getMessagesEvent.observe(this, Observer { messages ->
            val adapter = mBinding!!.messageList.adapter as MessageAdapter
            adapter.messages = messages

            if (adapter.itemCount > 0) {
                mBinding!!.messageList.post {
                    mBinding!!.messageList.smoothScrollToPosition(adapter.itemCount - 1)
                }
            }

        })

        mViewModel!!.onAddMessageEvent.observe(this, Observer { aBoolean ->

//            mBinding!!.fabSent.isEnabled = true
//            mBinding!!.etNewMessage.isEnabled = true

            if (aBoolean) {
                mBinding!!.etNewMessage.text?.clear()
            }
        })
    }

    override fun onResume() {
        super.onResume()
        runTimer()
    }

    override fun onPause() {
        super.onPause()
        stopTimer()
    }

    private fun setupUI() {
        mBinding!!.isSending = mViewModel!!.isSending

        mBinding!!.messageList.adapter = MessageAdapter(object : MessageAdapter.OnCallback {
            override fun onLongClick(messageModel: MessageModel) {
                Log.d("TAG", "setOnLongClickListener")
                showConfirmDeleteDialog(messageModel)
            }
        })

        mBinding!!.fabSent.setOnClickListener {
            val value = mBinding!!.etNewMessage.text
            if (!value.isNullOrEmpty()) {
                mViewModel!!.addMessage(value.toString())

                // after submit
//                mBinding!!.fabSent.isEnabled = false
//                mBinding!!.etNewMessage.isEnabled = false
                hideSoftKeyBoard(context!!, mBinding!!.root)
            }
        }
    }

    private fun runTimer() {
        val calendar = Calendar.getInstance()
        calendar.set(Calendar.MINUTE, calendar.get(Calendar.MINUTE) + 1)
        calendar.set(Calendar.SECOND, 0)

        this.mTimerTask = Timer(TIMER, false).schedule(
            calendar.time
        ) {
            activity?.runOnUiThread {
                showDialog()
            }
        }
    }

    private fun stopTimer() {
        this.mTimerTask?.cancel()
    }


    private fun showDialog() {
        if (this.mTimerDialog != null) {
            return
        }
        val builder = AlertDialog.Builder(context)
            .setMessage(R.string.timer_desc)
            .setCancelable(false)
            .setPositiveButton(R.string.title_ok) { _: DialogInterface, _: Int ->
                runTimer()
                this.mTimerDialog?.apply {
                    dismiss()
                    null
                }
            }
        this.mTimerDialog = builder.show()
    }

    private fun showConfirmDeleteDialog(messageModel: MessageModel) {
        AlertDialog.Builder(context)
            .setMessage(R.string.desc_dialog_delete)
            .setCancelable(false)
            .setPositiveButton(R.string.title_delete) { _: DialogInterface, _: Int ->
                mViewModel!!.deleteMessage(messageModel)
            }
            .setNegativeButton(R.string.title_cancel) { dialog: DialogInterface, _: Int ->
                dialog.dismiss()
            }
            .show()
    }
}