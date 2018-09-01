package org.biais.kinformer

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.file
import io.ktor.content.static
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.html.* // ktlint-disable no-wildcard-imports
import org.slf4j.LoggerFactory
import java.time.LocalDateTime

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

data class CachedItem(val expirationDate: LocalDateTime, val data: Any)

fun getCachedItem(cache: HashMap<String, CachedItem>, key: String, getData: () -> Any): Any {
    val current = LocalDateTime.now()
    if (cache.containsKey(key)) {
        LoggerFactory.getLogger("Cache").debug(cache[key].toString())
        if (cache[key]?.expirationDate?.isAfter(current) == true) {
            LoggerFactory.getLogger("Cache").debug("Cache hit")
            return cache[key]?.data ?: getData()
        } else {
            // Expire cached item
            LoggerFactory.getLogger("Cache").debug("Cache expired")
            cache.remove(key)
        }
    }
    LoggerFactory.getLogger("Cache").debug("Calling getData()")
    val res = getData()
    cache[key] = CachedItem(current.plusSeconds(120), res)
    return res
}

fun Application.main() {
    // Init and start the Crawler
    val crawler = Crawler()
    crawler.startCrawlerLoop()

    val cache = HashMap<String, CachedItem>()

    install(DefaultHeaders)
    install(CallLogging)

    routing {
        get("/") {
            val title = "BTC/ETH Informer"
            val tickers = getCachedItem(cache, "tickers", ::getAllTickers) as AllTickers
            call.respondHtml {
                head {
                    meta("viewport", "width=device-width; initial-scale=1.0;")
                    link("/btc-style.css", "stylesheet")
                    title { +title }
                }
                body {
                    div("header") {
                        div("container") {
                            +title
                        }
                    }
                    div("container") {
                        div("ticker") { +"1 BTC = ${tickers.btceth.format(2)} ETH" }
                        div("ticker") { +"1 ETH = ${tickers.etheur.format(2)} EUR" }
                        div("ticker") { +"1 BTC = ${tickers.btceur.format(2)} EUR" }
                    }
                }
            }
        }
        static("/style.css") {
            file("src/main/resources/style.css")
        }
    }
}

