package de.sortsys.api.server

import com.google.gson.Gson
import de.sortsys.api.db.SQLDatabase
import de.sortsys.api.ql.ParserException
import io.javalin.Javalin
import java.lang.Exception

class SQLHTTPServer(
    override val port: Int,
    private val db: SQLDatabase
) : Server {
    private var app: Javalin? = null

    override fun start() {
        val app = Javalin.create()

        app.post("/sql") {
            try {
                val query = it.body()
                val sql = db.parseQL(query)
                it.result(sql.sqlText())
            } catch (e: Throwable) {
                when (e) {
                    is ParserException -> {
                        it.status(400)
                        if (e.message != null) it.result(e.message!!)
                    }

                    is NotImplementedError -> {
                        it.status(505)
                        if (e.message != null) it.result(e.message!!)
                    }

                    else -> {
                        it.status(500)
                        if (e.message != null) println(e.message!!)
                    }
                }
            }
        }

        app.post("/query") {
            try {
                val query = it.body()
                val rows = db.queryQL(query)
                it.result(Gson().toJson(rows.map { row -> row.toArray() }))
            } catch (e: Throwable) {
                when (e) {
                    is ParserException -> {
                        it.status(400)
                        if (e.message != null) it.result(e.message!!)
                    }

                    is NotImplementedError -> {
                        it.status(505)
                        if (e.message != null) it.result(e.message!!)
                    }

                    else -> {
                        it.status(500)
                        if (e.message != null) println(e.message!!)
                    }
                }
            }
        }

        app.start(port)

        this.app = app
    }

    override fun stop() {
        app?.stop()
    }

}