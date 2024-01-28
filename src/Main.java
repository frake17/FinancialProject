
import java.util.Random;
import java.math.BigDecimal;
import java.rmi.server.UID;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
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
        List<String> Bankdetails = new ArrayList<String>();

        // For creating random alphanumeric string
        Random random = new Random();
        int leftLimit = 48; // numeral '0'
        int rightLimit = 122; // letter 'z'
        int targetStringLength = 10;
        String generatedString;
       
        // Condition to either select first bank unless its empty in which one will be created
        Bankdetails = MysqlStatement.SQLView("Select Balance, BankName, UID From bankoverview where DefaultBank = \"True\" ");
        Reader = new Scanner(System.in);

        if (Bankdetails.isEmpty()) // condition to prevent empty inputs + auto default bank
        {
        System.out.println("What is your bank name?");
        BankName = Reader.nextLine();

        System.out.println("What is your current balance?");
        Balance = new BigDecimal(Reader.nextLine());

        Bank = new BankOverview(Balance, BankName, random.toString());
        ListOfChanges.add(BankName + " will be added");
    
        /* See if can change to UID generator used in transaction */
        generatedString = random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
        ListOfChangesQuery.add(String.format("Insert into bankoverview(Balance, BankName, UID) values (%.2f, + \"%s\", \"%s\")", Balance, BankName, generatedString));
        ListOfChangesQuery.add(String.format("Update bankoverview set DefaultBank = \"True\" Where UID = \"%s\" ", generatedString));

        System.out.println("Bank account has been added.");
        }
        else
        {
            BigDecimal balance = new BigDecimal (Bankdetails.get(0));
            BankName = Bankdetails.get(1);
            Bank = new BankOverview(balance, BankName, Bankdetails.get(2));
        }
        
        
        /*Do a delete but for specific bank or transaction*/
        while (true)
        {
            System.out.println("Please choose the following" + System.lineSeparator() + "1.View current account details" + System.lineSeparator() + "2.Add transaction" + System.lineSeparator() + "3.View transaction" + System.lineSeparator() +"4.View ALL transaction" + System.lineSeparator() + "5.Add a new bank account" + System.lineSeparator() + "6.Clear all data" + System.lineSeparator() + "7.Change Bank" + System.lineSeparator() + "8.Update all changes" + System.lineSeparator() + "9.View/Delete pending changes" + System.lineSeparator() + "10.Add income" + System.lineSeparator() + "11.View Income" + System.lineSeparator() + "12.VIew All Income" + System.lineSeparator() +"13.Exit program");
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
                    Bank.ViewAllTransaction();
                    break;
                
                case "5":
                    generatedString = random.ints(leftLimit, rightLimit + 1).filter(i -> (i <= 57 || i >= 65) && (i <= 90 || i >= 97)).limit(targetStringLength).collect(StringBuilder::new, StringBuilder::appendCodePoint, StringBuilder::append).toString();
                    AddBank(ListOfChangesQuery, ListOfChangesQuery, generatedString);
                    break;

                case "6":
                    clearData(ListOfChangesQuery, ListOfChangesQuery);
                    break;
                
                case "7":
                    System.out.println("What is the bank name you would like to change to?");
                    String NewBankName = Reader.nextLine();
                    Bank = ChangeDefault(NewBankName, Bank.getName(), ListOfChangesQuery, ListOfChanges);
                    break;
                case "8":
                    System.out.println("Press 'Y' to confrim update.");
                    String confirm = Reader.nextLine();
                    UpdateSQL(ListOfChangesQuery, confirm);
                    break;
                case "9":
                    break;
                case "10":
                    System.out.println("What is the name of Income?");
                    Name = Reader.nextLine();
                    ListOfChanges.add(String.format("Income named \"%s\" for Bank \"%s\"", Name, BankName));
                    ListOfChangesQuery.add(Bank.createIncome(Name));
                    break;
                case "11":
                    Bank.ViewAllIncome();
                    break;
                case "12":
                    Bank.viewIncome();
                    break;
                case "13":
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

    public static void AddBank (List<String> ListOfChangesQuery, List<String> ListOfChanges, String UID) // Add bank without changing the default
    {
        Scanner Reader;
        String BankName;
        BigDecimal Balance;

        Reader = new Scanner(System.in);
        System.out.println("What is your bank provider?");
        BankName = Reader.nextLine();

        System.out.println("What is your current balance?");
        Balance = new BigDecimal(Reader.nextLine());

        Reader.close();

        ListOfChanges.add("Inserting bank record.");
        ListOfChangesQuery.add(String.format("insert into bankoverview (Balance, BankName, ListOfTransaction, DefaultBank, UID) Values( %.2f, \"%s\", null, null, \"%s\" )", Balance, BankName,UID));
    }

    public static void clearData (List<String> ListOfChangesQuery, List<String> ListOfChanges) // Dealete ALL data in MYSQL
    {
        ListOfChanges.add("Clearing bank records.");
        ListOfChangesQuery.add("Delete from bankoverview");

        ListOfChanges.add("Clearing transaction records");
        ListOfChangesQuery.add("Delete from transaction");
    }

    public static void UpdateSQL(List<String> ListOfChangesQuery, String confirm) // find a way to revert specific changes
    {
        // find where the statement is printing the sql statment when udpating
        if(confirm.toLowerCase() == "y")
        {
            for (String i : ListOfChangesQuery)
            {
                MysqlStatement.SQLInsert(i);
            }

            System.out.println("All updated");
        }
        else
        {
            System.out.println("Updated not appliled.");
        }
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

    public static void ViewPendingUpdate(List<String> ListOfChanges, List<String> ListOfChangesQuery)
    {   
        int QueryNumber;
        Scanner Reader = new Scanner(System.in);
        int counter = 0;
        for (String string : ListOfChanges) {
            System.out.println(counter + " " +string);
            counter += 1;
        }
        System.out.println("Enter yes to delete any updates");
        if (Reader.nextLine().toLowerCase() == "yes")
        {
            // do invalidation
            System.out.println("Enter the query number that you wish to edit");
            QueryNumber = Integer.parseInt(Reader.nextLine());
            DeleteUpdate(ListOfChanges, ListOfChangesQuery, QueryNumber);
        }
        Reader.close();
    }

    public static void DeleteUpdate(List<String> ListOfChanges, List<String> ListOfChangesQuery, int QueryNumber)
    {
        ListOfChanges.remove(QueryNumber);
        ListOfChangesQuery.remove(QueryNumber);

        System.out.println("Enter stop once done deleted the needed update");
        Scanner Reader = new Scanner(System.in);
        // do invalidation
        if (Reader.nextLine().toLowerCase() != "stop"){
            System.out.println("Enter the query number that you wish to edit");
            QueryNumber = Integer.parseInt(Reader.nextLine());
            DeleteUpdate(ListOfChanges, ListOfChangesQuery, QueryNumber);
        }
        Reader.close();
    }

}
