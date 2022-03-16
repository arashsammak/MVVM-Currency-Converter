package com.currency.converter.app.binding_adapter

import android.widget.TextView
import androidx.databinding.BindingAdapter
import com.currency.converter.app.util.Helper

class ExchangeBinding {

    companion object{

        @BindingAdapter("setAmount")
        @JvmStatic
        fun setAmount(textView: TextView, amount: Double ){

            textView.text = Helper.formatNum(amount).toString()

        }
    }
}