package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.template.AbsLongEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.sql.Date;


@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductPrice extends AbsLongEntity {

    private Double originalPrice;
    private Double sellingPrice;
    private Date changedDate;
    @ManyToOne(fetch = FetchType.EAGER)
    private Product product;
    private boolean valid;

}
