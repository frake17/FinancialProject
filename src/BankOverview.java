

import java.time.format.TextStyle;
import java.util.Locale;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;
import java.io.Serializable;

public class BankOverview implements Serializable{
    private BigDecimal Balance;
    private String BankName;
    private HashMap<String, List<Transaction>> ListOfTransaction = new HashMap<String, List<Transaction>>();
    private List<Integer> ListOfTransactionID = new ArrayList<Integer>();
    Scanner Reader;
    private Boolean Default;
    private String UID;

    public BankOverview(BigDecimal Balance, String BankName, String UID)
    {
        this.Balance = Balance;
        this.BankName = BankName;
        this.UID = UID;

    }

    private void setBalance(BigDecimal Balance)
    {
        this.Balance = Balance;
    }

    public void viewDetails()
    {
        System.out.println(BankName + Balance + Default);
    }

    public String getUID()
    {
        return UID;
    }

    public String getName()
    {
        return BankName;
    }

    public void ViewAllTransaction()
    {
        for (int UID : ListOfTransactionID)
        {
            MysqlStatement.SQLView("Select * FROM transaction WHERE UID = " + UID);
        }
    }

    public void viewTransaction() // Monthly transaction
    {
        // Error when viewing without updating
        //String formatting error when printing
        String Month;
        String Year;

        Reader = new Scanner(System.in);
        System.out.println("What year of transaction do you like to view(yyyy)?");
        Year = Reader.nextLine();

        Reader = new Scanner(System.in);
        System.out.println("What month of transaction do you like to view(month number)");
        Month = Reader.nextLine();

        List<String> Details = MysqlStatement.SQLView(String.format("Select * FROM transaction WHERE UID = \"%s\" AND month(Date) = \"%s\" AND YEAR(Date) = \"%s\" ", UID, Month, Year));
        List<String> ColumnName = Arrays.asList("UID", "Date", "Cost", "Name");
        for (int i = 1; i<Details.size(); i++)
        {
            System.out.println(ColumnName.get(i) + " " + Details.get(i));
        }
    }

    public String createTransaction(String Name) // Create query string and return 
    {
        String Month;
        String Year;
        String Key;
        LocalDate Transactiondate;
        BigDecimal Cost;
        String Category;
        Transaction NewTransaction;
        List<Transaction> TempList;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Reader = new Scanner(System.in);
        System.out.println("What is the date of transaction(dd/mm/yyyy)?");
        Transactiondate = LocalDate.parse(Reader.nextLine(), formatter);

        Reader = new Scanner(System.in);
        System.out.println("What is the cost of the transaction?");
        Cost = new BigDecimal(Reader.nextLine());

        Reader = new Scanner(System.in);
        System.out.println("What is the category of the transaction(food, work, studies, entertainment, etc)?");
        Category = Reader.nextLine();

        NewTransaction = new Transaction(Category, Transactiondate, Cost, Name);

        Month = Transactiondate.getMonth().getDisplayName(TextStyle.SHORT,Locale.ENGLISH);
        Year = Integer.toString(Transactiondate.getYear());
        Key = Month + " " + Year;

        if (ListOfTransaction.containsKey(Key))
        {
            TempList = ListOfTransaction.get(Key);
            TempList.add(NewTransaction);
        }
        else
        {
            TempList = new ArrayList<Transaction>();
            TempList.add(NewTransaction);
        }
        ListOfTransaction.put(Key.toUpperCase(), TempList);
        setBalance(Balance.subtract(Cost));

        // Insert SQL
        //MysqlStatement.SQLInsert("INSERT INTO Transaction (Category, Date, Cost, Name) VALUES (Category, Key, Cost, Name);");

        ListOfTransactionID.add(NewTransaction.getUID());

        return String.format("INSERT INTO Transaction (Category, Date, Cost, Name, UID) VALUES (\"%s\", \"%s\", %.2f, \"%s\", \"%s\");", Category, Transactiondate, Cost, Name, UID);
    }
}