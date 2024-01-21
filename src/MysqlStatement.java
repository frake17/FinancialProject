import java.sql.*;
import java.util.List;
import java.util.ArrayList;

public class MysqlStatement {
    public void main(String[] args) throws Exception {
    }
    public static void SQLInsert(String query){ //test if can be used for update as well and delete
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/financialproject", "root", "100carbook");
            Statement stmt = con.createStatement();
            int rs = stmt.executeUpdate(query);
            con.close();
            System.out.println(rs);
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    // handle warning when im more advanced
    public static List SQLView(String query) // For all Select purposes
    {
        List<String> Details = new ArrayList<String>();
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/FinancialProject", "root", "100carbook");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            ResultSetMetaData rsmd = rs.getMetaData();
            int columnsNumber = rsmd.getColumnCount();
            while (rs.next())
            {
                for (int i = 1; i <= columnsNumber; i++)
                {
                    //System.out.println(rs.getString(i));
                    Details.add(rs.getString(i));
                }
            }
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }

        return Details;
    }

    /**
    public static void SQLViewTransaction(String UID)
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/FinancialProject", "root", "100carbook");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("Select * FROM transaction WHERE UID = " + UID);
            while (rs.next())
            {
                System.out.println(rs);
            }
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

     public static void SQLViewMontlyTransaction(String UID, String Month, String Year)
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/FinancialProject", "root", "100carbook");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("Select * FROM transaction WHERE UID = " + UID + "month(Date) = " + Month + "YEAR(Date) = " + Year);
            while (rs.next())
            {
                System.out.println(rs);
            }
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }

    public static void SQLViewBank(String UID)
    {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://localhost:3306/FinancialProject", "root", "100carbook");
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("Select Balance, BankName FROM bankoverciew WHERE UID = " + UID);
            while (rs.next())
            {
                System.out.println(rs);
            }
            con.close();
        }
        catch(Exception e)
        {
            System.out.println(e);
        }
    }
    **/
}
