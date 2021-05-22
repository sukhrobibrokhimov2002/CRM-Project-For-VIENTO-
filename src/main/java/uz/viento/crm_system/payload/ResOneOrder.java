package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Timestamp;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResOneOrder {
    private String comment;
    private Timestamp orderDate;
    private String currencyAbbreviation;
    private String username;
    private String userPhoneNumber;
    private String orderStatus;
    private List<ResServiceOrProduct> productNames;
}
