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

import java.sql.*;

@Api("/ping")
public class HelloServiceImpl implements HelloService {

    private static final Logger logger = LoggerFactory.getLogger(HelloServiceImpl.class);
    private static String databaseServer;

    static {
        databaseServer = System.getenv("DB_SERVER");
        if (StringUtils.isBlank(databaseServer)) {
            logger.warn("DB_SERVER environment variable is not set");
        }
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String ping() {
        StringBuilder retval = new StringBuilder();
        String url = "jdbc:postgresql://" + databaseServer+ ":5432/testing?loggerLevel=DEBUG";
        retval.append("Attempting connection to: " + url);
        try (Connection connection = DriverManager.getConnection(url, "admin", "testing")) {
            System.out.println("Connected to PostgreSQL database.\n");
            Statement statement = connection.createStatement();
            ResultSet resultSet = statement.executeQuery("SELECT * FROM testtable");
            while (resultSet.next()) {
                retval.append("Found row: " + resultSet.getString(1));
            }

        } catch (SQLException e) {
            retval.append("Connection failure: " + e.getMessage() + " " + e.getSQLState() + " " + e.getErrorCode());
            e.printStackTrace();
        }
        return retval.toString();
    }

}
