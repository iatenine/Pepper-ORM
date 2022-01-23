package ORM;

import Logging.ORMLogger;
import org.apache.logging.log4j.core.appender.nosql.AbstractNoSqlConnection;
import org.junit.jupiter.api.Test;

import java.lang.reflect.Type;
import java.sql.ResultSet;
import java.sql.SQLException;

import static org.junit.jupiter.api.Assertions.*;

class CustomORMTest {

    @Test
    void connect() {

        String enpdoint = "";
        String username = "";
        String password = "";

        assertFalse(CustomORM.connect(
                "Impossible Endpoint",
                "Fake username",
                "bad password"
        ));
        assertTrue(CustomORM.connect(enpdoint, username, password));
    }

    @Test
    void buildTable() {
        String tableName = "test_table";
        // String, character
        // float, double
        // boolean
        // byte, short, int, long
        String[] colNames = {
                "name",
                "favorite_letter",
                "net_worth",          // float assumes 2 decimals
                "memorization_of_pi", // double should be more precise
                "alive",
                "age",
                "high_score",
                "computer memory",
                "date_of_birth"
        };
        Class[] dataTypes = {
                String.class,
                Character.class,
                Float.class,
                Double.class,
                Boolean.class,
                Byte.class,
                Short.class,
                Integer.class,
                Long.class
        };
        String newTable = CustomORM.buildTable(
                "uneven array lengths",
                new String[] {"test_col", "extra_col"},
                new Object[] {});
        assertNull(newTable);

        String betterTable = CustomORM.buildTable(
                tableName,
                colNames,
                dataTypes
        );
        assertEquals(tableName, betterTable);
    }

    @Test
    void addRow() {
        String tableName = "AddRowTest";
        String[] colNames = {
                "name",
                "age"
        };
        Class[] dataTypes = {
                String.class,
                Byte.class
        };

        String verifyName = CustomORM.buildTable(tableName, colNames, dataTypes);

        assertEquals(tableName, verifyName);

        int newId = CustomORM.addRow(
                tableName,
                "Hank",
                20
        );

        assertNotNull(newId);
        assertInstanceOf(Integer.class, newId);
        assertTrue(newId > 0);
    }

    @Test
    void getRow() {
        String tableName = "GetRowTest";
        String[] colNames = {
                "name",
                "age"
        };
        Class[] dataTypes = {
                String.class,
                Byte.class
        };

        String verifyName = CustomORM.buildTable(tableName, colNames, dataTypes);
        assertEquals(tableName, verifyName);

        int newId = CustomORM.addRow(
                tableName,
                "Hank",
                20
        );

        ResultSet rs_full = CustomORM.getRow(tableName, newId, new String[] {"*"});
        ResultSet rs_partial = CustomORM.getRow(tableName, newId, new String[]{"name"});

        try {
            // Place both ResultSets on their first response
            assertTrue(rs_full.next());
            assertTrue(rs_partial.next());
            String fetchedName1 = rs_full.getString(1);
            int fetchedAge = rs_full.getInt(2);

            String fetchedName2 = rs_partial.getString(1);
            int fetchedNull = rs_partial.getInt(2);
            assertEquals(20, fetchedAge);
            assertEquals("Hank", fetchedName1);
            assertEquals("Hank", fetchedName2);
        } catch (SQLException e) {
            ORMLogger.logger.info(e.getSQLState());
            ORMLogger.logger.error(e.getStackTrace());
        } catch (Exception i){
            ORMLogger.logger.error(i.getStackTrace());
        }


    }

    @Test
    void updateRow() {
        String tableName = "UpdateRowTest";
        String[] colNames = {
                "name",
                "age"
        };
        Class[] dataTypes = {
                String.class,
                Byte.class
        };

        String verifyName = CustomORM.buildTable(tableName, colNames, dataTypes);
        assertEquals(tableName, verifyName);

        int newId = CustomORM.addRow(
                tableName,
                "Hank",
                20
        );

        ResultSet rs = CustomORM.updateRow(
                tableName,
                newId,
                new String[] {
                        "name",
                        "age"
                },
                new String[] {
                        "Bobby",
                        "12"
                });
        try {
            assertTrue(rs.next());
            String newName = rs.getString(1);
            int newAge = rs.getInt(2);

            assertEquals("Bobby", newName);
            assertEquals(12, newAge);
        } catch (SQLException e) {
            ORMLogger.logger.info(e.getSQLState());
            ORMLogger.logger.error(e.getStackTrace());
        } catch (Exception i){
            ORMLogger.logger.error(i.getStackTrace());
        }
    }

    @Test
    void deleteRow() {
        String tableName = "DeleteRowTest";
        String[] colNames = {
                "name",
                "age"
        };
        Class[] dataTypes = {
                String.class,
                Byte.class
        };

        String verifyName = CustomORM.buildTable(tableName, colNames, dataTypes);
        assertEquals(tableName, verifyName);

        int newId = CustomORM.addRow(
                tableName,
                "Hank",
                20
        );

        ResultSet rs = CustomORM.deleteRow(tableName, newId);
        try {
            assertTrue(rs.next());
            String deletedName = rs.getString(1);
            int deletedAge = rs.getInt(2);

            // Ensure deletion occurred
            rs = CustomORM.getRow(tableName, newId, new String[] {"*"});
            assertFalse(rs.next());
        } catch (SQLException e) {
            ORMLogger.logger.info(e.getSQLState());
            ORMLogger.logger.error(e.getStackTrace());
        } catch (Exception i){
            ORMLogger.logger.error(i.getStackTrace());
        }
    }
}