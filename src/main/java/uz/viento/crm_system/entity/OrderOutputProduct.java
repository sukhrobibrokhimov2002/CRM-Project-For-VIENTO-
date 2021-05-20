package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.template.AbsUUIDEntity;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderOutputProduct extends AbsUUIDEntity {
    @ManyToOne
    private Service service;

    @ManyToOne
    private Product product;

    private Double amount;

    private Double totalPrice;

    @ManyToOne
    private Order order;
}
