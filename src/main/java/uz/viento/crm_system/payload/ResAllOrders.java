package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import java.sql.Timestamp;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResAllOrders {
    private String comment;
    private Timestamp orderDate;
    private String currencyAbbreviation;
    private String username;
    private String userPhoneNumber;
    private String orderStatus;
}
