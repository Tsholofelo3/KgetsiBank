package bw.ac.kgetsibank.util;

import java.io.IOException;
import java.io.InputStream;

import java.nio.charset.StandardCharsets;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.sql.Statement;

public final class Database {

    private static final String DATABASE_URL =
            "jdbc:sqlite:kgetsibank.db";

    private Database() {
    }

    public static Connection connect()
            throws SQLException {

        Connection connection =
                DriverManager.getConnection(
                        DATABASE_URL
                );

        try (Statement statement =
                     connection.createStatement()) {

            statement.execute(
                    "PRAGMA foreign_keys = ON"
            );
        }

        return connection;
    }

    public static void initialize() {

        try (Connection connection =
                     connect()) {

            runSqlResource(
                    connection,
                    "/db/schema.sql"
            );

            runSqlResource(
                    connection,
                    "/db/seed.sql"
            );

            System.out.println(
                    "KgetsiBank database initialized successfully."
            );

        } catch (Exception exception) {

            throw new IllegalStateException(
                    "Unable to initialize KgetsiBank database.",
                    exception
            );
        }
    }

    private static void runSqlResource(
            Connection connection,
            String resourcePath
    ) throws IOException, SQLException {

        try (InputStream inputStream =
                     Database.class
                             .getResourceAsStream(
                                     resourcePath
                             )) {

            if (inputStream == null) {

                throw new IOException(
                        "Resource not found: "
                                + resourcePath
                );
            }

            String sql =
                    new String(
                            inputStream.readAllBytes(),
                            StandardCharsets.UTF_8
                    );

            String[] statements =
                    sql.split(";");

            try (Statement statement =
                         connection.createStatement()) {

                for (String command :
                        statements) {

                    String trimmed =
                            command.trim();

                    if (!trimmed.isEmpty()) {

                        statement.execute(
                                trimmed
                        );
                    }
                }
            }
        }
    }

    public static String getDatabaseUrl() {

        return DATABASE_URL;
    }
}