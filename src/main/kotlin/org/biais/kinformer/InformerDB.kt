package org.biais.kinformer

import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.slf4j.LoggerFactory

class InformerDB {
    object CurrencyPair : IntIdTable() {
        val name = varchar("name", 50)
    }

    object Users : IntIdTable() {
        val currencyPair = reference("currencyPair", CurrencyPair)
        val datetime = date("datetime")
        val value = integer("value")
    }

    fun connect() {
        Database.connect("jdbc:sqlite:test.db", "org.sqlite.JDBC")
        LoggerFactory.getLogger("DB").debug("DB Connected")
    }
}
