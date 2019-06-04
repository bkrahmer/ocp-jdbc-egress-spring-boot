/**
 *  Copyright 2005-2016 Red Hat, Inc.
 *
 *  Red Hat licenses this file to you under the Apache License, version
 *  2.0 (the "License"); you may not use this file except in compliance
 *  with the License.  You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or
 *  implied.  See the License for the specific language governing
 *  permissions and limitations under the License.
 */
package io.fabric8.quickstarts.cxf.jaxrs;

import io.swagger.annotations.Api;
import org.apache.commons.lang3.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.*;
import java.util.Properties;

@Api("/ping")
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    private String databaseServer;

    public HelloServiceImpl() {
        databaseServer = System.getenv("DB_SERVER");
        if (StringUtils.isBlank(databaseServer)) {
            logger.warn("DB_SERVER environment variable is not set");
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
        extractResources();
    }

    public String ping() {
        StringBuilder retval = new StringBuilder();
        Properties props = new Properties();
        props.setProperty("user", "admin");
        props.setProperty("password", "testing");
        props.setProperty("ssl", "true");
        props.setProperty("sslcert", "/tmp/client.crt");
        props.setProperty("sslkey", "/tmp/client.key");
        props.setProperty("sslrootcert", "/tmp/root.crt");
        props.setProperty("sslpassword", "");
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://" + databaseServer+ ":5432/testing", props)) {
            System.out.println("Connected to PostgreSQL database.\n");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM testtable");
            while (resultSet.next()) {
                retval.append("Found row: " + resultSet.getString(1));
            }

        } catch (SQLException e) {
            retval.append("Connection failure: " + e.getMessage() + " " + e.getSQLState() + " " + e.getErrorCode());
        }
        return retval.toString();
    }

    private void extractResources() {
        File sslCert = new File("/tmp/client.crt");
        if (! sslCert.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/client.crt"), sslCert.toPath());
            } catch (IOException e) {
                logger.warn("Caught exception extracting client certificate");
            }
        }
        sslCert = new File("/tmp/client.key");
        if (! sslCert.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/client.key"), sslCert.toPath());
            } catch (IOException e) {
                logger.warn("Caught exception extracting client key");
            }
        }
        sslCert = new File("/tmp/root.crt");
        if (! sslCert.exists()) {
            try {
                Files.copy(this.getClass().getResourceAsStream("/root.crt"), sslCert.toPath());
            } catch (IOException e) {
                logger.warn("Caught exception extracting root certificate");
            }
        }
    }

}
