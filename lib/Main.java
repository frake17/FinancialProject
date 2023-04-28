package FinancialProject;

import java.math.BigDecimal;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Scanner;
import java.util.stream.Stream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.ObjectInputStream;

// SQL \ root \ 100carbook

public class Main {

    public static void main(String[] args) throws IOException
    {
        Scanner Reader;
        String BankName;
        String Choice;
        BigDecimal Balance;
        BankOverview Bank;
       
        Reader = new Scanner(System.in);
        System.out.println("What is your bank provider?");
        BankName = Reader.nextLine();
        Reader.close();

        Reader = new Scanner(System.in);
        System.out.println("What is your current balance?");
        Balance = new BigDecimal(Reader.nextLine());
        Reader.close();

        Bank = new BankOverview(Balance, BankName);
        System.out.println("Bank account has been added.");
        
        
        /* Do a method to switch bank account + add new bank account + clear all + delete*/
        while (true)
        {
            Reader = new Scanner(System.in);
            System.out.println("Please press the following" + System.lineSeparator() + "1.View current account details" + System.lineSeparator() + "2.Add transaction" + System.lineSeparator() + "3.View transaction" + System.lineSeparator() + "4.Add a new bank account" + System.lineSeparator() + "6.Clear all data" + System.lineSeparator() + "7.Save changes" + System.lineSeparator() + "8. Set degault Bank" + System.lineSeparator() +"9.Exit program");
            Choice = Reader.nextLine();
            switch(Choice)
            {
                case "1":
                    System.out.println(Bank.viewDetails());
                    break;

                case "2":
                    System.out.println(Bank.createTransaction());
                    break;

                case "3":
                    System.out.println(Bank.viewTransaction());
                    break;

                case "4":
                    break;
                
                case "5":
                    break;

                case "6":
                    clearData(new File("FinancialProject/Database/"));
                    break;
                
                case "7":
                    writeData("FinancialProject/Database/Database.dat", Bank);
                    break;
                
                case "8":
                    break;
                
                default:
                    System.out.println("Invalid choice");
            }

            if (Choice.equals("8"))
            {
                break;
            }
        }
    }

    public static Object readObjectFromDB (String path)
    {
        try
        {
            FileInputStream ReadFile = new FileInputStream(path);
            ObjectInputStream NewObject = new ObjectInputStream(ReadFile);

            Object obj = NewObject.readObject();
            NewObject.close();
            return obj;
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            return null;
        }
    }

    public static void writeData (String path, BankOverview Bank)
    {
        
    }

    public static void clearData (File dir)
    {
        for(File file: dir.listFiles()) 
            if (!file.isDirectory()) 
                file.delete();
        System.out.println("All data has been erased. Please exit the program to finalize the deletion of data.");
    }

    public static boolean isEmpty(Path path) throws IOException
    {
        if (Files.isDirectory(path))
        {
            try (Stream<Path> entries = Files.list(path)) {
                return !entries.findFirst().isPresent();
            }
        }
        return false;
    }
}
