package student.example.mokkivarausjarjestelmajava_ht;

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Date;

public class Palvelu {
    private Main main;
    int palvelu_id;
    int alue_id;
    String nimi;
    String kuvaus;
    Double hinta;
    Double alv = 0.1;

    /**
     *
     * @param alku Käyttäjän input muodossa String pp.kk.vvvv (esim 12.03.2021)
     * @param loppu Käyttäjän input muodossa String pp.kk.vvvv (esim 12.03.2021)
     * @return String muodossa kaikki saadut tiedot kannasta
     */
    public String SQLRaport(String alku, String loppu){
        String alku_pvm;
        String loppu_pvm;
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("dd.MM.yyyy");
        DateTimeFormatter formatter2 = DateTimeFormatter.ofPattern("yyyy-MM-dd");
        try {
            alku_pvm = LocalDate.parse(alku, formatter).format(formatter2);
            loppu_pvm = LocalDate.parse(loppu, formatter).format(formatter2);
        } catch (DateTimeParseException e) {
            return "Päivämäärä ei muodossa 'pp.kk.yyyy'.";
        }
        String query = ("Call palvelu_raportti ("+"'"+alku_pvm+"',"+" '"+loppu_pvm+"'"+")");

        String alue_nimi;
        String palvelu;
        double tuotto;
        StringBuilder kokoTeksti = new StringBuilder();
        try{
            ResultSet rs = main.connect.executeQuery(query);
            while(rs.next()) {
                alue_nimi = rs.getString("alue_nimi");
                palvelu = rs.getString("palvelu");
                tuotto = rs.getDouble("tuotto");
                kokoTeksti.append("Alue nimi: ").append(alue_nimi).append("\nPalvelu: ").append(palvelu).append("\nTuotto: ").append(tuotto).append("\n").append("\n");
            }
        return kokoTeksti.toString();
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }
        public String SQLToString(String nimi){
        String query = ("SELECT * FROM palvelu WHERE nimi = " + nimi);
        String SQLpalvelu_id = null;
        int SQLalue_id = 0;
        String SQLkuvaus = null;
        Double SQLhinta = null;
        Double SQLalv = null;
        try {
            ResultSet rs = main.connect.executeQuery(query);
            rs.next();
            SQLpalvelu_id = rs.getString("palvelu_id");
            SQLalue_id = rs.getInt("alue_id");
            SQLkuvaus = rs.getString("kuvaus");
            SQLhinta = rs.getDouble("hinta");
            SQLalv = rs.getDouble("alv");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String kokoTeksti = ("Palvelu id: " + SQLpalvelu_id + "\nAlue id: " + SQLalue_id + "\nNimi: " + nimi +
                "\nKuvaus: " + SQLkuvaus + "\nHinta: " + SQLhinta + "\nALV: " + SQLalv + " %");
        return kokoTeksti;
    }
    public Palvelu(Main main){
        this.main=main;
    }
}
