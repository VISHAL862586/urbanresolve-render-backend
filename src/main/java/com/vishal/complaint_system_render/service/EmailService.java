package com.vishal.complaint_system_render.service;



import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.stereotype.Service;

import com.vishal.complaint_system_render.entity.Complaint;

import jakarta.mail.internet.MimeMessage;
import jakarta.mail.util.ByteArrayDataSource;

@Service
public class EmailService {

        @Autowired
    private JavaMailSender mailSender;

    @Value("${spring.mail.username}")
    private String fromEmail;


    // ==========================
    // OTP EMAIL
    // ==========================
    public void sendOtpEmail(String toEmail, String otp) {

      try {

    System.out.println("Creating MimeMessage");

    MimeMessage message = mailSender.createMimeMessage();

    MimeMessageHelper helper = new MimeMessageHelper(message);

    System.out.println("Sending From");
        helper.setFrom(fromEmail);

    System.out.println("Sending To");

    helper.setTo(toEmail);

    helper.setSubject("OTP");

    helper.setText("OTP : " + otp);

    System.out.println("Calling mailSender.send()");

    mailSender.send(message);

    System.out.println("EMAIL SENT");

} catch (Exception e) {
    e.printStackTrace();
}
    }

    // ==========================
    // COMPLAINT PDF EMAIL
    // ==========================
    public void sendComplaintPdf(
            String toEmail,
            Complaint complaint
    ) throws Exception {

        byte[] pdf =
                PdfGenerator.generateComplaintPdf(complaint);

        MimeMessage message =
                mailSender.createMimeMessage();

        MimeMessageHelper helper =
                new MimeMessageHelper(message, true);

        helper.setFrom(fromEmail);        
        helper.setTo(toEmail);

        helper.setSubject(
                "Complaint Registered Successfully"
        );

        helper.setText(
                """
                Dear Citizen,

                Your complaint has been registered successfully.

                Please find the attached PDF acknowledgement receipt.

                UrbanResolve
                """
        );

        helper.addAttachment(
                "ComplaintReceipt.pdf",
                new ByteArrayDataSource(
                        pdf,
                        "application/pdf"
                )
        );

        mailSender.send(message);

        System.out.println("PDF EMAIL SENT SUCCESSFULLY");
    }
}
