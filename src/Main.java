
import java.util.Random;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.io.File;
import java.io.IOException;

// SQL \ root \ 100carbook
// Create another branch and make the update instant when creating transactions

public class Main {

    public static void main(String[] args) throws IOException
    {
        Scanner Reader;
        String BankName;
        String Choice;
        BigDecimal Balance;
        BankOverview Bank;
        List<String> ListOfChanges = new ArrayList<String>(); // Print string of the changes
        List<String> ListOfChangesQuery = new ArrayList<String>(); // For query for changes

        // For creating random alphanumeric string
        Random random = new Random();
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        String generatedString;
       
        // Condition to either select first bank unless its empty in which one will be created
        Reader = new Scanner(System.in);
        System.out.println("What is your bank name?");
        BankName = Reader.nextLine();

        System.out.println("What is your current balance?");
        Balance = new BigDecimal(Reader.nextLine());

        Bank = new BankOverview(Balance, BankName, random.toString());
        ListOfChanges.add(BankName + " will be added");
    

        generatedString = random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        ListOfChangesQuery.add(String.format("Insert into bankoverview(Balance, BankName, UID) values (%.2f, + \"%s\", \"%s\")", Balance, BankName, generatedString));

        System.out.println("Bank account has been added.");
        
        
        /* Do a method to switch bank account + add new bank account + clear all + delete*/
        while (true)
        {
            System.out.println("Please choose the following" + System.lineSeparator() + "1.View current account details" + System.lineSeparator() + "2.Add transaction" + System.lineSeparator() + "3.View transaction" + System.lineSeparator() +"4.View ALL transaction" + System.lineSeparator() + "5.Add a new bank account" + System.lineSeparator() + "6.Clear all data" + System.lineSeparator() + "7.Change Bank" + System.lineSeparator() + "8.Update all changes" + System.lineSeparator() +"9.Exit program");
            Choice = Reader.nextLine();
            switch(Choice)
            {
                case "1":
                    Bank.viewDetails();
                    break;

                case "2":
                    System.out.println("What is the name of transaction?");
                    String Name = Reader.nextLine();
                    ListOfChanges.add(String.format("Transaction named \"%s\" for Bank \"%s\"", Name, BankName));
                    ListOfChangesQuery.add(Bank.createTransaction(Name));
                    break;

                case "3":
                    Bank.viewTransaction();
                    break;

                case "4":
                    break;
                
                case "5":
                    break;

                case "6":
                    break;
                
                case "7":
                    System.out.println("What is the bank name you would like to change to?");
                    String NewBankName = Reader.nextLine();
                    Bank = ChangeDefault(NewBankName, Bank.getName(), ListOfChangesQuery, ListOfChanges);
                    break;
                case "8":
                    UpdateSQL(ListOfChangesQuery);
                    break;
                case "9":
                    break;
                default:
                    System.out.println("Invalid choice");
            }

            if (Choice.equals("9"))
            {
                break;
            }
        }
    }


    public static void AddBank () // Add bank without changing the default
    {
        Scanner Reader;
        String BankName;
        BigDecimal Balance;

        Reader = new Scanner(System.in);
        System.out.println("What is your bank provider?");
        BankName = Reader.nextLine();
        Reader.close();

        Reader = new Scanner(System.in);
        System.out.println("What is your current balance?");
        Balance = new BigDecimal(Reader.nextLine());
        Reader.close();

        
        MysqlStatement.SQLInsert(String.format("insert into bankoverview (Balance, BankName, ListOfTransaction, Default) Values( %d, \"%s\", null, null)", Balance, BankName));
        
    
    }

    public static void clearData (File dir) // Dealete ALL data in MYSQL
    {
        
    }

    public static void UpdateSQL(List<String> ListOfChangesQuery) // find a way to revert specific changes + ask if they want to confirm changes
    {
        // find where the statement is printing the sql statment when udpating
        for (String i : ListOfChangesQuery)
        {
            MysqlStatement.SQLInsert(i);
        }

        System.out.println("All updated");
    }

    public static BankOverview ChangeDefault(String bankName, String oldbankName, List<String> ListOfChangesQuery, List<String> ListOfChanges) // return bank overview
    {   
        // Bug\Error must update first then can change default
        // handle this warning when im more advanced 
        List<String> Details = MysqlStatement.SQLView(String.format("Select Balance, BankName, UID From bankoverview where BankName = \"%s\" ", bankName));
        BigDecimal balance = new BigDecimal (Details.get(0));
        String UID = Details.get(2);

        BankOverview bank = new BankOverview(balance, bankName, UID);

        ListOfChanges.add(bankName + " is being replaced as default bank");
        ListOfChangesQuery.add(String.format("Update bankoverview Set DefaultBank = \"true\" Where BankName = \"%s\" ", bankName));
        
        ListOfChanges.add(oldbankName + " is removed as default bank");
        ListOfChangesQuery.add(String.format("Update bankoverview Set DefaultBank = null Where BankName = \"%s\" ", oldbankName));
        
        return bank;

    }

}
