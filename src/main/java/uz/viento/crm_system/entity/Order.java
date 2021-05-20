package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.enums.StatusOrder;
import uz.viento.crm_system.entity.template.AbsUUIDEntity;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.OneToMany;
import java.sql.Timestamp;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "orders")
public class Order extends AbsUUIDEntity {

    private String name;

    private Timestamp orderDate;

    @ManyToOne
    private CurrencyType currencyType;

    @Column(unique = true)
    private String code;

    @ManyToOne
    private User users;

    private Double summa;

    private boolean payed;

    private StatusOrder statusOrder;



}
