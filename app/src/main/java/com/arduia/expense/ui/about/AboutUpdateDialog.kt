package com.arduia.expense.ui.about

import android.content.Context
import androidx.appcompat.app.AlertDialog
import com.arduia.expense.R
import com.arduia.expense.databinding.FragAboutUpdateDialogBinding

class AboutUpdateDialog(ctx: Context): AlertDialog(ctx) {

    private var _binding: FragAboutUpdateDialogBinding? = null
    private val binding get() = _binding!!

    private lateinit var data: AboutUpdateUiModel

    private var onInstallClickListener: (()-> Unit )? = null

    init {
        _binding = FragAboutUpdateDialogBinding.inflate(layoutInflater)
        setView(binding.root)
        setButton(BUTTON_POSITIVE, context.getString(R.string.install)){ _, _ ->
            onInstallClickListener?.invoke()
        }
        setButton(BUTTON_NEGATIVE,context.getString(R.string.cancel)){_,_ ->}
        setTitle(R.string.about_update)
        setIcon(R.drawable.ic_update)
    }

    fun show(data: AboutUpdateUiModel) {
        this.data = data
        bindData()
        show()
    }

    private fun bindData(){
        with(binding){
            tvVersionName.text = data.versionName
            tvChangeLog.text = data.changeLogs
            tvVersionCode.text = data.versionCode
        }
    }

    fun setOnInstallClickListener(listener: (()-> Unit)?){
        this.onInstallClickListener = listener
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

}