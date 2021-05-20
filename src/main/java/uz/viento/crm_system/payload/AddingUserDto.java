package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class AddingUserDto {
    @NotBlank(message = "FullName must not be empty")
    private String fullName;
    @NotBlank(message = "Password must not be empty")
    private String password;
    @NotBlank(message = "PhoneNumber must not be empty")
    private String phoneNumber;
    private List<Long> roleId;
    private Integer attachmentId;
}
