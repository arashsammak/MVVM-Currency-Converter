package com.currency.converter.app.data.db

import androidx.room.*
import com.currency.converter.app.model.TransactionResponse
import kotlinx.coroutines.flow.Flow


@Dao
interface ExchangeDao {

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertNewWallet(wallet: WalletEntity)

    @Insert
    suspend fun insertNewTransaction(transaction: TransactionEntity)

    @Query("SELECT * FROM wallet_table")
    fun getWalletBalances(): Flow<List<WalletEntity>>

    @Update
    suspend fun updateWalletBalance(wallet: WalletEntity)

    @Query("UPDATE wallet_table SET amount = :amount where symbol = :symbol" )
    suspend fun updateWallet(symbol: String, amount: Double)

    @Query("UPDATE wallet_table SET amount = amount + :amount where symbol = :symbol" )
    suspend fun incrementWallet(symbol: String, amount: Double)

    @Query("UPDATE wallet_table SET amount = amount - :amount - :transactionFee where symbol = :symbol" )
    suspend fun decrementWallet(symbol: String, amount: Double,transactionFee : Double)

    @Query("SELECT * FROM wallet_table WHERE SYMBOL = :symbol")
    suspend fun getBalance(symbol: String): WalletEntity

    @Query("SELECT COUNT(SELL_AMOUNT) FROM transaction_table")
    suspend fun getTransactionCount(): Int

    @Query("SELECT EXISTS(SELECT * FROM wallet_table WHERE SYMBOL = :symbol)")
    suspend fun isRowIsExist(symbol : String) : Boolean

    @Transaction
    suspend fun upsertWallet(transaction : TransactionEntity) : TransactionResponse {

        if(isRowIsExist(transaction.sellSymbol)){
            val wallet = getBalance(transaction.sellSymbol)
            if (wallet.amount > transaction.sellAmount + transaction.transactionFee && !wallet.symbol.equals(transaction.buySymbol)){

                decrementWallet(transaction.sellSymbol,transaction.sellAmount,transaction.transactionFee)
                insertNewTransaction(transaction)

            }else {
                return TransactionResponse(false,transaction)
            }

            if (isRowIsExist(transaction.buySymbol)){
                incrementWallet(transaction.buySymbol,transaction.buyAmount)

            }else{
                insertNewWallet(WalletEntity(transaction.buySymbol,transaction.buyAmount))
            }

            return TransactionResponse(true,transaction)

        }else{
            return TransactionResponse(false,transaction)
        }

    }


}
