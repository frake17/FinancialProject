import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.UUID;

public class Income {
    private String Category;
    private LocalDate Date;
    private BigDecimal Cost;
    private String Name;
    private String UID;

    public Income(String Category, LocalDate Date, BigDecimal Cost, String Name)
    {
        this.Category = Category;
        this.Date = Date;
        this.Cost = Cost;
        this.Name = Name;
        this.UID = "I" + UUID.randomUUID().toString().replace("-", "");
    }

    public String getCategory()
    {
        return Category;
    }

    public String getUID()
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
