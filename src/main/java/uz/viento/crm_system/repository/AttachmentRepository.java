package uz.viento.crm_system.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import uz.viento.crm_system.entity.attachment.Attachment;


public interface AttachmentRepository extends JpaRepository<Attachment, Long> {


}
