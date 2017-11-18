package org.biais.kinformer

import org.slf4j.LoggerFactory
import java.time.LocalTime
import kotlin.concurrent.thread


class Crawler {
    private val updateTimeMs = 60000

    fun startCrawlerLoop() {
        thread {
            val crawling = true
            val db = InformerDB()
            db.connect()
            while (crawling) {
                val startTime = LocalTime.now().toNanoOfDay()
                crawl(db)
                val elapsedTimeMs = (LocalTime.now().toNanoOfDay() - startTime) / 1000
                Thread.sleep(updateTimeMs - elapsedTimeMs)
            }
        }
    }

    private fun crawl(db : InformerDB) {
        LoggerFactory.getLogger("Crawler").debug("Crawler run")
    }
}
