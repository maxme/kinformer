package org.biais.kinformer

import io.ktor.application.Application
import io.ktor.application.call
import io.ktor.application.install
import io.ktor.content.files
import io.ktor.content.static
import io.ktor.features.CallLogging
import io.ktor.features.DefaultHeaders
import io.ktor.html.respondHtml
import io.ktor.routing.get
import io.ktor.routing.routing
import kotlinx.html.* // ktlint-disable no-wildcard-imports

fun Double.format(digits: Int) = java.lang.String.format("%.${digits}f", this)

fun Application.main() {
    install(DefaultHeaders)
    install(CallLogging)
    routing {
        get("/") {
            val title = "BTC/ETH Informer"
            val tickers = getAllTickers()
            call.respondHtml {
                head {
                    link("static/style.css", "stylesheet")
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
        static("static") {
            files("src/main/resources/")
        }
    }
}

