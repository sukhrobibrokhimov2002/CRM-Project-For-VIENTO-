package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Date;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddAdditionalProductDto {
    private Double amount;
    private Double sellingPrice;
    private Date expireDate;
    private Double originalPrice;


}
