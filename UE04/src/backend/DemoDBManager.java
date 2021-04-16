package backend;

import gradeTable.model.Results;
import gradeTable.model.Student;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

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

    private static final String INSERT_POINTS = "INSERT INTO " + POINTS_TABLE_NAME + " (" + POINTS_STUDENT_ID + ") VALUES (?)";
    private static final int INSERT_POINTS_PARAM_ID = 1;

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
            statement.execute(String.format("DROP TABLE %s", POINTS_TABLE_NAME));
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
            for (int i = 0; i < NR_ASSIGNMENTS; i++) {
                s.append("A").append(i + 1).append(" INTEGER");
                if (i < NR_ASSIGNMENTS - 1) {
                    s.append(",");
                }
            }

            // we create a foreign key to student
            statement.execute(String.format("CREATE TABLE %s ( %s VARCHAR(15) PRIMARY KEY REFERENCES "
                            + STUDENT_TABLE_NAME + "(id), " + s.toString() + ")",
                    POINTS_TABLE_NAME, POINTS_STUDENT_ID));
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public String addStudent(String id, String firstName, String lastName, Integer skz) {
        try {
            PreparedStatement insertStatement = dbConnection.prepareStatement(INSERT_STUDENT,
                    Statement.RETURN_GENERATED_KEYS);
            insertStatement.setString(INSERT_STUDENT_PARAM_ID, id);
            insertStatement.setString(INSERT_STUDENT_PARAM_FIRST_NAME, firstName);
            insertStatement.setString(INSERT_STUDENT_PARAM_LAST_NAME, lastName);
            insertStatement.setInt(INSERT_STUDENT_PARAM_SKZ, skz);


            final int affectedRows = insertStatement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Failed to add new person to database");
            }

            addPoints(id);
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }

        return id;
    }

    private String addPoints(String studentId) {
        PreparedStatement insertStatement = null;
        try {
            insertStatement = dbConnection.prepareStatement(INSERT_POINTS);
            insertStatement.setString(INSERT_POINTS_PARAM_ID, studentId);

            final int affectedRows = insertStatement.executeUpdate();
            if (affectedRows != 1) {
                throw new RuntimeException("Failed to add points for student to database");
            }

            // set points initially to -1
            for (int i = 0; i < NR_ASSIGNMENTS; i++) {
                updatePointsProperty("A" + (i + 1), -1, studentId);
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            // i found in the documentation that it is best practice to close statements afterwards
            if (insertStatement != null) {
                try {
                    insertStatement.close();
                } catch (SQLException ex) {
                    System.out.println("Could not close query");
                }
            }
        }

        return studentId;
    }

    public void updateStudentProperty(String colName, Object val, String studentID) {
        PreparedStatement updateStatement = null;
        try {
            updateStatement = dbConnection
                    .prepareStatement(String.format(UPDATE_STUDENT_PROPERTY, colName));
            if (val instanceof String) {
                updateStatement.setString(UPDATE_STUDENT_PARAM_VALUE, (String) val);
            } else if (val instanceof Integer) {
                updateStatement.setInt(UPDATE_STUDENT_PARAM_VALUE, (Integer) val);
            }
            updateStatement.setString(UPDATE_STUDENT_PARAM_ID, studentID);
            updateStatement.executeUpdate();

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (updateStatement != null) {
                try {
                    updateStatement.close();
                } catch (SQLException ex) {
                    System.out.println("Could not close query");
                }
            }
        }
    }

    public void updatePointsProperty(String colName, Integer val, String studentID) {
        PreparedStatement updateStatement = null;
        try {
            updateStatement = dbConnection.prepareStatement(String.format(UPDATE_POINTS_PROPERTY, colName));
            updateStatement.setInt(UPDATE_POINT_PARAM_VALUE, val);
            updateStatement.setString(UPDATE_STUDENT_PARAM_ID, studentID);
            updateStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (updateStatement != null) {
                try {
                    updateStatement.close();
                } catch (SQLException ex) {
                    System.out.println("Could not close query");
                }
            }
        }
    }

    public void deleteStudent(String studentID) {
        try {
            // if we delete a student we have to delete all ratings
            final PreparedStatement deletePointsStatement = dbConnection.prepareStatement(DELETE_POINTS);
            deletePointsStatement.setString(DELETE_POINTS_PARAM_ID, studentID);
            deletePointsStatement.executeUpdate();

            final PreparedStatement deleteStudentStatement = dbConnection.prepareStatement(DELETE_STUDENT);
            deleteStudentStatement.setString(DELETE_STUDENT_PARAM_ID, studentID);
            deleteStudentStatement.executeUpdate();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }

    public List<Student> importStudents() {
        final List<Student> imported = new ArrayList<>();
        PreparedStatement personQuery = null;

        try {
            // try-statements for auto-closing
            personQuery = dbConnection.prepareStatement(IMPORT_STUDENTS);
            try (final ResultSet items = personQuery.executeQuery()) {
                while (items.next()) {
                    final Student item = new Student(items.getString(STUDENT_ID), items.getString(STUDENT_FIRST_NAME),
                            items.getString(STUDENT_LAST_NAME), items.getInt(STUDENT_SKZ));

                    imported.add(item);
                }
            }
        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (personQuery != null) {
                try {
                    personQuery.close();
                } catch (SQLException ex) {
                    System.out.println("Could not close query");
                }
            }
        }

        return imported;
    }

    public Integer[] selectPoints(String studentId) {
        Integer[] imported = new Integer[NR_ASSIGNMENTS];
        PreparedStatement selectPointsQuery = null;
        try {
            // try-statements for auto-closing
            selectPointsQuery = dbConnection.prepareStatement(SELECT_POINTS_ID);
            selectPointsQuery.setString(POINTS_PARAM_ID, studentId);
            final ResultSet items = selectPointsQuery.executeQuery();

            while (items.next()) {
                for (int i = 0; i < NR_ASSIGNMENTS; i++) {
                    imported[i] = items.getInt("A" + (i + 1));
                }
            }

        } catch (SQLException e) {
            throw new RuntimeException(e);
        } finally {
            if (selectPointsQuery != null) {
                try {
                    selectPointsQuery.close();
                } catch (SQLException ex) {
                    System.out.println("Could not close query");
                }
            }
        }

        return imported;
    }

    /**
     * We map points to students and create Result-Objects
     *
     * @return List of results
     */
    public List<Results> importResults() {
        final List<Results> imported = new ArrayList<>();

        final List<Student> students = this.importStudents();
        students.forEach(s -> {
            final Results result = new Results(s);
            result.setPoints(selectPoints(s.getId()));
            imported.add(result);
        });

        return imported;
    }
}
