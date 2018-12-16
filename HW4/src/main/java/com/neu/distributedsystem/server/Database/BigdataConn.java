package com.neu.distributedsystem.server.Database;


import com.google.cloud.bigtable.hbase.BigtableConfiguration;
        import java.io.IOException;
        import javax.servlet.ServletContext;
        import javax.servlet.ServletContextEvent;
        import javax.servlet.ServletContextListener;
        import javax.servlet.annotation.WebListener;
        import org.apache.hadoop.hbase.client.Connection;

/**
 * BigtableHelper, a ServletContextListener, is setup in web.xml to run before a jsp is run. Project
 * / Instance settings can be passed as an Environment Variable, a System Property, or set in
 * web.xml from a context-param
 */
@WebListener
public class BigdataConn implements ServletContextListener {

    private static String PROJECT_ID;
    private static String INSTANCE_ID;

    // The initial connection to Cloud Bigtable is an expensive operation -- We cache this Connection
    // to speed things up.  For this sample, keeping them here is a good idea, for
    // your application, you may wish to keep this somewhere else.
    private static Connection connection = null; // The authenticated connection

    private static ServletContext sc;

    /** Connect will establish the connection to Cloud Bigtable. */
    public static void connect() throws IOException {

        if (PROJECT_ID == null || INSTANCE_ID == null) {
            if (sc != null) {
                sc.log("environment variables BIGTABLE_PROJECT, and BIGTABLE_INSTANCE need to be defined.");
            }
            return;
        }

        connection = BigtableConfiguration.connect(PROJECT_ID, INSTANCE_ID);
    }

    /**
     * Get the shared connection to Cloud Bigtable.
     * @return the connection
     */
    public static Connection getConnection() {
        if (connection == null) {
            try {
                connect();
            } catch (IOException e) {
                if (sc != null) {
                    sc.log("connect ", e);
                }
            }
        }
        if (connection == null) {
            if (sc != null) {
                sc.log("BigtableHelper-No Connection");
            }
        }
        return connection;
    }

    @Override
    public void contextInitialized(ServletContextEvent event) {
        // This will be invoked as part of a warmup request, or the first user
        // request if no warmup request was invoked.

        if (event != null) {
            sc = event.getServletContext();
            if (PROJECT_ID == null) {
                PROJECT_ID = sc.getInitParameter("BIGTABLE_PROJECT");
            }
            if (INSTANCE_ID == null) {
                INSTANCE_ID = sc.getInitParameter("BIGTABLE_INSTANCE");
            }
        }

        if (PROJECT_ID != null && PROJECT_ID.startsWith("@")) {
            PROJECT_ID = null;
        }
        if (INSTANCE_ID != null && INSTANCE_ID.startsWith("@")) {
            INSTANCE_ID = null;
        }

        if (PROJECT_ID == null) {
            PROJECT_ID = System.getProperty("BIGTABLE_PROJECT");
        }
        if (INSTANCE_ID == null) {
            INSTANCE_ID = System.getProperty("BIGTABLE_INSTANCE");
        }

        try {
            connect();
        } catch (IOException e) {
            if (sc != null) {
                sc.log("BigtableHelper - connect ", e);
            }
        }
        if (connection == null) {
            if (sc != null) {
                sc.log("BigtableHelper-No Connection");
            }
        }
        if (sc != null) {
            sc.log("ctx Initialized: " + PROJECT_ID + " " + INSTANCE_ID);
        }
    }

    @Override
    public void contextDestroyed(ServletContextEvent event) {
        // App Engine does not currently invoke this method.
        if (connection == null) {
            return;
        }
        try {
            connection.close();
        } catch (IOException io) {
            if (sc != null) {
                sc.log("contextDestroyed ", io);
            }
        }
        connection = null;
    }
}