package org.biais.kinformer

import org.jetbrains.exposed.dao.EntityID
import org.jetbrains.exposed.dao.IntEntity
import org.jetbrains.exposed.dao.IntEntityClass
import org.jetbrains.exposed.dao.IntIdTable
import org.jetbrains.exposed.sql.Database
import org.jetbrains.exposed.sql.SchemaUtils.create
import org.jetbrains.exposed.sql.transactions.TransactionManager
import org.jetbrains.exposed.sql.transactions.transaction
import org.slf4j.LoggerFactory
import java.sql.Connection

class InformerDB {
    object CurrencyPairs : IntIdTable() {
        val name = varchar("name", 50).uniqueIndex()
    }

    object Ticks : IntIdTable() {
        val currencyPair = reference("currencyPair", CurrencyPairs)
        val datetime = date("datetime")
        val value = long("value")
    }

    class Tick(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<Tick>(Ticks)
        var currencyPair by CurrencyPair referencedOn Ticks.currencyPair
        var datetime by Ticks.datetime
        var value by Ticks.value
    }

    class CurrencyPair(id: EntityID<Int>) : IntEntity(id) {
        companion object : IntEntityClass<CurrencyPair>(CurrencyPairs)
        var name by CurrencyPairs.name
    }

    fun connect() {
        Database.connect("jdbc:sqlite:test.db", "org.sqlite.JDBC")
        TransactionManager.manager.defaultIsolationLevel = Connection.TRANSACTION_SERIALIZABLE
        transaction {
            create(CurrencyPairs, Ticks)
        }
        LoggerFactory.getLogger("DB").debug("DB Connected")
    }
}
