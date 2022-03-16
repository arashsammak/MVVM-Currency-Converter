package com.currency.converter.app.util

import androidx.lifecycle.viewModelScope
import com.currency.converter.app.model.Exchange
import com.currency.converter.app.util.Constants.Companion.BASED_CURRENCY
import kotlinx.coroutines.launch
import java.util.*
import kotlin.collections.HashMap
import kotlin.reflect.KVisibility
import kotlin.reflect.full.memberProperties

class Helper {

    companion object{


         fun generateRatesMap(response: Exchange?): HashMap<String, Double> {

            val rateHashMap: HashMap<String, Double> = HashMap<String, Double>()

                response?.rates!!::class.memberProperties.forEach {
                    if (it.visibility == KVisibility.PUBLIC) {

                        val name =
                            it.name.replaceFirstChar { if (it.isLowerCase()) it.titlecase(Locale.getDefault()) else it.toString() }
                        val value = it.getter.call(response.rates)
                        rateHashMap[name] = value as Double
                    }
                }

            return rateHashMap
        }


        fun calculateBuyAmount(from : String, sellRate: Double?,buyRate: Double?,amount : Double) : Double{
            var buyAmount = 0.0
            buyAmount = if (from.equals(BASED_CURRENCY)) {
                (buyRate!! / sellRate!! * amount)
            }else{
                val toEur = (1 / formatNum(sellRate!!) * formatNum(amount)) * 1
                (formatNum(buyRate!!) / 1 * formatNum(toEur))
            }
            return formatNum(buyAmount)
        }


         fun formatNum(num: Double): Double {

            return String.format("%.2f", num).toDouble()

        }

    }


}