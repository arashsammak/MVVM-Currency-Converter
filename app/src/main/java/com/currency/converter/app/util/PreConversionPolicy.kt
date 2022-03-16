package com.currency.converter.app.util

import com.currency.converter.app.data.db.TransactionEntity
import com.currency.converter.app.viewmodel.MainViewModel

abstract class PreConversionPolicy(val transaction: TransactionEntity) {
    abstract fun preExecute(viewModel: MainViewModel): TransactionEntity


}