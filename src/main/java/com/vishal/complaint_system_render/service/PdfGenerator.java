package com.vishal.complaint_system_render.service;


import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

import com.itextpdf.text.Document;
import com.itextpdf.text.Element;
import com.itextpdf.text.Font;
import com.itextpdf.text.FontFactory;
import com.itextpdf.text.Image;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;
import com.vishal.complaint_system_render.entity.Complaint;

public class PdfGenerator {

    public static byte[] generateComplaintPdf(Complaint complaint)
            throws Exception {

        ByteArrayOutputStream out = new ByteArrayOutputStream();

        Document document = new Document();

        PdfWriter.getInstance(document, out);

        document.open();

        Font titleFont =
                FontFactory.getFont(FontFactory.HELVETICA_BOLD, 22);

        Paragraph title =
                new Paragraph("Complaint Acknowledgement Receipt",
                        titleFont);

        title.setAlignment(Element.ALIGN_CENTER);

        document.add(title);

        document.add(new Paragraph(" "));

        document.add(new Paragraph(
                "Dear Citizen,\n\n" +
                "Your complaint has been successfully registered " +
                "with UrbanResolve Municipal Corporation."
        ));

        document.add(new Paragraph(" "));

        document.add(new Paragraph(
                "Complaint ID: " + complaint.getId()
        ));

        document.add(new Paragraph(
                "Title: " + complaint.getTitle()
        ));

        document.add(new Paragraph(
                "Description: " + complaint.getDescription()
        ));

        document.add(new Paragraph(
                "Location: " + complaint.getLocation()
        ));

        document.add(new Paragraph(
                "Priority: " + complaint.getPriority()
        ));

        document.add(new Paragraph(
                "Status: " + complaint.getStatus()
        ));

        document.add(new Paragraph(
                "Date & Time: " + complaint.getDate()
        ));

        document.add(new Paragraph(" "));

        // ADD COMPLAINT IMAGE

        if (complaint.getImage() != null) {

        document.add(new Paragraph("Complaint Proof Image:"));

        document.add(new Paragraph(" "));

        Image complaintImage = Image.getInstance(

                new ByteArrayInputStream(
                        complaint.getImage()
                ).readAllBytes()
        );

        // IMAGE SIZE
        complaintImage.scaleToFit(300, 300);

        // CENTER IMAGE
        complaintImage.setAlignment(Element.ALIGN_CENTER);

        document.add(complaintImage);

        document.add(new Paragraph(" "));
        }

        document.add(new Paragraph(
                "Our municipal team will review your complaint " +
                "and take necessary action as soon as possible."
        ));

        document.add(new Paragraph(" "));

        document.add(new Paragraph(
                "Thank you for helping us improve the city.\n\n" +
                "UrbanResolve Municipal Corporation"
        ));

        document.close();

        return out.toByteArray();
    }
}
