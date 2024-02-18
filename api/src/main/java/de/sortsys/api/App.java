package de.sortsys.api;

import de.sortsys.api.db.PostgresDB;
import de.sortsys.api.db.SQLConnectionPool;
import de.sortsys.api.db.SQLPoolCredentials;
import de.sortsys.api.server.SQLHTTPServer;

import java.util.Arrays;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        final var credentials = new SQLPoolCredentials(
                "localhost",
                5432,
                "postgres",
                "postgres",
                "any"
        );

        final var pool = SQLConnectionPool.Companion.create(5, credentials);
        final var db = new PostgresDB(pool);

        final var server = new SQLHTTPServer(8080, db);
        server.start();

        //var rows = db.queryQL(query);
        //System.out.println(Arrays.toString(rows));

        System.out.println("Server listening on port 8080");

        //pool.close();
    }
}
