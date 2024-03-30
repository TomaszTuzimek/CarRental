package pl.tdelektro.CarRental.Management;


import com.itextpdf.text.DocumentException;
import org.junit.After;
import org.junit.Test;
import org.mockito.Mock;

import java.io.File;
import java.io.IOException;
import java.time.LocalDateTime;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.util.AssertionErrors.assertTrue;


public class ManagementInvoiceTest {
    @Mock
    ManagementReservation reservationMock = mock(ManagementReservation.class);
    File outputFile;

    @After
    public void removeTestFile() {
        if (outputFile.exists()) {
            outputFile.delete();
        }
    }

    @Test
    public void testCreateInvoice() throws DocumentException, IOException {


        ManagementInvoice managementInvoice = new ManagementInvoice();


        when(reservationMock.getReservationId()).thenReturn("2024-03-27 test@test.com");
        when(reservationMock.getCarId()).thenReturn(5);
        when(reservationMock.getCustomerEmail()).thenReturn("test@test.com");
        when(reservationMock.getStartDate()).thenReturn(LocalDateTime.of(2024, 03, 20, 10, 10));
        when(reservationMock.getEndDate()).thenReturn(LocalDateTime.of(2024, 03, 26, 10, 10));
        when(reservationMock.getTotalReservationCost()).thenReturn(1800f);

        managementInvoice.createInvoice(reservationMock);

        outputFile = new File("2024-03-27 test@test.com" + ".pdf");

        //Tests coverage if file exist and correct file name
        assertTrue("Invoice file should exist", outputFile.exists());
    }
}