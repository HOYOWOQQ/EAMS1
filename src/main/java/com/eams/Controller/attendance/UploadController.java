package com.eams.Controller.attendance;

import java.io.IOException;
import java.nio.file.Files;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import com.eams.Service.attendance.UploadService;

@RestController
@RequestMapping("/api/upload")
public class UploadController {

    @Autowired
    private UploadService uploadService;

    // 上傳附件
    @PostMapping(
    		  value = "/attachment",
    		  consumes = MediaType.MULTIPART_FORM_DATA_VALUE,
    		  produces = MediaType.APPLICATION_JSON_VALUE
    		)
    		public ResponseEntity<?> uploadAttachment(@RequestParam("file") MultipartFile file) {
    		    try {
    		        String fileName = uploadService.saveAttachment(file);
    		        return ResponseEntity.ok(Map.of("success", true, "fileName", fileName));
    		    } catch (Exception e) {
    		        e.printStackTrace(); // 先打 log 看真正錯誤
    		        return ResponseEntity.status(500).body(Map.of("success", false, "message", "上傳失敗：" + e.getMessage()));
    		    }
    		}

    // 提供預覽 / 下載附件
    @GetMapping("/attachment/{fileName:.+}")
    public ResponseEntity<Resource> downloadAttachment(@PathVariable String fileName) {
        try {
            Resource file = uploadService.loadAttachment(fileName);
            String contentType = Files.probeContentType(file.getFile().toPath());
            if (contentType == null) contentType = MediaType.APPLICATION_OCTET_STREAM_VALUE;

            return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(contentType))
                .header(HttpHeaders.CONTENT_DISPOSITION, "inline; filename=\"" + file.getFilename() + "\"")
                .body(file);
        } catch (IOException e) {
            return ResponseEntity.status(404).build();
        }
    }
 
}