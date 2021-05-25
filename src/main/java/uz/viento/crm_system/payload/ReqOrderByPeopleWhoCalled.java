package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqOrderByPeopleWhoCalled {

    private String address;
    private long currencyId;
    private String comment;
    private String additionalPhoneNumber;

}
