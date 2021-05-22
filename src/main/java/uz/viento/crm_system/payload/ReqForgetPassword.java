package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqForgetPassword {
    private String phoneNumber;
    private String newPassword;
    private String confirmPassword;

}
