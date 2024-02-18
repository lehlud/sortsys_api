package de.sortsys.api.server


interface Server {
    val port: Int

    fun start()

    fun stop()
}

