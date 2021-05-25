package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.enums.OrderOutputStatus;
import uz.viento.crm_system.entity.template.AbsUUIDEntity;

import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class OrderOutputService extends AbsUUIDEntity {
    @ManyToOne
    private Service service;

    private Double totalPrice;
    @Enumerated(EnumType.STRING)
    private OrderOutputStatus orderOutputStatus;

}
