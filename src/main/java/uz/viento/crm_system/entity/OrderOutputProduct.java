package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.template.AbsUUIDEntity;

import javax.persistence.*;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderOutputProduct extends AbsUUIDEntity {
    @ManyToOne
    private Product product;
    @ManyToOne
    private ProductPrice productPrice;

    private Double amount;

    private Double totalPrice;






}
