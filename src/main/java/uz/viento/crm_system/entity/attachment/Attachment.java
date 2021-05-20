package uz.viento.crm_system.entity.attachment;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.Product;
import uz.viento.crm_system.entity.template.AbsEntity;
import uz.viento.crm_system.entity.template.AbsLongEntity;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class Attachment extends AbsLongEntity {
    private String originalName, contentType;
    private long size;
    @ManyToOne(fetch = FetchType.LAZY)
    private Product product;
}
