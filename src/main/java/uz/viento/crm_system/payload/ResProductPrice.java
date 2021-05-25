package uz.viento.crm_system.payload;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.Product;

import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResProductPrice {
    private Double originalPrice;
    private Double sellingPrice;
    private Date changedDate;
    private boolean valid;
}
