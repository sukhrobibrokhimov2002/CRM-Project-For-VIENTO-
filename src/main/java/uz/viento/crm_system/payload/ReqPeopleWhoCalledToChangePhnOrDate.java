package uz.viento.crm_system.payload;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.sql.Date;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ReqPeopleWhoCalledToChangePhnOrDate {
    private String phoneNumber;
    private Date shouldCallDate;
}
