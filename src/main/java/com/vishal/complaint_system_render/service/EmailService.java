package com.vishal.complaint_system_render.service;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.util.Base64;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.vishal.complaint_system_render.entity.Complaint;

@Service
public class EmailService {

    @Value("${RESEND_API_KEY}")
    private String resendApiKey;

    private final HttpClient httpClient = HttpClient.newHttpClient();

    private static final String RESEND_URL = "https://api.resend.com/emails";

    // ==========================
    // OTP EMAIL
    // ==========================
    public void sendOtpEmail(String toEmail, String otp) {

        try {

            String json = """
                    {
                      "from": "onboarding@resend.dev",
                      "to": ["%s"],
                      "subject": "UrbanResolve OTP",
                      "text": "Your UrbanResolve OTP is: %s"
                    }
                    """.formatted(toEmail, otp);

            HttpRequest request = HttpRequest.newBuilder()
                    .uri(URI.create(RESEND_URL))
                    .header("Authorization", "Bearer " + resendApiKey)
                    .header("Content-Type", "application/json")
                    .POST(HttpRequest.BodyPublishers.ofString(json))
                    .build();

            HttpResponse<String> response =
                    httpClient.send(
                            request,
                            HttpResponse.BodyHandlers.ofString()
                    );

            System.out.println("Resend response status: " + response.statusCode());
            System.out.println("Resend response body: " + response.body());

            if (response.statusCode() >= 200 && response.statusCode() < 300) {
                System.out.println("OTP EMAIL SENT");
            } else {
                throw new RuntimeException(
                        "Resend email failed: " + response.body()
                );
            }

        } catch (Exception e) {
            e.printStackTrace();
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

        String base64Pdf = Base64.getEncoder().encodeToString(pdf);

        String json = """
                {
                  "from": "onboarding@resend.dev",
                  "to": ["%s"],
                  "subject": "Complaint Registered Successfully",
                  "text": "Dear Citizen,\\n\\nYour complaint has been registered successfully.\\n\\nPlease find the attached PDF acknowledgement receipt.\\n\\nUrbanResolve",
                  "attachments": [
                    {
                      "filename": "ComplaintReceipt.pdf",
                      "content": "%s"
                    }
                  ]
                }
                """.formatted(toEmail, base64Pdf);

        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create(RESEND_URL))
                .header("Authorization", "Bearer " + resendApiKey)
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(json))
                .build();

        HttpResponse<String> response =
                httpClient.send(
                        request,
                        HttpResponse.BodyHandlers.ofString()
                );

        System.out.println("Resend PDF response status: " + response.statusCode());
        System.out.println("Resend PDF response body: " + response.body());

        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            System.out.println("PDF EMAIL SENT SUCCESSFULLY");
        } else {
            throw new RuntimeException(
                    "Resend PDF email failed: " + response.body()
            );
        }
    }
}