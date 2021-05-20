package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import uz.viento.crm_system.entity.template.AbsLongEntity;

import javax.persistence.Entity;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class CurrencyType extends AbsLongEntity {
    private String name;
    private String abbreviation;
}
