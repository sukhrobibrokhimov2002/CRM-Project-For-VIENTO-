package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.sql.Date;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddingProductDto {

    @NotBlank(message = "Product name mustn't be null")
    private String nameUz;
    @NotBlank(message = "Product name mustn't be null")
    private String nameRu;
    @NotBlank(message = "Product name mustn't be null")
    private String nameEng;
    @NotBlank(message = "Product description mustn't be null")
    private String descriptionUz;
    @NotBlank(message = "Product description mustn't be null")
    private String descriptionRu;
    @NotBlank(message = "Product description mustn't be null")
    private String descriptionEng;
    @NotBlank(message = "Product amount mustn't be null")
    private double amount;
    @NotBlank(message = "Product measurement mustn't be null")
    private Long measurementId;
    private boolean available;
    private List<Long> attachmentId;
    private Long categoryId;
    private Long currencyId;
    private double originalPrice;
    private double sellingPrice;
    private Date expireDate;
    private String madeIn;

}
