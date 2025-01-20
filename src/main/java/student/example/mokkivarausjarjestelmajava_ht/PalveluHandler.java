package student.example.mokkivarausjarjestelmajava_ht;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.geometry.Bounds;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Popup;
import javafx.stage.Stage;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

public class PalveluHandler extends Application {
    /**
     * Kenttä Mainille, main instanssi tuodaan tähän luokkaan aina kun ohjelma ajetaan
     */
    private Main main;
    /**
     * Kenttä Palvelulle, palvelu instanssi tuodaan tähän luokkaan aina kun ohjelma ajetaan
     */
    private Palvelu palvelu;

    /**
     * Alustaja palveluHandlerille, tällä tuodaan PalveluHandlerin metodit käytettäväksi Mainiin
     * @param main Main instanssi, joka kutsuu PalveluHandleria
     * @param palvelu Palvelu instanssi, joka mahdollista palvelun metodien kutsumisen
     */
    public PalveluHandler(Main main, Palvelu palvelu){
        this.main=main;
        this.palvelu=palvelu;
    }

    /**
     * valittuIndeksi pitää kirjaa siitä, minkä palvelun käyttäjä on valinnut
     * Oletuksena -1, jotta käyttäjä ei voi vahingossa muokata tai poistaa palveluita
     */
    String valittuNimi = "-1";
    protected void palveluMetodi(Stage palveluStage, ResultSet rs){
        BorderPane BPpalveluille = new BorderPane();
        TextArea aluePalveluidenTiedoille = new TextArea();
        aluePalveluidenTiedoille.setText("Klikkaa palvelua nähdäksesi sen tarkemmat tiedot :)");
        aluePalveluidenTiedoille.setEditable(false);
        /**
         * Logiikka palveluiden indeksien näyttämiselle ListViewissä ja tietojen hakemiselle tietokannasta
         */
        ArrayList<String> palveluNimiLista = new ArrayList<>();
        try {
            while (rs.next())
                palveluNimiLista.add(rs.getString("nimi"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ListView<String> palveluLista = new ListView<>();
        palveluLista.setItems(FXCollections.observableArrayList(palveluNimiLista));
        palveluLista.getSelectionModel().selectedItemProperty().addListener(ov->{
            valittuNimi= ("'" + palveluLista.getSelectionModel().getSelectedItem() + "'");
            aluePalveluidenTiedoille.setText(palvelu.SQLToString(valittuNimi));
        });
        Button kotiNappi = main.kotiNappain(palveluStage);
        Button lisaysNappi = new Button("Lisää uusi palvelu");
        lisaysNappi.setOnAction(e->{
            palvelunLisaysMetodi(palveluStage);
        });
        Button poistoNappi = new Button("Poista valittu palvelu");
        poistoNappi.setOnAction(e->{
            palvelunPoisto(palveluStage);
        });
        Button muokkausNappi = new Button("Muokkaa valittua palvelua");
        muokkausNappi.setOnAction(e->{
            palvelunMuokkausMetodi(palveluStage);
        });

        Button etsintaNappi = new Button("Etsi palvelua");
        etsintaNappi.setOnAction(e->{
            palvelunEtsintaMetodi(palveluStage);
        });
        Button popupButton = new Button("Palvelu raportti");
        popupButton.setOnAction(e -> showPopup());

        HBox paneeliAlaValikolle = new HBox(10);
        paneeliAlaValikolle.getChildren().addAll(kotiNappi, lisaysNappi,muokkausNappi, etsintaNappi, poistoNappi, popupButton);
        BPpalveluille.setBottom(paneeliAlaValikolle);
        BPpalveluille.setLeft(palveluLista);
        BPpalveluille.setCenter(aluePalveluidenTiedoille);
        Scene scene = new Scene(BPpalveluille);
        palveluStage.setTitle("Palvelut");
        palveluStage.setScene(scene);
        palveluStage.show();
    }
    protected void palvelunLisaysMetodi(Stage palveluStage){
        /**
         * graafisia komponentteja ja niiden sijoittelua
         */
        BorderPane BPPalvelunLisaamiselle = new BorderPane();
        BPPalvelunLisaamiselle.setPrefSize(400, 400);
        VBox paneeliUudenPalvelunTiedoille = new VBox(10);
        Text annaAlue = new Text("Millä alueella palvelu on?");
        TextField alueTF = new TextField();
        Text palvelunNimi = new Text("Palvelun nimi");
        TextField nimiTF = new TextField();
        Text kuvausTeksti = new Text("Millainen palvelu on kyseessä?");
        TextField kuvausTF = new TextField();
        Text hintaTeksti = new Text("Ja paljonkos lysti kustantaa?");
        TextField hintaTF = new TextField();
        Text alvTeksti = new Text("Montako prosenttia valtio vetää välistä?");
        TextField alvTF = new TextField();
        Button tallennusNappi = new Button("Tallenna");
        tallennusNappi.setAlignment(Pos.CENTER);
        Button kotiNappi = main.kotiNappain(palveluStage);
        BPPalvelunLisaamiselle.setBottom(kotiNappi);
        paneeliUudenPalvelunTiedoille.getChildren().addAll(annaAlue, alueTF, palvelunNimi, nimiTF,
                kuvausTeksti, kuvausTF, hintaTeksti, hintaTF,
                alvTeksti, alvTF, tallennusNappi);
        paneeliUudenPalvelunTiedoille.setAlignment(Pos.CENTER);
        /**
         * Toiminnallisuus tallennusnappiin.
         * hakee tiedot kaikista textFieldeistä ja lisää palvelun niiden perusteella
         */
        tallennusNappi.setOnAction(e->{
            String palvelunAlue = alueTF.getText();
            String lisattavanPalvelunNimi = nimiTF.getText();
            String lisattavaKuvaus = kuvausTF.getText();
            String lisattavaHinta = hintaTF.getText();
            String lisattavaALV = alvTF.getText();
            /**
             * Käytetään main instanssissa olemassa olevaa connectionia SQL tietojen muokkaamiseen
             */
            main.connect.insertData("palvelu", "alue_id, nimi, kuvaus, hinta, alv",
                    (palvelunAlue + ", \"" + lisattavanPalvelunNimi + "\", \"" + lisattavaKuvaus + "\", " + lisattavaHinta +
                            ", " + lisattavaALV));
            main.mainMenuMaker(palveluStage);
        });
        BPPalvelunLisaamiselle.setCenter(paneeliUudenPalvelunTiedoille);
        Scene lisaysScene = new Scene(BPPalvelunLisaamiselle);
        palveluStage.setScene(lisaysScene);
        palveluStage.show();
    }
    public void palvelunPoisto(Stage palveluStage){
        VBox varoitusPaneeli = new VBox(30);
        varoitusPaneeli.setPrefSize(300, 300);
        varoitusPaneeli.setPadding(new Insets(10, 10, 10, 10));
        Text varoitusTeksti = new Text("Oletko varma että haluat poistaa palvelun\n" + palvelu.SQLToString(valittuNimi));
        HBox paneeliValikolle = new HBox(10);
        paneeliValikolle.setAlignment(Pos.CENTER);
        Button haluanPoistaa = new Button("Kyllä");
        Button enHalua = new Button("Ei");
        paneeliValikolle.getChildren().addAll(haluanPoistaa, enHalua);
        varoitusPaneeli.getChildren().addAll(varoitusTeksti, paneeliValikolle);
        Stage popUpStage = new Stage();
        Scene popUpScene = new Scene(varoitusPaneeli);
        haluanPoistaa.setOnAction(e->{
            main.connect.deleteStuff("palvelu", "nimi", valittuNimi);
            System.out.println("palvelu poistettu onnistuneesti");
            popUpStage.close();
            main.mainMenuMaker(palveluStage);
        });
        enHalua.setOnAction(e->{
            System.out.println("palvelua ei poistettu");
            popUpStage.close();
        });

        popUpStage.setScene(popUpScene);
        popUpStage.setTitle("VAROITUS");
        popUpStage.show();
    }
    public void palvelunMuokkausMetodi(Stage muokkausStage){
        BorderPane BPpalvelunMuokkaukselle = new BorderPane();
        VBox paneeliMuokattavilleTiedoille = new VBox(10);
        Text muokattavapalvelu = new Text("MUOKATTAVA PALVELU:\n" + palvelu.SQLToString(valittuNimi));
        Text alueMuokkausTeksti = new Text("Uusi alue id (numero)");
        TextField alueTF = new TextField();
        Text nimiTeksti = new Text("Uusi nimi");
        TextField nimiTF = new TextField();
        Text kuvausTeksti = new Text("Uusi kuvaus");
        TextField kuvausTF = new TextField();
        Text hintaTeksti = new Text("Uusi hinta");
        TextField hintaTF = new TextField();
        Text alvTeksti = new Text("Uusi ALV%");
        TextField alvTF = new TextField();

        Button tallennusNappi = new Button("Tallenna");
        tallennusNappi.setOnAction(e->{
            if (!alueTF.getText().isEmpty())
                main.connect.updateTable("palvelu", "alue_id", alueTF.getText(), ("nimi = " + valittuNimi));
            if (!nimiTF.getText().isEmpty())
                main.connect.updateTable("palvelu", "nimi", ("\"" + nimiTF.getText()) + "\"", ("nimi = " + valittuNimi));
            if (!kuvausTF.getText().isEmpty())
                main.connect.updateTable("palvelu", "kuvaus", ("\"" + kuvausTF.getText()) + "\"", ("nimi = " + valittuNimi));
            if (!hintaTF.getText().isEmpty())
                main.connect.updateTable("palvelu", "hinta", hintaTF.getText(), ("nimi = " + valittuNimi));
            if (!alvTF.getText().isEmpty())
                main.connect.updateTable("palvelu", "alv", alvTF.getText(), ("nimi = " + valittuNimi));
            main.mainMenuMaker(muokkausStage);
        });
        paneeliMuokattavilleTiedoille.getChildren().addAll(muokattavapalvelu, alueMuokkausTeksti, alueTF, nimiTeksti, nimiTF, hintaTeksti,
                hintaTF, kuvausTeksti, kuvausTF, alvTeksti, alvTF, tallennusNappi);
        Button kotiNappula = main.kotiNappain(muokkausStage);
        BPpalvelunMuokkaukselle.setLeft(kotiNappula);
        paneeliMuokattavilleTiedoille.setAlignment(Pos.CENTER);
        paneeliMuokattavilleTiedoille.setPadding(new Insets(10, 10, 10, 10));
        BPpalvelunMuokkaukselle.setCenter(paneeliMuokattavilleTiedoille);
        Scene scene = new Scene(BPpalvelunMuokkaukselle);
        muokkausStage.setTitle("Palvelun tietojen muokkaus");
        muokkausStage.setScene(scene);
        muokkausStage.show();
    }
    protected void palvelunEtsintaMetodi(Stage etsintaStage){
        BorderPane BPPalvelunEtsinnalle = new BorderPane();
        VBox paneeliEtsintaKriteereille = new VBox(10);
        paneeliEtsintaKriteereille.setAlignment(Pos.CENTER);
        Text alkuHopina = new Text("Kirjoita haluamasi kriteerit alla oleviin kenttiin.\nVoit jättää kentän tyhjäksi jos et halua käyttää kyseistä kriteeriä");
        Text hakuSanaKriteeri = new Text("Hakusana");
        TextField hakuSanaTF = new TextField();
        Text alueKriteeri = new Text("Alueen ID, jolla palvelu sijaitsee");
        TextField alueTF = new TextField();
        Text hinnat = new Text("palvelun minimi ja maksimi hinta");
        HBox paneeliHinnoille = new HBox(10);
        TextField minimiHinta = new TextField();
        TextField maksimiHinta = new TextField();
        Label minimiLabel = new Label("Min", minimiHinta);
        minimiLabel.setContentDisplay(ContentDisplay.RIGHT);
        Label maksimiLabel = new Label("Max", maksimiHinta);
        maksimiLabel.setContentDisplay(ContentDisplay.RIGHT);
        paneeliHinnoille.getChildren().addAll(minimiLabel, minimiHinta, maksimiLabel, maksimiHinta);
        Button etsi = new Button("Etsi");
        //toiminnallisuus
        etsi.setOnAction(e->{
            List<String> kriteeriLista = new ArrayList<>();
            if (!hakuSanaTF.getText().isEmpty()) {
                /**
                 * Tässä muutetaan käyttäjän syöte pieniksi kirjaimiksi ja haetaan tietokannasta kuvaus-kentästä pelkästään pienillä kirjaimilla
                 */
                kriteeriLista.add("LOWER(kuvaus) LIKE '%" + hakuSanaTF.getText().toLowerCase() + "%' OR LOWER(nimi) LIKE '%" + hakuSanaTF.getText().toLowerCase() + "%'");
            }

            if (!alueTF.getText().isEmpty()) {
                kriteeriLista.add("alue_id = " + alueTF.getText());
            }

            if (!minimiHinta.getText().isEmpty()) {
                kriteeriLista.add("hinta >= " + minimiHinta.getText());
            }

            if (!maksimiHinta.getText().isEmpty()) {
                kriteeriLista.add("hinta <= " + maksimiHinta.getText());
            }
            String kriteerit = String.join(" AND ", kriteeriLista);
            palveluMetodi(etsintaStage, main.connect.searchForStuff("palvelu", kriteerit));
        });
        Button kotiNappi = main.kotiNappain(etsintaStage);
        paneeliEtsintaKriteereille.getChildren().addAll(alkuHopina, hakuSanaKriteeri, hakuSanaTF, alueKriteeri, alueTF, hinnat, paneeliHinnoille, etsi);
        BPPalvelunEtsinnalle.setCenter(paneeliEtsintaKriteereille);
        BPPalvelunEtsinnalle.setLeft(kotiNappi);
        Scene scene = new Scene(BPPalvelunEtsinnalle);
        etsintaStage.setTitle("Palvelun etsintä");
        etsintaStage.setScene(scene);
        etsintaStage.show();
    }
    public void showPopup() {

        BorderPane paneeliPopUpille = new BorderPane();
        paneeliPopUpille.setPrefSize(450, 350);
        paneeliPopUpille.setPadding(new Insets(10, 10, 10, 10));
        // Upper Center
        HBox upperCenterBox = new HBox(10);
        upperCenterBox.setAlignment(Pos.CENTER);
        TextField textField1 = new TextField();
        textField1.setFocusTraversable(false);
        textField1.setPromptText("alku pvm: pp.kk.vvvvv");
        TextField textField2 = new TextField();
        textField2.setFocusTraversable(false);
        textField2.setPromptText("loppu pvm: pp.kk.vvvvv");
        Button upperCenterButton = new Button("Etsi");
        upperCenterBox.getChildren().addAll(textField1, textField2, upperCenterButton);
        paneeliPopUpille.setTop(upperCenterBox);

        // Middle Center
        TextArea raporttiText = new TextArea();
        raporttiText.setEditable(false);
        paneeliPopUpille.setCenter(raporttiText);
        Button okNappi = new Button("OK");
        okNappi.setAlignment(Pos.CENTER);
        paneeliPopUpille.setBottom(okNappi);

        paneeliPopUpille.requestFocus();
        Scene scene = new Scene(paneeliPopUpille);
        Stage popUpStage = new Stage();

        upperCenterButton.setOnAction(e -> {
            if(textField1.getText() != null & textField2.getText() != null){
            // Add functionality here to handle button click
            String text1 = textField1.getText();
            String text2 = textField2.getText();
            raporttiText.setText(palvelu.SQLRaport(text1,text2));
            paneeliPopUpille.requestFocus();
            }
        });

        okNappi.setOnAction(e -> {
            popUpStage.close();
        });

        popUpStage.setTitle("Raportti");
        popUpStage.setScene(scene);
        popUpStage.show();
    }
    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }
}
