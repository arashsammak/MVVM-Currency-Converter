package com.currency.converter.app.ui


import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.EditText
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.LinearLayoutManager
import com.currency.converter.app.R
import com.currency.converter.app.builder.ConversionBuilder
import com.currency.converter.app.data.db.TransactionEntity
import com.currency.converter.app.data.db.WalletEntity
import com.currency.converter.app.databinding.ActivityMainBinding
import com.currency.converter.app.ui.adapter.BalancesAdapter
import com.currency.converter.app.util.Constants.Companion.ACCESS_KEY
import com.currency.converter.app.util.Constants.Companion.GIVEAWAY_DEPOSIT
import com.currency.converter.app.util.Constants.Companion.TRANSACTION_COUNT
import com.currency.converter.app.util.DefaultPreExecutionPolicy
import com.currency.converter.app.util.Helper
import com.currency.converter.app.util.PreferenceHelper
import com.currency.converter.app.util.PreferenceHelper.get
import com.currency.converter.app.util.PreferenceHelper.set
import com.currency.converter.app.util.Status
import com.currency.converter.app.viewmodel.MainViewModel
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.android.synthetic.main.activity_main.*
import java.util.*
import kotlin.collections.HashMap


@AndroidEntryPoint
class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    private val adapter by lazy { BalancesAdapter() }
    lateinit var viewModel: MainViewModel

    private var rateHashMap: HashMap<String, Double> = HashMap<String, Double>()
    private var balances = listOf<WalletEntity>()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        initActivity()
        giveawayDeposit(GIVEAWAY_DEPOSIT)

    }

    private fun initActivity() {

        viewModel = ViewModelProvider(this)[MainViewModel::class.java]

        enabledEditText(false)

        setupBalancesList()
        setupSellSpinnerListener()
        setupBuySpinnerListener()

        observeLocalData()
        observeRatesFromNetwork()
        observeTransactionCallback()

        enabledSubmitButton(false)

        etSell.afterTextChanged {

            if (it.isNotEmpty() && !sellSpinner.selectedItem.toString().equals(buySpinner.selectedItem.toString())) {

                enabledSubmitButton(true)

                conversionPreview(
                    sellSpinner.selectedItem.toString(),
                    buySpinner.selectedItem.toString(),
                    it.toDouble()
                )

            } else {

                enabledSubmitButton(false)
                tvBuy.text = ""
            }
        }

        btnSubmit.setOnClickListener {

            if (!etSell.text.isNullOrEmpty()) {
                submitTransaction()
            } else {
                Toast.makeText(this, "Fill the sell field", Toast.LENGTH_SHORT).show()
            }

        }

    }

    private fun setupBalancesList() {
        binding.rvBalances.layoutManager =
            LinearLayoutManager(this, LinearLayoutManager.HORIZONTAL, false)
        binding.rvBalances.adapter = adapter
    }

    private fun giveawayDeposit(amount: Double) {

        if (PreferenceHelper.defaultPrefs(this)["FIRST", ""] == "") {

            val walletEntity = WalletEntity("EUR", amount)

            viewModel.insertNewWallet(walletEntity)

        }
        PreferenceHelper.defaultPrefs(this)["FIRST"] = "FILLED"


    }

    private fun observeRatesFromNetwork() {


        viewModel.getRates(ACCESS_KEY).observe(this, Observer {

            it?.let { resource ->
                when (resource.status) {

                    Status.SUCCESS -> {
                        showProgress(false)
                        resource.data?.let { rates -> rateHashMap = rates }

                        fillBuySpinner(rateHashMap.toSortedMap())
                        enabledEditText(true)
                    }
                    Status.ERROR -> {
                        showProgress(false)
                        Toast.makeText(this, it.message, Toast.LENGTH_LONG).show()
                    }
                    Status.LOADING -> {
                        showProgress(true)
                    }
                }

            }
        })

    }

    private fun observeLocalData() {
        viewModel.getWalletBalancesFromDB().observe(this, { response ->

            response.let { adapter.setData(it) }
            balances = response

            fillSellSpinner(balances)
        })

    }

    private fun observeTransactionCallback() {
        viewModel.transactionMessage.observe(this, { response ->
            getResponse(response.isCommitted,response.transactionEntity)

        })
    }

    private fun submitTransaction() {

        val sellSymbol = sellSpinner.selectedItem.toString()
        val buySymbol = buySpinner.selectedItem.toString()
        val sellAmount = etSell.text.toString().toDouble()

        ConversionBuilder.Builder(balances, viewModel, rateHashMap)
            .setFrom(sellSymbol)
            .setTo(buySymbol)
            .setAmount(sellAmount)
            .setPreExecutionPolicy(DefaultPreExecutionPolicy::class)
            .build()


    }

    private fun conversionPreview(from: String, to: String, amount: Double) {

        val sellRate = rateHashMap[from]
        val buyRate = rateHashMap[to]

        val buyAmount = Helper.calculateBuyAmount(from, sellRate, buyRate, amount)

        tvBuy.text = getString(R.string.buy_amount, Helper.formatNum(buyAmount).toString());

    }

    private fun fillSellSpinner(myWallet: List<WalletEntity>) {


        val sellArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            myWallet.map { it.symbol }
        )
        sellArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        if (sellSpinner.adapter == null) {
            sellSpinner!!.adapter = sellArrayAdapter
        } else {
            var selectedItem = sellArrayAdapter.getPosition(sellSpinner.selectedItem as String?)
            sellSpinner!!.adapter = sellArrayAdapter
            sellSpinner.setSelection(selectedItem)
        }
    }

    private fun fillBuySpinner(hashMap: SortedMap<String, Double>) {

        val buyArrayAdapter = ArrayAdapter(
            this,
            android.R.layout.simple_spinner_item,
            hashMap.keys.toTypedArray()
        )
        buyArrayAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        if (buySpinner.adapter == null) {
            buySpinner!!.adapter = buyArrayAdapter
        } else {
            var selectedItem = buyArrayAdapter.getPosition(buySpinner.selectedItem as String?)
            buySpinner!!.adapter = buyArrayAdapter
            buySpinner.setSelection(selectedItem)
        }
    }

    private fun setupBuySpinnerListener() {
        buySpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (etSell.text!!.isNotEmpty() && !sellSpinner.selectedItem.toString().equals(buySpinner.selectedItem.toString())) {
                    enabledSubmitButton(true)
                    conversionPreview(
                        sellSpinner.selectedItem.toString(),
                        buySpinner.selectedItem.toString(),
                        etSell.text.toString().toDouble()
                    )
                }else{
                    enabledSubmitButton(false)
                    tvBuy.text = ""
                }
            }
        }
    }

    private fun setupSellSpinnerListener() {

        sellSpinner.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {

            override fun onNothingSelected(parent: AdapterView<*>?) {
            }

            override fun onItemSelected(
                parent: AdapterView<*>?,
                view: View?,
                position: Int,
                id: Long
            ) {

                if (etSell.text!!.isNotEmpty() && !sellSpinner.selectedItem.toString().equals(buySpinner.selectedItem.toString())) {
                    enabledSubmitButton(true)
                    conversionPreview(
                        sellSpinner.selectedItem.toString(),
                        // parent?.getItemAtPosition(position).toString(),
                        buySpinner.selectedItem.toString(),
                        etSell.text.toString().toDouble()
                    )
                }else{
                    enabledSubmitButton(false)
                    tvBuy.text = ""
                }
            }
        }


    }

    private fun getResponse(callbackStatus: Boolean, transactionEntity: TransactionEntity) {

        when (callbackStatus) {

          false -> {

                AlertDialog.Builder(this)
                    .setTitle("Not allowed transaction")
                    .setNeutralButton("Done") { _, _ -> }
                    .setMessage("Your wallet balance is not enough or you want to try make wrong transaction!")
                    .show()
            }
          true -> {

                AlertDialog.Builder(this)
                    .setTitle("Currency converted")
                    .setNeutralButton("Done") { _, _ -> }
                    .setMessage(
                        "You have converted " +
                                "${transactionEntity.sellAmount} ${transactionEntity.sellSymbol} to " +
                                "${transactionEntity.buyAmount} ${transactionEntity.buySymbol}. Commission Fee " +
                                "-${transactionEntity.transactionFee} ${transactionEntity.sellSymbol}"
                    )
                    .show()
            }


        }
    }

    private fun EditText.afterTextChanged(afterTextChanged: (String) -> Unit) {

        this.addTextChangedListener(object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {
            }

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
            }

            override fun afterTextChanged(editable: Editable?) {
                afterTextChanged.invoke(editable.toString())
            }
        })
    }

    private fun enabledSubmitButton(enabled: Boolean) {

        if (enabled) {
            btnSubmit.isClickable = true
            btnSubmit.setBackgroundResource(R.drawable.btn_onclick)
        } else {
            btnSubmit.isClickable = false
            btnSubmit.setBackgroundResource(R.drawable.btn_disabled_onclick)
        }
    }

    private fun enabledEditText(enabled: Boolean) {

        etSell.isClickable = enabled
    }

    private fun showProgress(status: Boolean) {
        if (status) {
            pbLoading.visibility = View.VISIBLE
        } else {
            pbLoading.visibility = View.GONE
        }
    }

}

