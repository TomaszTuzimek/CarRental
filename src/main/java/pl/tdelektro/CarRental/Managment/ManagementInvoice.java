package pl.tdelektro.CarRental.Managment;

import com.itextpdf.text.*;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;

class ManagementInvoice {

    void createInvoice(ManagementReservation managementReservation) throws FileNotFoundException, DocumentException {
        Document document = new Document();
        PdfWriter.getInstance(document, new FileOutputStream("HelloWorld.pdf"));
        document.open();

        Paragraph helloWorld = new Paragraph("Hello, World!");
        document.add(helloWorld);

        document.close();
    }
}