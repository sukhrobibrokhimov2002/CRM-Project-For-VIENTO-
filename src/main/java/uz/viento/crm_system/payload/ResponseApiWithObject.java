package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.User;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResponseApiWithObject {
    private String message;
    private boolean success;
    private Object object;
}
