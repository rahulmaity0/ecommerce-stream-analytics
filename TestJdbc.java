import java.sql.*;
public class TestJdbc {
  public static void main(String[] args) throws Exception {
    Class.forName("org.postgresql.Driver");
    try (Connection c = DriverManager.getConnection("jdbc:postgresql://localhost:5433/analytics", "analytics_user", "analytics_pass")) {
      System.out.println("CONNECTED " + c.getMetaData().getURL());
      try (Statement s = c.createStatement(); ResultSet rs = s.executeQuery("select current_user, current_database()")) {
        while (rs.next()) {
          System.out.println(rs.getString(1) + " " + rs.getString(2));
        }
      }
    }
  }
}
