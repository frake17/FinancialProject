package FinancialProject;

import java.time.format.TextStyle;
import java.util.Locale;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import java.util.List;
import java.io.Serializable;

public class BankOverview implements Serializable{
    private BigDecimal Balance;
    private String BankName;
    private HashMap<String, List<Transaction>> ListOfTransaction = new HashMap<String, List<Transaction>>();
    Scanner Reader;
    private Boolean Default;
    private String UID;

    public BankOverview(BigDecimal Balance, String BankName)
    {
        this.Balance = Balance;
        this.BankName = BankName;

    }

    private void setBalance(BigDecimal Balance)
    {
        this.Balance = Balance;
    }

    public String viewDetails()
    {
        return String.format("Current balance: %s, bank: %s", Balance, BankName);
    }

    public void setUID(String UID)
    {
        this.UID = UID;
    }

    public String getUID()
    {
        return UID;
    }

    public String viewTransaction()
    {
        String Month;
        String Year;
        String Key;
        List<Transaction> TempList;
        String TempString = "";

        Reader = new Scanner(System.in);
        System.out.println("What year of transaction do you like to view(yyyy)?");
        Year = Reader.nextLine();

        Reader = new Scanner(System.in);
        System.out.println("What month of transaction do you like to view(month name)");
        Month = Reader.nextLine();

        Key = Month.toUpperCase() + " " + Year;
        TempList = ListOfTransaction.get(Key);
        System.out.println(ListOfTransaction);
        System.out.println(Key);

        for (int i = 0; i < TempList.size(); i++)  
        {
            TempString = TempString + "Transaction named " + TempList.get(i).getName() + " under " + TempList.get(i).getCategory() + " costed " + TempList.get(i).getCost();
        }

        return TempString;

    }

    public String createTransaction()
    {
        String Month;
        String Data = "";
        String Year;
        String Key;
        LocalDate Transactiondate;
        BigDecimal Cost;
        String Category;
        String Name;
        Transaction NewTransaction;
        List<Transaction> TempList;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd/MM/yyyy");

        Reader = new Scanner(System.in);
        System.out.println("What is the name of transaction?");
        Name = Reader.nextLine();

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




        return "Transaction is logged.";
    }
}
