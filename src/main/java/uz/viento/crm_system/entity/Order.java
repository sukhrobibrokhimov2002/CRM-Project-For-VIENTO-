package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.enums.StatusOrder;
import uz.viento.crm_system.entity.template.AbsUUIDEntity;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity(name = "orders")
public class Order extends AbsUUIDEntity {

    private String comment;

    private Timestamp orderDate;

    @ManyToOne
    private CurrencyType currencyType;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    private List<OrderOutputService> orderOutputServiceList;

    @OneToMany(cascade = CascadeType.PERSIST)
    @JoinColumn(name = "order_id")
    private List<OrderOutputProduct> orderOutputProducts;

    @Column(unique = true)
    private String code;

    @ManyToOne
    private User users;

    private String phoneNumber;
    private String address;
    private Double summa;




    @Enumerated(EnumType.STRING)
    private StatusOrder statusOrder;



}
