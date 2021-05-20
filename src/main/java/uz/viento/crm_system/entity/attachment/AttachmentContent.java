package uz.viento.crm_system.entity.attachment;


import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import uz.viento.crm_system.entity.template.AbsEntity;
import uz.viento.crm_system.entity.template.AbsLongEntity;


import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.OneToOne;

@EqualsAndHashCode(callSuper = true)
@Data
@NoArgsConstructor
@AllArgsConstructor
@Entity
public class AttachmentContent extends AbsLongEntity {

    @OneToOne(fetch = FetchType.LAZY, optional = false)
    private Attachment attachment;

    private byte[] mainContent;
}
