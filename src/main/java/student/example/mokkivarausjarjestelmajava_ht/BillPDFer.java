package student.example.mokkivarausjarjestelmajava_ht;

import com.itextpdf.text.Document;
import com.itextpdf.text.DocumentException;
import com.itextpdf.text.PageSize;
import com.itextpdf.text.Paragraph;
import com.itextpdf.text.pdf.PdfWriter;

import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

/*
HIEMAN SCUFFED TAPA SAADA ITEXT
MENE OSOITTEESEEN https://bit.ly/3v5ZumI JA SINUN PITÄISI LADATA JAR TIEDOSTO ITEXTIÄ VARTEN
LAITA TIEDOSTO PAIKKAAN, JOSTA LÖYDÄT SEN
INTELLIJ:SSÄ
FILE->PROJECT STRUCTURE->LIBRARIES->+ NAPPI->JAVA->ITEXT JAR FILE JONKA LATASIT
JA ADD TO PROJECT
 */

public class BillPDFer {
    Main main;
    public void createBillPDF(String lasku_id){
        String laskuString = luoPdfTeksti(lasku_id);
        Document lasku = new Document();
        FileOutputStream fileOutputStream = null;
        PdfWriter pdfWriter = null;

        try {
            fileOutputStream = new FileOutputStream("lasku.pdf");
            pdfWriter = PdfWriter.getInstance(lasku, fileOutputStream);
            lasku.open();
            lasku.add(new Paragraph(laskuString));

        } catch (FileNotFoundException | DocumentException e) {
            throw new RuntimeException("Failed to create PDF: ", e);
        } finally {
            if (lasku.isOpen()) {
                lasku.close();
            }
            if (pdfWriter != null) {
                pdfWriter.close();
            }
            if (fileOutputStream != null) {
                try {
                    fileOutputStream.close();
                } catch (IOException e) {
                    System.err.println("Error closing FileOutputStream: " + e.getMessage());
                }
            }
        }
    }

    private String luoPdfTeksti(String lasku_id) {
        int laskuID;
        int varausID;
        int asiakasID;
        String asiakasNimi;
        String mokkinimi;
        LocalDateTime SQLalkupvm;
        LocalDateTime SQLloppupvm;
        String palveluMaara;
        Double kokonaisHinta;
        //Haetaan laskuun laitettavat tiedot:
        try {
            ResultSet rs = main.connect.searchForStuff("laskutustiedot", ("lasku_id = " + lasku_id));
            rs.next();
            laskuID = rs.getInt("lasku_id");
            varausID = rs.getInt("varaus_id");
            asiakasID = rs.getInt("asiakas_id");
            asiakasNimi = rs.getString("asiakas");
            mokkinimi = rs.getString("mökki");
            SQLalkupvm = rs.getObject("alkupaiva", LocalDateTime.class);
            SQLloppupvm = rs.getObject("loppupaiva", LocalDateTime.class);
            palveluMaara = rs.getString("käytetyt palvelut");
            kokonaisHinta = rs.getDouble("kokonaishinta");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String formatoituAlkupvm = SQLalkupvm.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        String formatoituloppupvm = SQLloppupvm.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));

        String laskuString = ("Lasku id: " + laskuID + "\nvaraus id: " + varausID + "\nAsiakas_id: " + asiakasID + "\nAsiakkaan nimi: " + asiakasNimi +
                "\nVuokrattu mökki: " + mokkinimi + "\nVuokrausaika: " + formatoituAlkupvm + " - " + formatoituloppupvm + "\nLisäpalveluiden summa: " +
                palveluMaara + "\nYht: " + kokonaisHinta + "\nTilinumero: FI75 1065 5000 3130 63\nMaksathan laskun ennen vuokrauksen alkua!");
        return laskuString;
    }
    public BillPDFer(Main main) {
        this.main = main;
    }
}
