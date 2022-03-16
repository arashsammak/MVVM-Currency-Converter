package com.currency.converter.app.db


import androidx.room.Room
import androidx.test.core.app.ApplicationProvider
import androidx.test.ext.junit.runners.AndroidJUnit4
import androidx.test.filters.SmallTest
import com.currency.converter.app.data.db.ExchangeDao
import com.currency.converter.app.data.db.ExchangeDatabase
import com.currency.converter.app.data.db.WalletEntity
import com.google.common.truth.Truth.assertThat
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.FlowPreview
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.runBlocking
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.junit.runner.RunWith

@FlowPreview
@ExperimentalCoroutinesApi
@RunWith(AndroidJUnit4::class)
@SmallTest
class ExchangeDatabaseTest {

    private lateinit var database: ExchangeDatabase
    private lateinit var dao: ExchangeDao
    private var savedWalletId: Int = 0

    @Before
    fun setup() {
        database = Room.inMemoryDatabaseBuilder(
            ApplicationProvider.getApplicationContext(),
            ExchangeDatabase::class.java
        ).allowMainThreadQueries().build()
        dao = database.exchangeDao()

    }

    @After
    fun teardown() {
        database.close()
    }

    @Test
    fun insertTesting() = runBlocking {


        insertWallet()


    }

    private fun insertWallet() = runBlocking {
        val wallet = WalletEntity("EUR", 1000.0)
        dao.insertNewWallet(wallet)

        savedWalletId = wallet.id

        val allExchanges = dao.getWalletBalances()

        assertThat(allExchanges.first().contains(wallet)).isTrue()

    }
}


