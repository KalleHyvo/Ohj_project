package student.example.mokkivarausjarjestelmajava_ht;


import java.sql.ResultSet;
import java.sql.SQLException;

public class Asiakas {
    Main main;
    int asiakas_id = 0;
    int postinro = 0;
    String etunimi;
    String sukunimi;
    String lahiosoite;
    String email;
    String puhelinnumero;

    public String toString(){
        return ("Asiakas id: " + asiakas_id + "\npostinumero: " + postinro + "\nEtunimi: " + etunimi + "\nSukunimi: " + sukunimi + "\nOsoite: " + lahiosoite + "\nSähköposti: " + email + "\nPuhelinnumero: " + puhelinnumero);
    }
    public String SQLToString(String id){
        String query = ("SELECT * FROM asiakas WHERE asiakas_id = " + id);
        String SQLpostinro;
        String SQLetunimi;
        String SQLsukunimi;
        String SQLlahiosoite;
        String SQLsposti;
        String SQLpuhnro;
        try {
            ResultSet rs = main.connect.executeQuery(query);
            rs.next();
            SQLpostinro = rs.getString("postinro");
            SQLetunimi = rs.getString("etunimi");
            SQLsukunimi = rs.getString("sukunimi");
            SQLlahiosoite = rs.getString("lahiosoite");
            SQLsposti = rs.getString("email");
            SQLpuhnro = rs.getString("puhelinnro");
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        String kokoTeksti = ("asiakas id: " + id + "\npostinumero: " + SQLpostinro + "\nNimi: " + SQLetunimi +
                " " + SQLsukunimi + "\nLähiosoite: " + SQLlahiosoite + "\nSähköposti: " + SQLsposti +
                "\nPuhelinnumero: " + SQLpuhnro);
        return kokoTeksti;
    }

    public Asiakas(int asiakas_id, int postinro, String etunimi, String sukunimi, String lahiosoite, String email, String puhelinnumero) {
        this.asiakas_id = asiakas_id;
        this.postinro = postinro;
        this.etunimi = etunimi;
        this.sukunimi = sukunimi;
        this.lahiosoite = lahiosoite;
        this.email = email;
        this.puhelinnumero = puhelinnumero;
    }

    public Asiakas(Main main) {
        this.main = main;
    }
}
