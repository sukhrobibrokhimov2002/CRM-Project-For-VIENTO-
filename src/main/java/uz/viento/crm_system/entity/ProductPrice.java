package uz.viento.crm_system.entity;


import lombok.*;
import net.minidev.json.annotate.JsonIgnore;
import uz.viento.crm_system.entity.template.AbsLongEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import java.sql.Date;


@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class ProductPrice extends AbsLongEntity {

    private Double originalPrice;
    private Double sellingPrice;
    private Date changedDate;
    @ManyToOne(fetch = FetchType.LAZY)
    @JsonIgnore
    private Product product;
    private boolean valid;

}
