package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class RegisterDto {
    @NotBlank(message = "full Name must not be null")
    private String fullName;
    @NotBlank(message = "PhoneNumber must not be null")
    private String phoneNumber;

    @NotBlank(message = "PrePassword must not be null")
    private String prePassword;
    @NotBlank(message = "Password must not be null")
    private String password;
}
