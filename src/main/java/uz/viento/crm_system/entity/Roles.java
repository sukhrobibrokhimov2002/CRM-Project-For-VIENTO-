package uz.viento.crm_system.entity;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import org.springframework.security.core.GrantedAuthority;
import uz.viento.crm_system.entity.enums.RoleName;
import uz.viento.crm_system.entity.template.AbsLongEntity;

import javax.persistence.*;
import java.util.List;

@EqualsAndHashCode(callSuper = true)
@Data
@AllArgsConstructor
@NoArgsConstructor
@Entity
public class Roles extends AbsLongEntity implements GrantedAuthority {

    @Column(unique = true)
    @Enumerated(EnumType.STRING)
    private RoleName roleName;



    @Override
    public String getAuthority() {
        return String.valueOf(this.roleName);
    }
}
