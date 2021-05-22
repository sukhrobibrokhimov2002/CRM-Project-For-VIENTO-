package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.UUID;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqOutputProductDto {
    @NotBlank(message = "Product must not be null")
    private UUID productId;
    @NotBlank(message = "Product amount must not be null")
    private Double amount;
}
