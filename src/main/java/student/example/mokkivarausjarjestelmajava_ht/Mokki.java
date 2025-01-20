package student.example.mokkivarausjarjestelmajava_ht;

import javafx.application.Application;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;

public class Mokki {
    Main main;
    int mokki_id;
    int alue_id;
    int postinro;
    String mokkinimi;
    String katuosoite;
    Double hinta;
    String kuvaus;
    int henkilomaara;
    ArrayList<String> varustelu = new ArrayList<>();
    public String toString(){
        return ("Mökki: " + mokkinimi + "\nmökin id: " + mokki_id + "\nalue: " + alue_id + "\nPostinumero: " + postinro + "\nosoite: " + katuosoite +
                "\nhinta/yö: " + hinta + "\nmökin kuvaus: " + kuvaus + "\nhenkilömäärä: " + henkilomaara + "\nmökin varustelu: " + varustelu);
    }

    /**
     * etsii mökkinimi:n perusteella mökin tiedot SQL tietokannasta ja palauttaa ne String tyyppisenä
     * @param valittuNimi mökin nimi, jonka tiedot halutaan palauttaa
     * @return String, jossa valitun mökin tiedot
     */
    public String SQLToString(String valittuNimi){
        String query = ("SELECT * FROM mokkialue WHERE mokkinimi = " + valittuNimi);
        int SQLmokki_id = -1;
        String SQLalue_id;
        int SQLpostinro = -1;
        String SQLkatuosoite = null;
        Double SQLhinta = null;
        String SQLkuvaus = null;
        int SQLhenkilomaara = -1;
        String SQLvarustelu = null;
        Double SQLalv = -1.0;
        int alvEuroina = -1;
        try {
            ResultSet rs = main.connect.executeQuery(query);
            rs.next();
            SQLalue_id = rs.getString("nimi");
            SQLpostinro = rs.getInt("postinro");
            SQLmokki_id = rs.getInt("mokki_id");
            SQLkatuosoite = rs.getString("katuosoite");
            SQLhinta = rs.getDouble("hinta");
            SQLkuvaus = rs.getString("kuvaus");
            SQLhenkilomaara = rs.getInt("henkilomaara");
            SQLvarustelu = rs.getString("varustelu");
            SQLalv = rs.getDouble("alv");
            alvEuroina = (int)Math.round((SQLhinta)*(SQLalv/100));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String kokoTeksti = ("Mokki " + valittuNimi + "\nmökin id: " + SQLmokki_id + "\nalue: " + SQLalue_id + "\nPostinumero: " + SQLpostinro + "\nosoite: " + SQLkatuosoite +
                "\nhinta/yö: " + SQLhinta + "\nmökin kuvaus: " + SQLkuvaus + "\nhenkilömäärä: " + SQLhenkilomaara + "\nmökin varustelu: " + SQLvarustelu + "\nAlv %: " + SQLalv + "\nAlv €: " + alvEuroina);
        return kokoTeksti;
    }

    public Mokki() {
    }
    public Mokki(Main main){
        this.main=main;
    }

    public Mokki(String identifier, Main main) throws SQLException {
        this.main=main;
        String query = "SELECT * FROM mokki WHERE alue_id = "+identifier+";";
        try (ResultSet rs = main.connect.executeQuery(query)) {
            this.mokki_id = rs.getInt("mokki_id");
            this.alue_id = rs.getInt("alue_id");
            this.postinro = rs.getInt("postinro");
            this.mokkinimi = rs.getString("mokkinimi");
            this.katuosoite = rs.getString("katuosoite");
            this.hinta = rs.getDouble("hinta");
            this.kuvaus = rs.getString("kuvaus");
            this.henkilomaara = rs.getInt("henkilomaara");
        }
        //this.varustelu = rs.getString("varustelu");
    }

    public Mokki(String data, String table, String values, Main main) throws SQLException {
        main.connect.insertData(data,table,values);

    }
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
        String query = ("Call majoitus_raportti ("+"'"+alku_pvm+"',"+" '"+loppu_pvm+"'"+")");
        String alue_nimi;
        String mokkinimi;
        double tuotto;
        StringBuilder kokoTeksti = new StringBuilder();
        try{
            ResultSet rs = main.connect.executeQuery(query);
            while(rs.next()) {
                alue_nimi = rs.getString("alue_nimi");
                mokkinimi = rs.getString("mokkinimi");
                tuotto = rs.getDouble("tuotto");
                kokoTeksti.append("Alue nimi: ").append(alue_nimi).append("\nMökki: ").append(mokkinimi).append("\nTuotto: ").append(tuotto).append("\n").append("\n");
            }
            return kokoTeksti.toString();
        } catch (SQLException e) {
            return "Database error: " + e.getMessage();
        } catch (Exception e) {
            return "Unexpected error: " + e.getMessage();
        }
    }


    public static void main(String[] args) {
    }
}
