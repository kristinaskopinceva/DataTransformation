import java.sql.*;

public class Test {
    public static void main(String[] args) throws Exception  {
        Connection connection1 = null;
            try {
                Class.forName("com.microsoft.sqlserver.jdbc.SQLServerDriver");
                 connection1 = DriverManager.getConnection("jdbc:sqlserver://localhost:1433;databaseName=Test;integratedSecurity=true;");
        } catch (Exception e) {
            e.printStackTrace();
        }
        Statement statement = connection1.createStatement();
          // PreparedStatement resultSet = connection1.prepareStatement("INSERT INTO  TEST (FIELD) VALUES  (100)");
                ResultSet resultSet1 = statement.executeQuery("SELECT * FROM TEST");

            while (resultSet1.next()){
            System.out.println(resultSet1.getInt("FIELD"));

        }


    }}