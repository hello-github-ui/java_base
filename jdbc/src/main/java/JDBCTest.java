import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.Statement;

/**
 * @author 030
 * @date 14:54 2021/11/8
 * @description
 */
public class JDBCTest {
    private static final String url = "jdbc:mysql://localhost:3306/test";
    private static final String username = "root";
    private static final String password = "123456";


    public static void main(String[] args) {

        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection connection = DriverManager.getConnection(url, username, password);
            Statement st = connection.createStatement();
            ResultSet rs = st.executeQuery("select  * from user ");
            while (rs.next()){
//                System.out.println("查询到数据了" + rs.toString());
                System.out.println(rs.getInt(1) + " " + rs.getString(2) + " " + rs.getString(3));
            }
        }catch (Exception e){
            throw new RuntimeException(e.getCause());
        }

    }
}
