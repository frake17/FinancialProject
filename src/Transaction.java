

import java.math.BigDecimal;
import java.time.LocalDate;

public class Transaction {
    private String Category;
    private LocalDate Date;
    private BigDecimal Cost;
    private String Name;
    private int UID;

    public Transaction(String Category, LocalDate Date, BigDecimal Cost, String Name)
    {
        this.Category = Category;
        this.Date = Date;
        this.Cost = Cost;
        this.Name = Name;
    }

    public String getCategory()
    {
        return Category;
    }

    public int getUID()
    {
        return UID;
    }

    public LocalDate getDate()
    {
        return Date;
    }

    public BigDecimal getCost()
    {
        return Cost;
    }

    public String getName()
    {
        return Name;
    }
}
