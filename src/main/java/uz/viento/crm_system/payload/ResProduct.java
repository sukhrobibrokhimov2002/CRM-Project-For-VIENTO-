package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.Category;
import uz.viento.crm_system.entity.attachment.Attachment;

import java.util.Date;
import java.util.List;
@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResProduct {

    private String nameUz;
    private String nameRu;
    private String nameEng;
    private String descriptionUz;
    private String descriptionRu;
    private String descriptionEng;
    private String serialNumber;
    private String madeIn;
    private boolean available;
    private List<Attachment> attachmentList;
    private String category;
    private Double sellingPrice;
    private Date expireDate;




}
