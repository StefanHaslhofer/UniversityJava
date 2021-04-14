package backend;

import java.sql.Connection;
import java.sql.DatabaseMetaData;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;

import gradeTable.model.Student;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;

import static gradeTable.model.Results.NR_ASSIGNMENTS;

public class DemoDBManager {

    // DB QUERY STRINGS
    private static final String DB_URL = "jdbc:derby:resultsDB;create=true";
    private static final String STUDENT_TABLE_NAME = "Student";
    private static final String STUDENT_ID = "Id";
    private static final String STUDENT_FIRST_NAME = "First_Name";
    private static final String STUDENT_LAST_NAME = "Last_Name";
    private static final String STUDENT_SKZ = "Skz";

    private static final String IMPORT_STUDENTS = "SELECT * from " + STUDENT_TABLE_NAME;

    private static final String SELECT_STUDENTS_ID = "SELECT * from " + STUDENT_TABLE_NAME + " WHERE " + STUDENT_ID + "=?";
    private static final int STUDENT_PARAM_ID = 1;

    private static final String INSERT_STUDENT = "INSERT INTO " + STUDENT_TABLE_NAME +
            " (" + STUDENT_ID + ", " + STUDENT_FIRST_NAME + ", " + STUDENT_LAST_NAME + ", " + STUDENT_SKZ + ") VALUES(?,?,?,?)";
    private static final int INSERT_STUDENT_PARAM_ID = 1;
    private static final int INSERT_STUDENT_PARAM_FIRST_NAME = 2;
    private static final int INSERT_STUDENT_PARAM_LAST_NAME = 3;
    private static final int INSERT_STUDENT_PARAM_SKZ = 4;

    private static final String UPDATE_STUDENT_PROPERTY = "UPDATE " + STUDENT_TABLE_NAME + " SET %s=? WHERE "
            + STUDENT_ID + "=?";
    private static final int UPDATE_STUDENT_PARAM_VALUE = 1;
    private static final int UPDATE_STUDENT_PARAM_ID = 2;

    private static final String DELETE_STUDENT = "DELETE FROM " + STUDENT_TABLE_NAME + " WHERE "
            + STUDENT_ID + "=?";
    private static final int DELETE_STUDENT_PARAM_ID = 1;



    private static final String POINTS_TABLE_NAME = "Points";
    private static final String POINTS_STUDENT_ID = "StudentId";

    private static final String IMPORT_POINTS = "SELECT * from " + POINTS_TABLE_NAME;

    private static final String SELECT_POINTS_ID = "SELECT * from " + POINTS_TABLE_NAME + " WHERE " + POINTS_STUDENT_ID + "=?";
    private static final int POINTS_PARAM_ID = 1;

    private static final String UPDATE_POINTS_PROPERTY = "UPDATE " + POINTS_TABLE_NAME + " SET %s=? WHERE "
            + POINTS_STUDENT_ID + "=?";
    private static final int UPDATE_POINT_PARAM_VALUE = 1;
    private static final int UPDATE_POINT_PARAM_ID = 2;

    private static final String DELETE_POINTS = "DELETE FROM " + POINTS_TABLE_NAME + " WHERE "
            + POINTS_STUDENT_ID + "=?";
    private static final int DELETE_POINTS_PARAM_ID = 1;


    // DBManager fields
    private Connection dbConnection;

    public DemoDBManager() {
        dbConnection = null;
    }

    public void printMetadata() throws SQLException {
        DatabaseMetaData metaData = dbConnection.getMetaData();
        ResultSet tableMetaData = metaData.getTables(null, null, null, null);
        while (tableMetaData.next()) {
            System.out.println(tableMetaData.getString("TABLE_NAME") + ", " + tableMetaData.getString("TABLE_TYPE")
                    + ", " + tableMetaData.getString("TABLE_SCHEM"));
        }
    }

    public void openConnection(boolean newDb) throws SQLException {
        if (dbConnection != null && !dbConnection.isClosed()) {
            throw new SQLException("DB Session not closed");
        }

        try {
            dbConnection = DriverManager.getConnection(DB_URL);
        } catch (SQLException e) {
            e.printStackTrace();
        }

        if (newDb) {
            deleteTables();
            createTables();
        }
    }

    public void closeConnection() {
        if (dbConnection == null) {
            throw new IllegalStateException("Connection was already closed");
        }

        try {
            dbConnection.close();
        } catch (SQLException e) {
            throw new RuntimeException("Could not close database", e);
        }
    }

    private void deleteTables() {
        try (Statement statement = dbConnection.createStatement()) {
            statement.execute(String.format("DROP TABLE %s", STUDENT_TABLE_NAME));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    private void createTables() {

        try (Statement statement = dbConnection.createStatement()) {
            statement.execute(String.format("CREATE TABLE %s ("
                            + "%s VARCHAR(15) PRIMARY KEY, "
                            + "%s VARCHAR(15), "
                            + "%s VARCHAR(30), "
                            + "%s INTEGER)",
                    STUDENT_TABLE_NAME, STUDENT_ID, STUDENT_FIRST_NAME, STUDENT_LAST_NAME, STUDENT_SKZ));
        } catch (SQLException e) {
            e.printStackTrace();
        }

        try (Statement statement = dbConnection.createStatement()) {
            StringBuilder s = new StringBuilder();

            // append columns for each
            for(int i = 0; i<NR_ASSIGNMENTS; i++) {
                s.append("A").append(i+1).append(" INTEGER");
                if(i<NR_ASSIGNMENTS-1) {
                    s.append(",");
                }
            }

            statement.execute(String.format("CREATE TABLE %s (" + s.toString() + ")",
                    POINTS_TABLE_NAME));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String addStudent(String id, String firstName, String lastName, Integer skz) {
        try (PreparedStatement insertStatement = dbConnection.prepareStatement(INSERT_STUDENT,
                Statement.RETURN_GENERATED_KEYS)) {

            insertStatement.setString(INSERT_STUDENT_PARAM_ID, id);
            insertStatement.setString(INSERT_STUDENT_PARAM_FIRST_NAME, firstName);
            insertStatement.setString(INSERT_STUDENT_PARAM_LAST_NAME, lastName);
            insertStatement.setInt(INSERT_STUDENT_PARAM_SKZ, skz);

            final int affectedRows = insertStatement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Failed to add new person to database");
            }

            // return created person ID (primary key)
            try (ResultSet generatedKeys = insertStatement.getGeneratedKeys()) {
                generatedKeys.next();
                return generatedKeys.getString(1);
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    public void updateProperty(String colName, Object val, String studentID) {
        try (final PreparedStatement updateStatement = dbConnection
                .prepareStatement(String.format(UPDATE_STUDENT_PROPERTY, colName))) {
            if (val instanceof String) {
                updateStatement.setString(UPDATE_STUDENT_PARAM_VALUE, (String) val);
            } else if (val instanceof Integer) {
                updateStatement.setInt(UPDATE_STUDENT_PARAM_VALUE, (Integer) val);
            }
            updateStatement.setString(UPDATE_STUDENT_PARAM_ID, studentID);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public void deleteStudent(String studentID) {
        try (final PreparedStatement deleteStatement = dbConnection
                .prepareStatement(DELETE_STUDENT)) {

            deleteStatement.setString(DELETE_STUDENT_PARAM_ID, studentID);
            deleteStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Student> importStudents() {
        final List<Student> imported = new ArrayList<>();

        try {
            // try-statements for auto-closing
            try (PreparedStatement personQuery = dbConnection.prepareStatement(IMPORT_STUDENTS)) {
                try (final ResultSet items = personQuery.executeQuery()) {
                    while (items.next()) {
                        final Student item = new Student(items.getString(STUDENT_ID), items.getString(STUDENT_FIRST_NAME),
                                items.getString(STUDENT_LAST_NAME), items.getInt(STUDENT_SKZ));

                        item.firstNameProperty().addListener(new ChangeListener<String>() {

                            @Override
                            public void changed(ObservableValue<? extends String> observable, String oldValue, String newValue) {
                                updateProperty(STUDENT_FIRST_NAME, newValue, item.getId());
                            }

                        });
                        imported.add(item);
                    }
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return imported;
    }
}
