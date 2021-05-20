package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import uz.viento.crm_system.entity.enums.Status;
import uz.viento.crm_system.entity.template.AbsLongEntity;

import javax.persistence.*;
import java.sql.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class PeopleWhoCalled extends AbsLongEntity {
    @ManyToOne
    private User users;
    @ManyToMany
    private List<Service> service;
    @ManyToMany
    private List<Product> product;

    private String phoneNumber;

    private Date calledDate;
    private Date whenShouldCall;

    private String comment;

    @Enumerated(EnumType.STRING)
    private Status status;
}
