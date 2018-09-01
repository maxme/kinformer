package org.biais.kinformer

import org.biais.kinformer.InformerDB.CurrencyPair.*
import org.jetbrains.exposed.sql.transactions.transaction
import org.joda.time.DateTime
import org.slf4j.LoggerFactory
import java.time.LocalDateTime
import java.time.LocalTime
import java.time.ZoneOffset
import kotlin.concurrent.thread

class Crawler {
    private val updateTimeSeconds = 60

    fun startCrawlerLoop() {
        thread {
            val crawling = true
            val db = InformerDB()
            db.connect()
            while (crawling) {
                val startTime = LocalTime.now().toSecondOfDay()
                crawl(db)
                val elapsedTimeSeconds = (LocalTime.now().toSecondOfDay() - startTime)
                Thread.sleep((updateTimeSeconds - elapsedTimeSeconds).toLong() * 1000)
            }
        }
    }

    private fun crawl(db: InformerDB) {
        LoggerFactory.getLogger("Crawler").debug("Crawler run")
        db.connect()
        val tickers = listOf("etheur", "btceur", "ethbtc")
        val date = LocalDateTime.now().toEpochSecond(ZoneOffset.UTC)
        for (ticker in tickers) {
            transaction {
                val resQuery = InformerDB.CurrencyPair.find { InformerDB.CurrencyPairs.name eq ticker }
                var currPair: InformerDB.CurrencyPair
                if (!resQuery.empty()) {
                    currPair = resQuery.first()
                } else {
                    currPair = InformerDB.CurrencyPair.new { name = ticker }
                }
                val v = getTickerFromBitstamp(ticker)
                InformerDB.Tick.new {
                    currencyPair = currPair
                    datetime = DateTime(date * 1000)
                    value = (v * 1000000).toLong()
                }
            }
        }
    }
}
