package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqChangePassword {
    private String password;
    private String newPassword;
    private String confirmNewPassword;
    private String phoneNumber;
    private String newPhoneNumber;

}
