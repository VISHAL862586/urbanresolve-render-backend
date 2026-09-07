package com.vishal.complaint_system_render.controller;



import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.server.ResponseStatusException;

import com.vishal.complaint_system_render.entity.Complaint;
import com.vishal.complaint_system_render.entity.User;
import com.vishal.complaint_system_render.repository.ComplaintRepository;
import com.vishal.complaint_system_render.service.EmailService;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/complaints")

public class ComplaintController {
    @Autowired
    private EmailService emailService;

    @Autowired
    private ComplaintRepository repo;

    @PostMapping
    public Complaint createComplaint(

            @RequestParam("title") String title,
            @RequestParam("category") String category,
            @RequestParam("description") String description,
            @RequestParam("location") String location,
            @RequestParam("priority") String priority,
            @RequestParam("image") MultipartFile image,

            HttpSession session

    ) 
    {

        User user = (User) session.getAttribute("user");

        if (user == null) {

            throw new ResponseStatusException(
                    HttpStatus.UNAUTHORIZED,
                    "Login required"
            );
        }

        try {

            Complaint complaint = new Complaint();

            complaint.setTitle(title);
            complaint.setCategory(category);
            complaint.setDescription(description);
            complaint.setLocation(location);
            complaint.setPriority(priority);

            complaint.setStatus("Pending");

            complaint.setDate(java.time.LocalDateTime.now());

            complaint.setUser(user);

            // SAVE IMAGE
            if (image != null && !image.isEmpty()) {

                complaint.setImage(
                        image.getBytes()
                );

                // SAVE IMAGE TYPE
                complaint.setImageType(
                        image.getContentType()
                );
            }

            Complaint savedComplaint =
                    repo.save(complaint);

            // SEND EMAIL PDF
            try {

                emailService.sendComplaintPdf(
                        user.getEmail(),
                        savedComplaint
                );

            } catch (Exception e) {

                e.printStackTrace();
            }

            return savedComplaint;

        } catch (Exception e) {

            e.printStackTrace();

            throw new RuntimeException(
                    "Error saving complaint"
            );
        }
    }

   @PutMapping("/{id}")
    public Complaint updateStatus(@PathVariable Long id,
                                @RequestParam String status,
                                HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new ResponseStatusException(HttpStatus.UNAUTHORIZED);
        }

        if (!"ADMIN".equals(user.getRole())) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN);
        }

        Complaint complaint = repo.findById(id)
                .orElseThrow(() -> new RuntimeException("Not found"));

        complaint.setStatus(status);

        return repo.save(complaint);
    }

    // ✅ DELETE
   @DeleteMapping("/{id}")
    public String deleteComplaint(@PathVariable Long id,
                             HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null || !user.getRole().equals("ADMIN")) {
            throw new RuntimeException("Access Denied");
        }

        repo.deleteById(id);
        return "Deleted";
    }

    // ✅ GET
    @GetMapping
    public List<Complaint> getAllComplaints(HttpSession session) {

        User user = (User) session.getAttribute("user");

        if (user == null) {
            throw new RuntimeException("Not logged in"); 
        }

        if ("ADMIN".equals(user.getRole())) {
            return repo.findAll();
        } else {
            return repo.findByUser(user);
        }
    }

   @GetMapping("/image/{id}")
    public ResponseEntity<byte[]> getComplaintImage(
            @PathVariable Long id
    ) {

        Complaint complaint = repo.findById(id)
                .orElseThrow(() ->
                        new RuntimeException("Complaint not found"));

        byte[] image = complaint.getImage();

        if (image == null) {

            return ResponseEntity.notFound().build();
        }

        String type = complaint.getImageType();

        if (type == null || type.isEmpty()) {

            type = "image/jpeg";
        }

        return ResponseEntity
                .ok()
                .contentType(
                        MediaType.parseMediaType(type)
                )
                .body(image);
    }
}
