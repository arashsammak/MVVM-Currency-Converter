package com.currency.converter.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.currency.converter.app.util.Constants.Companion.TRANSACTION_TABLE
import java.util.*

@Entity(tableName = TRANSACTION_TABLE)
data class TransactionEntity(


    @ColumnInfo(name = "SELL_AMOUNT")
    var sellAmount: Double,

    @ColumnInfo(name = "BUY_AMOUNT")
    var buyAmount: Double,

    @ColumnInfo(name = "SELL_SYMBOL")
    var sellSymbol: String,

    @ColumnInfo(name = "BUY_SYMBOL")
    var buySymbol: String,

    @ColumnInfo(name = "TRANSACTION_FEE")
    var transactionFee: Double,

    @ColumnInfo(name = "TRANSACTION_DATE")
    var date: String,



    ) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}