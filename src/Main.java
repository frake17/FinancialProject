import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;
import java.util.UUID;
import java.io.IOException;

// SQL \ root \ 100carbook
// Create another branch and make the update instant when creating transactions

public class Main {
    public static Scanner Reader;

    public static void main(String[] args) throws IOException
    {
        String BankName;
        String Choice;
        BigDecimal Balance;
        BankOverview Bank;
        List<String> ListOfChanges = new ArrayList<String>(); // Print string of the changes
        List<String> ListOfChangesQuery = new ArrayList<String>(); // For query for changes
        List<String> Bankdetails = new ArrayList<String>();

        String generatedString;
       
        // Condition to either select first bank unless its empty in which one will be created
        Bankdetails = MysqlStatement.SQLView("Select Balance, BankName, UID From bankoverview where DefaultBank = \"True\" ");
        Reader = new Scanner(System.in);

        if (Bankdetails.isEmpty()) // condition to prevent empty inputs + auto default bank // Needs to loop thru Bankdetails!!
        {
        generatedString = UUID.randomUUID().toString().replace("-", "");
        System.out.println("What is your bank name?");
        BankName = Reader.nextLine();

        System.out.println("What is your current balance?");
        Balance = new BigDecimal(Reader.nextLine());

        Bank = new BankOverview(Balance, BankName, UUID.randomUUID().toString().replace("-", ""));
        ListOfChanges.add(BankName + " will be added");
    
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
            System.out.println("Please choose the following" + System.lineSeparator() + "1.View current account details" + System.lineSeparator() + "2.Add transaction" + System.lineSeparator() + "3.View/Delete transaction by month" + System.lineSeparator() +"4.View ALL transaction" + System.lineSeparator() + "5.Add a new bank account" + System.lineSeparator() + "6.Clear all data" + System.lineSeparator() + "7.Change Bank" + System.lineSeparator() + "8.Update all changes" + System.lineSeparator() + "9.View/Delete pending changes" + System.lineSeparator() + "10.Add income" + System.lineSeparator() + "11.View Income by month" + System.lineSeparator() + "12.View/Delete All Income" + System.lineSeparator() + "13. View/Delete Bank" + System.lineSeparator() +"14.Exit program");
            Choice = Reader.nextLine();
            switch(Choice)
            {
                case "1":
                    Bank.viewDetails();
                    break;

                case "2":
                    System.out.println("What is the name of transaction?");
                    String Name = Reader.nextLine();
                    Transaction newTransaction = Bank.createTransaction(Name);
                    ListOfChanges.add(String.format("Transaction named \"%s\" for Bank \"%s\" costed = \"%.2f\" ", Name, BankName, newTransaction.getCost()));
                    ListOfChangesQuery.add(String.format("INSERT INTO Transaction (Category, Date, Cost, Name, UID, BankName) VALUES (\"%s\", \"%s\", %.2f, \"%s\", \"%s\", \"%s\");", newTransaction.getCategory(), newTransaction.getDate().toString(), newTransaction.getCost(), newTransaction.getCategory(), Name, newTransaction.getUID(), BankName));
                    break;

                case "3":
                    Bank.viewTransaction();
                    break;

                case "4":
                    Bank.ViewAllTransaction();
                    break;
                
                case "5":
                    generatedString = UUID.randomUUID().toString().replace("-", "");
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
                    UpdateSQL(ListOfChanges, ListOfChangesQuery, confirm, BankName);
                    break;
                case "9":
                    ViewPendingUpdate(ListOfChanges, ListOfChangesQuery);
                    break;
                case "10":
                    System.out.println("What is the name of Income?");
                    Name = Reader.nextLine();
                    Income newIncome = Bank.createIncome(Name);
                    ListOfChanges.add(String.format("Income named \"%s\" for Bank \"%s\" for = \"%.2f\" ", Name, BankName, newIncome.getCost()));
                    ListOfChangesQuery.add(String.format("INSERT INTO Income (Category, Date, Cost, Name, UID, BankName) VALUES (\"%s\", \"%s\", %.2f, \"%s\", \"%s\", \"%s\");", newIncome.getCategory(), newIncome.getDate(), newIncome.getCost(), Name, newIncome.getUID(), BankName));
                    break;
                case "11":
                    Bank.ViewAllIncome();
                    break;
                case "12":
                    Bank.viewIncome();
                    break;
                case "13":
                    viewbank();
                    break;
                case "14":
                    break;
                default:
                    System.out.println("Invalid choice");
            }

            if (Choice.equals("14"))
            {
                Reader.close();
                break;
            }
            
        }
    }

    public static void viewbank(){
        String Name;
        List<String> DeleteBankList = MysqlStatement.SQLView("Select * From bankoverview");
        for (String string : DeleteBankList) {
            System.out.println(string);
        }

        System.out.println("Enter yes to delete any updates");
        if (Reader.nextLine().toLowerCase() == "yes"){
            System.out.println("Please enter the name of the bank you want to delete");
            Name = Reader.nextLine();
            DeleteBank(Name);
        }
    }

    public static void DeleteBank(String BankName)
    {
        MysqlStatement.SQLInsert(String.format("DELETE FROM bankoverview WHERE bankname = \"%s\" ", BankName));
        System.out.println(BankName + " has been deleted.");

        System.out.println("Enter stop once done deleted the needed update");
        Scanner Reader = new Scanner(System.in);
        // do invalidation
        if (Reader.nextLine().toLowerCase() != "stop"){
            System.out.println("Enter the bank name that you wish to edit");
            BankName = Reader.nextLine();
            DeleteBank(BankName);
        }
    }

    public static void AddBank (List<String> ListOfChangesQuery, List<String> ListOfChanges, String UID) // Add bank without changing the default
    {
        String BankName;
        BigDecimal Balance;

        Reader = new Scanner(System.in);
        System.out.println("What is your bank provider?");
        BankName = Reader.nextLine();

        System.out.println("What is your current balance?");
        Balance = new BigDecimal(Reader.nextLine());

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

    public static void UpdateSQL(List<String> ListOfChanges, List<String> ListOfChangesQuery, String confirm, String BankName) // find a way to revert specific changes
    {
        Double Cost = 0.00;
        if(confirm.toLowerCase().equals("y"))
        {
            for (String i : ListOfChangesQuery)
            {
                MysqlStatement.SQLInsert(i);
            }

            for (String i : ListOfChanges)
            {
                if (i.contains("Transaction"))
                {
                    Cost = Cost +  Double.parseDouble(i.substring(i.lastIndexOf("=")+3, i.length()-2));
                }
                else if (i.contains("Income")) {
                    Cost = Cost -  Double.parseDouble(i.substring(i.lastIndexOf("=")+3, i.length()-2));
                } 
            }

            MysqlStatement.SQLInsert(String.format("UPDATE Bankoverview set balance = \"%.2f\" Where BankName = \"%s\" ", Cost, BankName));

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
        int counter = 0;
        for (String string : ListOfChanges) {
            System.out.println(counter + " " +string);
            counter += 1;
        }
        System.out.println("Enter yes to delete any updates");
        if (Reader.nextLine().toLowerCase().equals("yes"))
        {
            // do invalidation
            System.out.println("Enter the query number that you wish to edit");
            QueryNumber = Integer.parseInt(Reader.nextLine());
            DeleteUpdate(ListOfChanges, ListOfChangesQuery, QueryNumber);
        }
    }

    public static void DeleteUpdate(List<String> ListOfChanges, List<String> ListOfChangesQuery, int QueryNumber)
    {
        ListOfChanges.remove(QueryNumber);
        ListOfChangesQuery.remove(QueryNumber);

        System.out.println("Enter stop once done deleted the needed update");
        // do invalidation
        if (!Reader.nextLine().toLowerCase().equals("stop")){
            System.out.println("Enter the query number that you wish to edit");
            QueryNumber = Integer.parseInt(Reader.nextLine());
            DeleteUpdate(ListOfChanges, ListOfChangesQuery, QueryNumber);
        }
    }   

}
