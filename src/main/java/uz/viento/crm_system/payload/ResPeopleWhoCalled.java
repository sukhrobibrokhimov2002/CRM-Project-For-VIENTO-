package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.Product;
import uz.viento.crm_system.entity.Service;
import uz.viento.crm_system.entity.User;
import uz.viento.crm_system.entity.enums.Status;

import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import java.sql.Date;
import java.time.LocalDate;
import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResPeopleWhoCalled {


    private String fullName;
    private String userPhoneNumber;
    private List<String> serviceName;
    private List<String> productName;

    private String phoneNumber;

    private LocalDate whenShouldCall;

    private String comment;

    private Status status;

    public ResPeopleWhoCalled(String fullName,List<String> serviceName, List<String> productName, String phoneNumber, LocalDate whenShouldCall, String comment, Status status) {
        this.fullName=fullName;
        this.serviceName = serviceName;
        this.productName = productName;
        this.phoneNumber = phoneNumber;
        this.whenShouldCall = whenShouldCall;
        this.comment = comment;
        this.status = status;

    }
}
