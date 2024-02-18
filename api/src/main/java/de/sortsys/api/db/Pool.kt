package de.sortsys.api.db

import java.sql.Connection
import java.sql.DriverManager
import java.util.LinkedList
import java.util.Queue
import java.util.concurrent.Semaphore

interface ConnectionPool<T> {
    fun acquire(): T
    fun release(conn: T)
    fun close()
}

abstract class FixedSizeConnectionPool<T>(
    private val pool: Queue<T>,
    private val semaphore: Semaphore,
) : ConnectionPool<T> {
    override fun acquire(): T {
        return synchronized(pool) {
            semaphore.acquire()
            pool.remove()
        }
    }

    override fun release(conn: T) {
        synchronized(pool) {
            semaphore.release()
            pool.add(conn)
        }
    }

    protected abstract fun closeConn(conn: T)

    override fun close() {
        synchronized(pool) {
            while (semaphore.tryAcquire()) {
                closeConn(pool.remove())
            }
        }
    }
}

data class SQLPoolCredentials(
    val host: String,
    val port: Int,
    val database: String,
    val user: String,
    val pass: String,
)

class SQLConnectionPool(pool: Queue<Connection>, semaphore: Semaphore) :
    FixedSizeConnectionPool<Connection>(pool, semaphore) {
    companion object {
        fun create(size: Int, creds: SQLPoolCredentials): SQLConnectionPool {
            val pool = LinkedList<Connection>()
            for (i in 1..size) {
                val url = "jdbc:postgresql://${creds.host}:${creds.port}/${creds.database}"
                val conn = DriverManager.getConnection(url, creds.user, creds.pass)
                pool.add(conn)
            }

            return SQLConnectionPool(pool, Semaphore(size))
        }
    }

    override fun closeConn(conn: Connection) {
        conn.close()
    }
}
