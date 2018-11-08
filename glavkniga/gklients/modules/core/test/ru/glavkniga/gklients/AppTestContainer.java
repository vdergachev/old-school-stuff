package ru.glavkniga.gklients;

import com.haulmont.cuba.testsupport.TestContainer;

import static com.google.common.collect.Lists.newArrayList;

/**
 * Created by Vladimir on 18.05.2017.
 */
public class AppTestContainer extends TestContainer {

    public static AppTestContainer create() {
        return Container.INSTANCE;
    }

    private static class Container extends AppTestContainer {

        public static final Container INSTANCE = new Container();

        private static volatile boolean initialized;

        private Container() {
            super();

            appComponents = newArrayList(
                    "com.haulmont.cuba",
                    "com.haulmont.reports",
                    "com.haulmont.fts"
            );

            appPropertiesFiles = newArrayList(
                    "app.properties",
                    "test-app.properties"
            );

            dbDriver = "org.postgresql.Driver";
            dbUrl = "jdbc:postgresql://localhost/testgk";
            dbUser = "gklients";
            dbPassword = "gklients";
        }

        @Override
        public void before() throws Throwable {
            if (!initialized) {
                super.before();
                initialized = true;
            }
            setupContext();
        }

        @Override
        public void after() {
            cleanupContext();
        }
    }
}
