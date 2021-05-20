package uz.viento.crm_system.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.util.FileCopyUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.multipart.MultipartHttpServletRequest;
import uz.viento.crm_system.entity.attachment.Attachment;
import uz.viento.crm_system.entity.attachment.AttachmentContent;
import uz.viento.crm_system.payload.ResponseAttachmentApi;
import uz.viento.crm_system.repository.AttachmentContentRepository;
import uz.viento.crm_system.repository.AttachmentRepository;
import uz.viento.crm_system.service.AttachmentService;

import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

@RestController
@RequestMapping(value = "/attachment")
public class AttachmentController {


    @Autowired
    AttachmentService attachmentService;
    @Autowired
    AttachmentRepository attachmentRepository;
    @Autowired
    AttachmentContentRepository attachmentContentRepository;

    @PostMapping("/upload")
    public ResponseEntity<?> uploadDb(MultipartHttpServletRequest request) throws IOException {
        ResponseAttachmentApi upload = attachmentService.upload(request);
        if (upload.isSuccess()) return ResponseEntity.ok(upload);
        return ResponseEntity.status(409).body(upload);
    }

    @GetMapping("/download/{id}")
    public void download(@PathVariable Integer id, HttpServletResponse response) throws IOException {
        attachmentService.download(id, response);
    }


    @GetMapping("/info")
    public List<Attachment> getAll() {
        List<Attachment> all = attachmentRepository.findAll();
        return all;
    }

    @GetMapping("/info/{id}")
    public Attachment getOneById(@PathVariable long id) {
        Optional<Attachment> attachmentOptional = attachmentRepository.findById(id);
        if (!attachmentOptional.isPresent()) return new Attachment();
        return attachmentOptional.get();
    }
}
