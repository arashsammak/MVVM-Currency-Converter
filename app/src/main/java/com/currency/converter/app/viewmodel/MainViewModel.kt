package com.currency.converter.app.viewmodel

import android.app.Application
import androidx.lifecycle.*
import com.currency.converter.app.data.db.TransactionEntity
import com.currency.converter.app.data.db.WalletEntity
import com.currency.converter.app.model.TransactionResponse
import com.currency.converter.app.respository.ExchangeRepository
import com.currency.converter.app.util.Helper
import com.currency.converter.app.util.Resource
import dagger.hilt.android.lifecycle.HiltViewModel
import kotlinx.coroutines.*
import javax.inject.Inject


@HiltViewModel
class MainViewModel @Inject constructor(
    private val repository: ExchangeRepository,
    application: Application

) : AndroidViewModel(application) {

     val count =  MutableLiveData<Int>()
     var transactionMessage = MutableLiveData<TransactionResponse>()

    fun getWalletBalancesFromDB(): LiveData<List<WalletEntity>> = repository.getBalances().asLiveData()

    fun insertNewWallet(walletEntity: WalletEntity) {
        viewModelScope.launch(Dispatchers.IO)
        {
            repository.insertNewWallet(walletEntity)
        }
    }

    fun upsertWallet(transactionEntity: TransactionEntity) {
        viewModelScope.launch(Dispatchers.IO)
        {
                transactionMessage.postValue(repository.upsertWallet(transactionEntity))
        }
    }

    fun getTransactionCount()  {

        viewModelScope.launch(Dispatchers.IO)
        {

            count.postValue(repository.getTransactionCount())
        }
    }

    fun getRates(accessKey: String)= liveData(Dispatchers.IO) {

       // while (true) {
            emit(Resource.loading(data = null))
            try {
                emit(
                    Resource.success(
                        data = Helper.generateRatesMap(
                            repository.getRatesFromNetwork(
                                accessKey
                            ).body()
                        )
                    )
                )
            } catch (exception: Exception) {
                emit(Resource.error(data = null, message = exception.message ?: "Error occurred"))
            }
         //   delay(5000)
        //}
    }





}









