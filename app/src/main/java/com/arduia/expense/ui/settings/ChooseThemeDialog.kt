package com.arduia.expense.ui.settings

import android.content.Context
import android.os.Bundle
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatDelegate
import com.arduia.expense.R
import com.arduia.expense.databinding.ChooseThemeDialogBinding

class ChooseThemeDialog (context: Context): AlertDialog(context) {

    private var _binding: ChooseThemeDialogBinding? = ChooseThemeDialogBinding.inflate(layoutInflater)
    private val binding get() = _binding!!

    private var currentThemeMode = AppCompatDelegate.MODE_NIGHT_NO
    private var onSaveListener = { _: Int -> }

    init {
        setView(binding.root)
        setButton(BUTTON_POSITIVE, context.getString(R.string.restart   )){ _, _ ->
            onSaveListener.invoke(currentThemeMode)
        }
        setButton(BUTTON_NEGATIVE, "Cancel"){_,_ ->}
        setTitle("Select Theme")
        setIcon(R.drawable.ic_theme)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setupView()
    }


    fun showData(mode: Int){
        currentThemeMode = mode
        show()
    }

    override fun onDetachedFromWindow() {
        super.onDetachedFromWindow()
        _binding = null
    }

    fun setOnSaveListener(listener: (Int)-> Unit){
        this.onSaveListener = listener
    }

    private fun setupView(){
        setCancelable(false)
        when(currentThemeMode){
            AppCompatDelegate.MODE_NIGHT_YES -> binding.rbDarkTheme.isChecked = true
            AppCompatDelegate.MODE_NIGHT_NO -> binding.rbLightTheme.isChecked = true
            AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM -> binding.rbSystemDefault.isChecked = true
        }

        binding.rgTheme.setOnCheckedChangeListener { _, id ->
            currentThemeMode = when(id){
                R.id.rb_dark_theme -> AppCompatDelegate.MODE_NIGHT_YES
                R.id.rb_light_theme -> AppCompatDelegate.MODE_NIGHT_NO
                else -> AppCompatDelegate.MODE_NIGHT_FOLLOW_SYSTEM
            }
        }
    }

}