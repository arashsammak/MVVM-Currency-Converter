package com.currency.converter.app.respository

import com.currency.converter.app.network.ExchangeApi
import com.currency.converter.app.data.db.TransactionEntity
import com.currency.converter.app.data.db.ExchangeDao
import com.currency.converter.app.data.db.WalletEntity
import com.currency.converter.app.model.TransactionResponse
import dagger.hilt.android.scopes.ActivityRetainedScoped
import kotlinx.coroutines.flow.Flow
import javax.inject.Inject

@ActivityRetainedScoped
class ExchangeRepository @Inject constructor(
    private val exchangeDao: ExchangeDao,
    private val network: ExchangeApi,
    ) {

    private val wallet = exchangeDao

    fun getBalances(): Flow<List<WalletEntity>> {
        return wallet.getWalletBalances();
    }

    suspend fun insertNewWallet(walletEntity: WalletEntity) {
        wallet.insertNewWallet(walletEntity)
    }

    suspend fun updateWalletBalance(walletEntity: WalletEntity) {
        wallet.updateWalletBalance(walletEntity)
    }

    suspend fun upsertWallet(transactionEntity: TransactionEntity): TransactionResponse {
        return wallet.upsertWallet(transactionEntity)
    }

    //GET RATES FROM API
    suspend fun getRatesFromNetwork(accessKey: String) = network.getRates(accessKey)

    suspend fun getTransactionCount(): Int {
        return wallet.getTransactionCount()
    }
}