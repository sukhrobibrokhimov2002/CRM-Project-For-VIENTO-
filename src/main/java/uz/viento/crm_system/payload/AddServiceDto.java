package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.Category;

import javax.persistence.Column;
import javax.persistence.ManyToOne;
import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddServiceDto {

    @NotBlank(message = "Service name must not be null")
    private String name;
    @NotBlank(message = "Service description must not be null")
    private String description;
    private boolean available;
    private Double serviceFee;
    private Long categoryId;
}
