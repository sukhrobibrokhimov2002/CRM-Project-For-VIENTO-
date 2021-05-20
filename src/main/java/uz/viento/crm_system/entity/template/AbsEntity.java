package uz.viento.crm_system.entity.template;

import lombok.AllArgsConstructor;
import lombok.Data;

import lombok.NoArgsConstructor;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;
import org.springframework.data.annotation.CreatedBy;
import org.springframework.data.annotation.LastModifiedBy;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.*;
import java.sql.Timestamp;
import java.util.UUID;

import uz.viento.crm_system.entity.User;

@MappedSuperclass
@Data
@EntityListeners(AuditingEntityListener.class)
public class AbsEntity {



    @Column(updatable = false, nullable = false)
    @CreationTimestamp
    private Timestamp createdAt;


    @Column(nullable = false)
    @UpdateTimestamp
    private Timestamp updatedAt;

    @JoinColumn(updatable = false)
    @CreatedBy
    private UUID createdBy;


    @LastModifiedBy
    private UUID updatedBy;

}
