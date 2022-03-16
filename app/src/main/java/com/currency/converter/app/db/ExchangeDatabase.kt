package com.currency.converter.app.data.db

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(entities = [WalletEntity::class,TransactionEntity::class], version = 1)


abstract class ExchangeDatabase: RoomDatabase() {

    abstract fun exchangeDao(): ExchangeDao
}
