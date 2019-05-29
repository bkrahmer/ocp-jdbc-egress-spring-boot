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

import java.sql.*;

@Api("/testConnection")
public class HelloServiceImpl implements HelloService {

    static {
        try {
            Class.forName("org.postgresql.Driver");
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        }
    }

    public String testConnection() {
        StringBuilder retval = new StringBuilder();
        try (Connection connection = DriverManager.getConnection(
                "jdbc:postgresql://support1.849d.internal:5432/example", "admin", "testing")) {
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

}
