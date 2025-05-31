package lex.shemaleandre.izshema1.controller;

import lex.shemaleandre.izshema1.dto.ApiResponse;
import lex.shemaleandre.izshema1.service.MarkingGuideService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.net.MalformedURLException;
import java.util.UUID;

// Controller for marking guide management
@RestController
@RequestMapping("/api")
public class MarkingGuideController {

    @Autowired
    private MarkingGuideService markingGuideService;

    // Upload a marking guide (Instructor only)
    @PostMapping("/courses/{courseId}/marking-guides")
    @PreAuthorize("hasRole('INSTRUCTOR')")
    public ResponseEntity<ApiResponse> uploadMarkingGuide(@PathVariable UUID courseId,
                                                          @RequestParam("title") String title,
                                                          @RequestParam("file") MultipartFile file) throws IOException {
        return new ResponseEntity<>(markingGuideService.uploadMarkingGuide(courseId, title, file), HttpStatus.CREATED);
    }

    // Download a marking guide (Student only)
    @GetMapping("/marking-guides/{guideId}/download")
    @PreAuthorize("hasRole('STUDENT')")
    public ResponseEntity<Resource> downloadMarkingGuide(@PathVariable UUID guideId) throws MalformedURLException {
        Resource resource = markingGuideService.downloadMarkingGuide(guideId);
        String fileName = resource.getFilename() != null ? resource.getFilename() : "marking_guide.pdf";
        return ResponseEntity.ok()
                .contentType(MediaType.APPLICATION_PDF)
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
                .body(resource);
    }
}