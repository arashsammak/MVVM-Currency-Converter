package com.currency.converter.app.builder

import com.currency.converter.app.data.db.TransactionEntity
import com.currency.converter.app.data.db.WalletEntity
import com.currency.converter.app.util.DefaultPreExecutionPolicy
import com.currency.converter.app.util.Helper
import com.currency.converter.app.viewmodel.MainViewModel
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.full.primaryConstructor

class ConversionBuilder private constructor(
    builder: Builder,
    balances: List<WalletEntity>,
    viewModel: MainViewModel,
    rateHashMap: HashMap<String, Double>
) {


    var from: String = ""
    var to: String = ""
    var fee: Double = 0.7
    var amount: Double = 0.0
    var preConversionPolicy: KClass<DefaultPreExecutionPolicy>? = null



    class Builder(
        private var balances: List<WalletEntity>,
        private var viewModel: MainViewModel,
        private var rateHashMap: HashMap<String, Double>,
    ) {

        private var from: String = ""
        private var to: String = ""
        private var fee: Double = 0.7
        private var amount: Double = 0.0

        private var preConversionPolicy: KClass<DefaultPreExecutionPolicy>? = null


        fun setPreExecutionPolicy(preConversionPolicy: KClass<DefaultPreExecutionPolicy>) =
            apply { this.preConversionPolicy = preConversionPolicy }

        fun setFrom(from: String) = apply { this.from = from }
        fun setTo(to: String) = apply { this.to = to }
        fun setAmount(amount: Double) = apply { this.amount = amount }
        fun build(): ConversionBuilder = ConversionBuilder(this, balances, viewModel, rateHashMap)



        fun getFrom() = from
        fun getTo() = to
        fun getFee() = fee
        fun getAmount() = amount
        fun getPreExecution() = preConversionPolicy


    }

    init {

        from = builder.getFrom()
        to = builder.getTo()
        amount = Helper.formatNum(builder.getAmount())
        preConversionPolicy = builder.getPreExecution()


        var transactionRequest = TransactionEntity(
            sellAmount = Helper.formatNum(amount),
            buyAmount = 0.0,
            sellSymbol = from,
            buySymbol = to,
            transactionFee = fee,
            date = Date().toString()
        )


        viewModel.getTransactionCount()
        //SET CUSTOM POLICY FOR TRANSACTION
        val preConversionPolicyConstructor = preConversionPolicy?.primaryConstructor
        val instance = preConversionPolicyConstructor!!.call(transactionRequest)
        transactionRequest = instance.preExecute(viewModel)


        val sellRate = rateHashMap[transactionRequest.sellSymbol]
        val buyRate = rateHashMap[transactionRequest.buySymbol]

        val buyAmount = Helper.calculateBuyAmount(
                transactionRequest.sellSymbol,
                sellRate,
                buyRate,
                Helper.formatNum(transactionRequest.sellAmount))

        transactionRequest.buyAmount = Helper.formatNum(buyAmount)
        transactionRequest.transactionFee = Helper.formatNum(transactionRequest.sellAmount * transactionRequest.transactionFee / 100)

        //CALL FOR COMMIT TRANSACTION
        viewModel.upsertWallet(transactionRequest)
        

    }




}