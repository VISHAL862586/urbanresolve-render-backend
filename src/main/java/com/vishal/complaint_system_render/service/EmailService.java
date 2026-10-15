
package com.vishal.complaint_system_render.service;

import org.springframework.core.io.ByteArrayResource;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.vishal.complaint_system_render.entity.Complaint;

import jakarta.mail.internet.MimeMessage;

@Service
public class EmailService {

    private final JavaMailSender mailSender;

    public EmailService(JavaMailSender mailSender) {
        this.mailSender = mailSender;
    }

    // ==========================
    // OTP EMAIL
    // ==========================
    public void sendOtpEmail(String toEmail, String otp) {
        try {
            MimeMessage message = mailSender.createMimeMessage();
            MimeMessageHelper helper =
                    new MimeMessageHelper(message, false, "UTF-8");

            helper.setTo(toEmail);
            helper.setSubject("UrbanResolve OTP");
            helper.setText(
                    "Your UrbanResolve OTP is: " + otp
                    + "\n\nPlease do not share this OTP with anyone."
            );

            mailSender.send(message);

            System.out.println("OTP EMAIL SENT TO " + toEmail);

        } catch (Exception e) {
            throw new RuntimeException("Failed to send OTP email", e);
        }
    }

    // ==========================
    // COMPLAINT PDF EMAIL
    // ==========================
    public void sendComplaintPdf(
            String toEmail,
            Complaint complaint
    ) throws Exception {

        byte[] pdf = PdfGenerator.generateComplaintPdf(complaint);

        MimeMessage message = mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true, "UTF-8");

        helper.setTo(toEmail);
        helper.setSubject("Complaint Registered Successfully");

        helper.setText(
                "Dear Citizen,\n\n"
                + "Your complaint has been registered successfully.\n\n"
                + "Please find attached your PDF acknowledgement receipt.\n\n"
                + "Regards,\nUrbanResolve"
        );

        helper.addAttachment(
                "ComplaintReceipt.pdf",
                new ByteArrayResource(pdf)
        );

        mailSender.send(message);

        System.out.println("COMPLAINT PDF EMAIL SENT TO " + toEmail);
    }
}
