package student.example.mokkivarausjarjestelmajava_ht;



import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;


public class Lasku {
    private final Main main;
    int lasku_id=0;
    int varaus_id=0;
    double summa = 0;
    double alv = 24;
    boolean maksettu = false;

    public String SQLToString(String id){
        String criteria = ("lasku_id = " + id);
        int SQLvaraus_id;
        int asiakas_id;
        String asiakasNimi;
        String mokkiNimi;
        LocalDateTime alkuPaiva;
        LocalDateTime loppuPaiva;
        String kaytetytPalvelut;
        int kokonaisHinta;
        int maksettu;
        String formatoituAlkuPaiva;
        String formatoituLoppuPaiva;
        String maksuStatus = "ei";

        try {
            ResultSet rs = main.connect.searchForStuff("laskutustiedot", criteria);
            rs.next();
            SQLvaraus_id = rs.getInt("varaus_id");
            asiakas_id = rs.getInt("asiakas_id");
            asiakasNimi = rs.getString("asiakas");
            mokkiNimi = rs.getString("mökki");
            alkuPaiva = rs.getObject("alkupaiva", LocalDateTime.class);
            loppuPaiva = rs.getObject("loppupaiva", LocalDateTime.class);
            kaytetytPalvelut = rs.getString("käytetyt palvelut");
            kokonaisHinta = rs.getInt("kokonaishinta");
            maksettu = rs.getInt("maksettu");
            formatoituAlkuPaiva = alkuPaiva.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
            formatoituLoppuPaiva = loppuPaiva.format(DateTimeFormatter.ofPattern("dd.MM.yyyy"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        if (maksettu==1)
            maksuStatus="kyllä";
        return ("Lasku id: " + id + "\nvaraus id: " + SQLvaraus_id + "\nAsiakas id: " + asiakas_id + "\nAsiakkaan nimi: " +
                asiakasNimi + "\nMökki: " + mokkiNimi + "\nVarauksen alku: " + formatoituAlkuPaiva + "\nVarauksen loppu: " +
                formatoituLoppuPaiva + "\nKäytetyt palvelut: " + kaytetytPalvelut + "\nSumma: " + kokonaisHinta + "\nOnko maksettu: " + maksuStatus);
    }
    public Lasku(Main main){
        this.main=main;
    }
}
