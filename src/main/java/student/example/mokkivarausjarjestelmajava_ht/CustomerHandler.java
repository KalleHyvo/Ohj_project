package student.example.mokkivarausjarjestelmajava_ht;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class CustomerHandler extends Application {
    private Main main;
    private Asiakas asiakas;
    String valittuID;

    public CustomerHandler(Main main, Asiakas asiakas) {
        this.main = main;
        this.asiakas=asiakas;
    }

    protected void asiakasMetodi(Stage asiakasStage, ResultSet rs){
        BorderPane BPasiakkaille = new BorderPane();
        TextArea alueAsiakkaidenTiedoille = new TextArea();
        alueAsiakkaidenTiedoille.setText("Klikkaa asiakasta nähdäksesi sen tarkemmat tiedot :)");
        alueAsiakkaidenTiedoille.setEditable(false);
        /**
         * Logiikka asiakkaiden indeksien näyttämiselle ListViewissä ja tietojen hakemiselle tietokannasta
         */
        ArrayList<String> asiakasNimiLista = new ArrayList<>();
        try {
            while (rs.next())
                asiakasNimiLista.add(rs.getString("asiakas_id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ListView<String> asiakasLista = new ListView<>();
        asiakasLista.setItems(FXCollections.observableArrayList(asiakasNimiLista));
        asiakasLista.getSelectionModel().selectedItemProperty().addListener(ov->{
            valittuID =(asiakasLista.getSelectionModel().getSelectedItem());
            alueAsiakkaidenTiedoille.setText(asiakas.SQLToString(valittuID));
        });
        Button kotiNappi = main.kotiNappain(asiakasStage);
        Button lisaysNappi = new Button("Lisää uusi asiakas");
        lisaysNappi.setOnAction(e->{
            asiakkaanLisaysMetodi(asiakasStage);
        });
        Button poistoNappi = new Button("Poista valittu asiakas");
        poistoNappi.setOnAction(e->{
            asiakkaanPoisto(asiakasStage);
        });
        Button muokkausNappi = new Button("Muokkaa valittua asiakasta");
        muokkausNappi.setOnAction(e->{
            asiakkaanMuokkausMetodi(asiakasStage);
        });
        Button etsintaNappi = new Button("Etsi asiakasta");
        etsintaNappi.setOnAction(e->{
            asiakkaanEtsintaMetodi(asiakasStage);
        });
        HBox paneeliAlaValikolle = new HBox(10);
        paneeliAlaValikolle.getChildren().addAll(kotiNappi, lisaysNappi, etsintaNappi, muokkausNappi, poistoNappi);
        BPasiakkaille.setBottom(paneeliAlaValikolle);
        BPasiakkaille.setLeft(asiakasLista);
        BPasiakkaille.setCenter(alueAsiakkaidenTiedoille);
        Scene scene = new Scene(BPasiakkaille);
        asiakasStage.setTitle("Asiakkaat");
        asiakasStage.setScene(scene);
        asiakasStage.show();
    }
    protected void asiakkaanLisaysMetodi(Stage asiakasStage){
        /**
         * graafisia komponentteja ja niiden sijoittelua
         */
        BorderPane BPAsiakkaanLisaamiselle = new BorderPane();
        BPAsiakkaanLisaamiselle.setPrefSize(400, 400);
        VBox paneeliUudenAsiakkaanTiedoille = new VBox(10);
        Text annaAlue = new Text("Asiakkaan postinumero");
        TextField postinroTF = new TextField();
        Text annaOsoite = new Text("Asiakkaan katuosoite");
        TextField katuosoiteTF = new TextField();
        Text etuNimi = new Text("Etunimi");
        TextField etunimiTF = new TextField();
        Text sukuNimi = new Text("Sukunimi");
        TextField sukunimiTF = new TextField();
        Text spostiOsoite = new Text("Email");
        TextField spostiTF = new TextField();
        Text puhelinnumeroTeksti = new Text("Puhelinnumero");
        TextField puhnroTF = new TextField();
        Button tallennusNappi = new Button("Tallenna");
        tallennusNappi.setAlignment(Pos.CENTER);
        Button kotiNappi = main.kotiNappain(asiakasStage);
        BPAsiakkaanLisaamiselle.setBottom(kotiNappi);
        paneeliUudenAsiakkaanTiedoille.getChildren().addAll(annaAlue, postinroTF, annaOsoite, katuosoiteTF, etuNimi, etunimiTF, sukuNimi, sukunimiTF, spostiOsoite, spostiTF,
                puhelinnumeroTeksti, puhnroTF, tallennusNappi);
        paneeliUudenAsiakkaanTiedoille.setAlignment(Pos.CENTER);
        /**
         * Toiminnallisuus tallennusnappiin.
         * hakee tiedot kaikista textFieldeistä ja lisää mökin niiden perusteella
         */
        tallennusNappi.setOnAction(e->{
            String lisattavanKatuosoite = katuosoiteTF.getText();
            String lisattavanPostinro = postinroTF.getText();
            String lisattavanEtunimi = etunimiTF.getText();
            String lisattavanEmail = spostiTF.getText();
            String lisattavanSukunimi = sukunimiTF.getText();
            String lisattavanPuhnro = puhnroTF.getText();
            /**
             * Käytetään main instanssissa olemassa olevaa connectionia SQL tietojen muokkaamiseen
             */
            if (lisattavanPostinro.length()==5) {
                main.connect.insertData("asiakas",
                        "postinro, etunimi, sukunimi, lahiosoite, email, puhelinnro",
                        ("\"" + lisattavanPostinro + "\", \"" + lisattavanEtunimi + "\", \"" +
                                lisattavanSukunimi + "\", \"" + lisattavanKatuosoite + "\", \"" +
                                lisattavanEmail + "\", \"" + lisattavanPuhnro + "\""));
                main.mainMenuMaker(asiakasStage);
            } else main.errorPopUp("Tarkista postinumero");
        });
        BPAsiakkaanLisaamiselle.setCenter(paneeliUudenAsiakkaanTiedoille);
        Scene lisaysScene = new Scene(BPAsiakkaanLisaamiselle);
        asiakasStage.setScene(lisaysScene);
        asiakasStage.show();
    }

    public void asiakkaanPoisto(Stage asiakasStage){
        VBox varoitusPaneeli = new VBox(30);
        varoitusPaneeli.setPrefSize(300, 300);
        varoitusPaneeli.setPadding(new Insets(10, 10, 10, 10));
        Text varoitusTeksti = new Text("Oletko varma että haluat poistaa asiakkaan\n" + asiakas.SQLToString(valittuID));
        HBox paneeliValikolle = new HBox(10);
        paneeliValikolle.setAlignment(Pos.CENTER);
        Button haluanPoistaa = new Button("Kyllä");
        Button enHalua = new Button("Ei");
        paneeliValikolle.getChildren().addAll(haluanPoistaa, enHalua);
        varoitusPaneeli.getChildren().addAll(varoitusTeksti, paneeliValikolle);
        Stage popUpStage = new Stage();
        Scene popUpScene = new Scene(varoitusPaneeli);
        haluanPoistaa.setOnAction(e->{
            main.connect.deleteStuff("asiakas", "asiakas_id", valittuID);
            System.out.println("Asiakas poistettu onnistuneesti");
            popUpStage.close();
            main.mainMenuMaker(asiakasStage);
        });
        enHalua.setOnAction(e->{
            System.out.println("Asiakasta ei poistettu");
            popUpStage.close();
        });

        popUpStage.setScene(popUpScene);
        popUpStage.setTitle("VAROITUS");
        popUpStage.show();
    }
    protected void asiakkaanEtsintaMetodi(Stage etsintaStage){
        BorderPane BPAsiakkaanEtsinnalle = new BorderPane();
        VBox paneeliEtsintaKriteereille = new VBox(10);
        paneeliEtsintaKriteereille.setAlignment(Pos.CENTER);
        Text alkuHopina = new Text("Kirjoita haluamasi kriteerit alla oleviin kenttiin.\nVoit jättää kentän tyhjäksi jos et halua käyttää kyseistä kriteeriä");
        Text etunimiKriteeri = new Text("Asiakkaan etunimi");
        TextField etunimiTF = new TextField();
        Text sukunimiKriteeri = new Text("Asiakkaan sukunimi");
        TextField sukunimiTF = new TextField();
        Text postinroKriteeri = new Text("Asiakkaan postinumero");
        TextField postinroTF = new TextField();
        Text lahiosoiteKriteeri = new Text("Asiakkaan lähiosoite");
        TextField lahiosoiteTF = new TextField();
        Text emailKriteeri = new Text("Asiakkaan sähköposti");
        TextField emailTF = new TextField();
        Text puhelinnumeroKriteeri = new Text("Asiakkaan puhelinnumero");
        TextField puhelinnumeroTF = new TextField();
        Button etsi = new Button("Etsi");
        //toiminnallisuus
        etsi.setOnAction(e->{
            List<String> kriteeriLista = new ArrayList<>();
            if (!etunimiTF.getText().isEmpty()) {
                kriteeriLista.add("LOWER(etunimi) LIKE '%" + etunimiTF.getText().toLowerCase() + "%'");
            }

            if (!sukunimiTF.getText().isEmpty()) {
                kriteeriLista.add("LOWER(sukunimi) LIKE '%" + sukunimiTF.getText().toLowerCase() + "%'");
            }

            if (postinroTF.getText().length()==5) {
                kriteeriLista.add("postinro LIKE '%" + postinroTF.getText() + "%'");
            }
            else if (!postinroTF.getText().isEmpty()){
                main.errorPopUp("Tarkista postinumero!");
            }

            if (!lahiosoiteTF.getText().isEmpty()) {
                kriteeriLista.add("LOWER(lahiosoite) LIKE '%" + lahiosoiteTF.getText().toLowerCase() + "%'");
            }
            if (!emailTF.getText().isEmpty()) {
                kriteeriLista.add("LOWER(email) LIKE '%" + emailTF.getText().toLowerCase() + "%'");
            }
            if (!puhelinnumeroTF.getText().isEmpty()) {
                kriteeriLista.add("LOWER(puhelinnro) LIKE '%" + puhelinnumeroTF.getText().toLowerCase() + "%'");
            }

            String kriteerit = String.join(" AND ", kriteeriLista);
            if (!kriteerit.isEmpty()) {
                asiakasMetodi(etsintaStage, main.connect.searchForStuff("asiakas", kriteerit));
            }
        });
        Button kotiNappi = main.kotiNappain(etsintaStage);
        paneeliEtsintaKriteereille.getChildren().addAll(alkuHopina, postinroKriteeri, postinroTF,
                etunimiKriteeri, etunimiTF, sukunimiKriteeri, sukunimiTF, lahiosoiteKriteeri, lahiosoiteTF,
                emailKriteeri, emailTF, puhelinnumeroKriteeri, puhelinnumeroTF, etsi);
        BPAsiakkaanEtsinnalle.setCenter(paneeliEtsintaKriteereille);
        BPAsiakkaanEtsinnalle.setLeft(kotiNappi);
        Scene scene = new Scene(BPAsiakkaanEtsinnalle);
        etsintaStage.setTitle("Asiakkaan haku");
        etsintaStage.setScene(scene);
        etsintaStage.show();
    }
    public void asiakkaanMuokkausMetodi(Stage muokkausStage){
        BorderPane BPAsiakkaanMuokkaukselle = new BorderPane();
        VBox paneeliMuokattavilleTiedoille = new VBox(10);
        Text muokattavaAsiakas = new Text("ASIAKKAAN TIEDOT\n" + asiakas.SQLToString(valittuID));
        Text postinroTeksti = new Text("\nJÄTÄ KENTTÄ TYHÄKSI JOS ET HALUA MUOKATA SITÄ\nUusi postinumero");
        TextField postinroTF = new TextField();
        Text etunimiTeksti = new Text("Uusi etunimi");
        TextField etunimiTF = new TextField();
        Text sukunimiTeksti = new Text("Uusi sukunimi");
        TextField sukunimiTF = new TextField();
        Text osoiteTeksti = new Text("Uusi osoite.");
        TextField osoiteTF = new TextField();
        Text emailTeksti = new Text("Uusi sähköpostiosoite");
        TextField emailTF = new TextField();
        Text puhnroTeksti = new Text("Uusi puhelinnumero");
        TextField puhnroTF = new TextField();
        Button tallennusNappi = new Button("Tallenna");
        tallennusNappi.setOnAction(e->{
            boolean muokkausOnnistui = false;
            if (postinroTF.getText().length()==5) {
                main.connect.updateTable("asiakas", "postinro", postinroTF.getText(), ("asiakas_id = " + valittuID));
                muokkausOnnistui=true;
            }
            else if (!postinroTF.getText().isEmpty()) {
                main.errorPopUp("Tarkista postinumero!");
            }
            if (!etunimiTF.getText().isEmpty()){
                main.connect.updateTable("asiakas", "etunimi", ("\"" + etunimiTF.getText() + "\""), ("asiakas_id = " + valittuID));
                muokkausOnnistui=true;
            }
            if (!sukunimiTF.getText().isEmpty()) {
                main.connect.updateTable("asiakas", "sukunimi", ("\"" + sukunimiTF.getText() + "\""), ("asiakas_id = " + valittuID));
                muokkausOnnistui=true;
            }
            if (!osoiteTF.getText().isEmpty()) {
                main.connect.updateTable("asiakas", "lahiosoite", ("\"" + osoiteTF.getText() + "\""), ("asiakas_id = " + valittuID));
                muokkausOnnistui = true;
            }
            if (!emailTF.getText().isEmpty()){
                main.connect.updateTable("asiakas", "email", "\"" + emailTF.getText() + "\"", "asiakas_id = " + valittuID);
                muokkausOnnistui=true;
            }
            if (!puhnroTF.getText().isEmpty()){
                main.connect.updateTable("asiakas", "puhelinnro", ("\"" + puhnroTF.getText()) + "\"", ("asiakas_id = " + valittuID));
                muokkausOnnistui=true;
            }
            if (muokkausOnnistui) {
                main.mainMenuMaker(muokkausStage);
            }
        });
        paneeliMuokattavilleTiedoille.getChildren().addAll(
                muokattavaAsiakas, postinroTeksti,
                postinroTF, etunimiTeksti, etunimiTF,
                sukunimiTeksti, sukunimiTF,
                osoiteTeksti, osoiteTF, emailTeksti,
                emailTF, puhnroTeksti, puhnroTF, tallennusNappi);
        Button kotiNappula = main.kotiNappain(muokkausStage);
        BPAsiakkaanMuokkaukselle.setLeft(kotiNappula);
        paneeliMuokattavilleTiedoille.setAlignment(Pos.CENTER);
        paneeliMuokattavilleTiedoille.setPadding(new Insets(10, 10, 10, 10));
        BPAsiakkaanMuokkaukselle.setCenter(paneeliMuokattavilleTiedoille);
        Scene scene = new Scene(BPAsiakkaanMuokkaukselle);
        muokkausStage.setTitle("Asiakkaan tietojen muokkaus");
        muokkausStage.setScene(scene);
        muokkausStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }
}
