package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.attachment.Attachment;
import uz.viento.crm_system.entity.template.AbsUUIDEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Product extends AbsUUIDEntity {

    @Column(unique = true, nullable = false)
    private String nameUz;
    @Column(unique = true, nullable = false)
    private String nameRu;
    @Column(unique = true, nullable = false)
    private String nameEng;
    private String descriptionUz;
    private String descriptionRu;
    private String descriptionEng;
    @Column(unique = true, nullable = false)
    private String serialNumber;
    private Double amount;
    private String madeIn;

    @OneToMany(mappedBy = "product", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private List<ProductPrice> productPrices;

    @ManyToOne(optional = false)
    private Measurement measurement;

    private boolean available;

    @OneToMany(mappedBy = "product")
    private List<Attachment> attachmentList;

    @ManyToOne(optional = false)
    private Category category;  

    @ManyToOne
    private CurrencyType currencyType;


    private boolean expired;

    private Date expireDate;
    private Date enterDate;
}
