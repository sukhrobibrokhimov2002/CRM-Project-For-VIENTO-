package uz.viento.crm_system.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.viento.crm_system.entity.attachment.Attachment;
import uz.viento.crm_system.entity.attachment.AttachmentContent;
import uz.viento.crm_system.payload.ResponseAttachmentApi;
import uz.viento.crm_system.repository.AttachmentContentRepository;
import uz.viento.crm_system.repository.AttachmentRepository;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.Optional;

@Service
public class AttachmentService {

    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    public ResponseAttachmentApi upload(MultipartHttpServletRequest request) throws IOException {
        Iterator<String> fileNames = request.getFileNames();
        MultipartFile file = request.getFile(fileNames.next());
        Attachment attachment = new Attachment();
        if (file == null) return new ResponseAttachmentApi(null, "File is empty", false);
        attachment.setOriginalName(file.getOriginalFilename());
        attachment.setContentType(file.getContentType());
        attachment.setSize(file.getSize());
        Attachment savedAttachment = attachmentRepository.save(attachment);

        //Working with attachment bytes
        AttachmentContent attachmentContent = new AttachmentContent();
        attachmentContent.setMainContent(file.getBytes());
        attachmentContent.setAttachment(savedAttachment);
        attachmentContentRepository.save(attachmentContent);

        return new ResponseAttachmentApi(attachment, "File is saved", true);

    }

    public void download(long id, HttpServletResponse response) throws IOException {
        Optional<Attachment> attachmentOptional = attachmentRepository.findById(id);
        if (!attachmentOptional.isPresent()) return;
        Attachment attachment = attachmentOptional.get();
        Optional<AttachmentContent> byAttachment_id = attachmentContentRepository.findByAttachment_Id(id);
        if (byAttachment_id.isPresent()) {
            AttachmentContent attachmentContent = byAttachment_id.get();

            response.setHeader("Content-Disposition", "attachment; filename=\"" + attachment.getOriginalName() + "\"");
            response.setContentType(attachment.getContentType());

            FileCopyUtils.copy(attachmentContent.getMainContent(), response.getOutputStream());
        }
    }
}
