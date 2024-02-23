package de.sortsys.api;

import de.sortsys.api.db.PostgresConnectionPool;
import de.sortsys.api.db.PostgresDB;
import de.sortsys.api.db.PostgresCredentials;
import de.sortsys.api.server.SQLHTTPServer;

/**
 * Hello world!
 */
public class App {
    public static void main(String[] args) {
        final var credentials = new PostgresCredentials.Companion.

        final var pool = PostgresConnectionPool.Companion.create(5, credentials);
        final var db = new PostgresDB(pool);

        final var server = new SQLHTTPServer(8080, db);
        server.start();

        //var rows = db.queryQL(query);
        //System.out.println(Arrays.toString(rows));

        System.out.println("Server listening on port 8080");

        //pool.close();
    }
}
