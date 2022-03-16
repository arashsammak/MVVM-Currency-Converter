package com.currency.converter.app.util

import androidx.lifecycle.Observer
import com.currency.converter.app.data.db.TransactionEntity
import com.currency.converter.app.util.Constants.Companion.FREE_FEE_NUMBER
import com.currency.converter.app.viewmodel.MainViewModel


class DefaultPreExecutionPolicy(transaction: TransactionEntity) : PreConversionPolicy(transaction) {

    var transactionCount: Int = 0
    override fun preExecute(viewModel: MainViewModel): TransactionEntity {


        viewModel.count.observeForever(object : Observer<Int> {
            override fun onChanged(t: Int) {
                transactionCount = t+2
                viewModel.count.removeObserver(this)
            }
        })

        freeFeeRules(FREE_FEE_NUMBER)

        return transaction
    }

    private fun freeFeeRules(num : Int){

       if (transactionCount > num) {

            transaction.transactionFee = 0.7

        } else {
            transaction.transactionFee = 0.0
        }


        //TODO add more rules for the fee
    }


}