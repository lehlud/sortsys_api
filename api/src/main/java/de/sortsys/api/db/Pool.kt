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

