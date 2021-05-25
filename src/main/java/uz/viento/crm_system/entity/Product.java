package uz.viento.crm_system.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import lombok.*;
import uz.viento.crm_system.entity.attachment.Attachment;
import uz.viento.crm_system.entity.template.AbsUUIDEntity;

import javax.persistence.*;
import java.util.Date;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
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
    @JsonIgnore
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
