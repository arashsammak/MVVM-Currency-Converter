package com.currency.converter.app.data.db

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.Index
import androidx.room.PrimaryKey
import com.currency.converter.app.util.Constants.Companion.WALLET_TABLE

@Entity(tableName = WALLET_TABLE,indices = [Index(value = ["SYMBOL"], unique = true)])
data class WalletEntity(



    @ColumnInfo(name = "SYMBOL")
    var symbol: String,

    @ColumnInfo(name = "AMOUNT")
    var amount: Double,

    ) {
    @PrimaryKey(autoGenerate = true)
    var id = 0
}