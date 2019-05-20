package org.biais.kinformer

import com.beust.klaxon.JsonObject
import com.beust.klaxon.Parser
import com.beust.klaxon.string
import com.github.kittinunf.fuel.Fuel
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.slf4j.LoggerFactory

data class AllTickers(val etheur: Double, val btceur: Double, val btceth: Double)

fun getTickerFromBitstamp(code: String): Double {
    LoggerFactory.getLogger("Ticker").debug("Get $code ticker")
    val prefixUrl = "https://www.bitstamp.net/api/v2/ticker/"
    val (_, _, result) = Fuel.get(prefixUrl + code).responseString()
    val json = Parser().parse(result.get().reader()) as JsonObject
    return json.string("last")?.toDouble() ?: 0.0
}

fun getAllTickers(): AllTickers {
    return runBlocking() {
        // List of all supported tickers
        val tickers = listOf("etheur", "btceur", "ethbtc")
        val res = mutableMapOf<String, Double>()
        val jobs = tickers.map {
            launch {
                res[it] = getTickerFromBitstamp(it)
            }
        }
        jobs.forEach { it.join() }
        val ethbtc: Double = res["ethbtc"] ?: 1.0
        AllTickers(res["etheur"] ?: 1.0, res["btceur"] ?: 1.0, 1.0 / ethbtc)
    }
}
