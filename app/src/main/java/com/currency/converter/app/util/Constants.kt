package com.currency.converter.app.util

class Constants {

    companion object{

        //YOU CAN CHANGE IT
        const val ACCESS_KEY = "50b2c7b78e5a0aeb94c783f3ad690e8b"
        const val FREE_FEE_NUMBER = 5
        const val BASED_CURRENCY = "EUR"
        const val GIVEAWAY_DEPOSIT = 1000.0


        const val DATABASE_NAME = "CURRENCY_CONVERTER_DB"
        const val WALLET_TABLE = "WALLET_TABLE"
        const val TRANSACTION_TABLE = "TRANSACTION_TABLE"
        const val TRANSACTION_COUNT = "TRANSACTION_COUNT"

        //YOU CAN DEFINE BASE_URL IN build.gradle AS WELL
        const val BASE_URL = "http://api.exchangeratesapi.io"


    }
}