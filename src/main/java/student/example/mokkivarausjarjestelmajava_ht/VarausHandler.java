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
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class VarausHandler extends Application {
    private final Main main;
    private final Varaus varaus;
    int valittuIndeksi=-1;

    /**
     * Alustaja, joka luo varausHandlerin. Tähän tuodaan pääohjelmasta main ja varaus metodien käyttämistä varten
     * @param main Pääohjelman instanssi metodien käyttämistä varten
     * @param varaus Pääohjelman varaus instanssi metodien käyttämistä varten
     */
    public VarausHandler(Main main, Varaus varaus){
        this.main=main;
        this.varaus=varaus;
    }

    /**
     * Formatoi käyttäjän antaman päivämäärän SQL:n hyväksymään muotoon
     */
    DateTimeFormatter sqlKoodiksiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    protected void varausMetodi(Stage varausStage, ResultSet rs){
        BorderPane BPvarauksille = new BorderPane();
        TextArea alueVaraustenTiedoille = new TextArea();
        alueVaraustenTiedoille.setText("Klikkaa varausta nähdäksesi sen tarkemmat tiedot :)");
        alueVaraustenTiedoille.setEditable(false);
        /*
         * Logiikka palveluiden indeksien näyttämiselle ListViewissä ja tietojen hakemiselle tietokannasta
         */
        ArrayList<String> varausNimiLista = new ArrayList<>();
        try {
            while (rs.next())
                varausNimiLista.add(rs.getString("varaus_id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ListView<String> varausLista = new ListView<>();
        varausLista.setItems(FXCollections.observableArrayList(varausNimiLista));
        varausLista.getSelectionModel().selectedItemProperty().addListener(ov->{
            valittuIndeksi=Integer.parseInt(varausLista.getSelectionModel().getSelectedItem());
            alueVaraustenTiedoille.setText(varaus.SQLToString(valittuIndeksi));
        });
        Button kotiNappi = main.kotiNappain(varausStage);
        Button lisaysNappi = new Button("Tee uusi varaus");

        lisaysNappi.setOnAction(e-> varauksenLisaysMetodi(varausStage));

        Button poistoNappi = new Button("Poista valittu varaus");
        poistoNappi.setOnAction(e-> varauksenPoisto(varausStage));

        Button muokkausNappi = new Button("Muokkaa valittua varausta");
        muokkausNappi.setOnAction(e-> varauksenMuokkausMetodi(varausStage));
        Button etsintaNappi = new Button("Etsi varausta");
        etsintaNappi.setOnAction(e-> varauksenEtsintaMetodi(varausStage));
        HBox paneeliAlaValikolle = new HBox(10);
        paneeliAlaValikolle.getChildren().addAll(kotiNappi, lisaysNappi, muokkausNappi, etsintaNappi, poistoNappi);
        BPvarauksille.setBottom(paneeliAlaValikolle);
        BPvarauksille.setLeft(varausLista);
        BPvarauksille.setCenter(alueVaraustenTiedoille);
        Scene scene = new Scene(BPvarauksille);
        varausStage.setTitle("Palvelut");
        varausStage.setScene(scene);
        varausStage.show();
    }
    protected void varauksenLisaysMetodi(Stage varausStage){
        /*
         * graafisia komponentteja ja niiden sijoittelua
         */
        BorderPane BPvarauksenLisaamiselle = new BorderPane();
        BPvarauksenLisaamiselle.setPrefSize(400, 400);
        VBox paneeliUudenVarauksenTiedoille = new VBox(10);
        Text annaAsiakas = new Text("Uuden varauksen lisääminen:\nKAIKKI KENTÄT TÄYTETTÄVÄ!\nAsiakkaan id");
        TextField asiakasidTF = new TextField();
        Text varattavaMokki = new Text("Varattavan mökin id");
        TextField mokkiTF = new TextField();
        HBox paneeliVarauksenAlulle = new HBox(10);
        Text alkuPrompt = new Text("Syötä varauksen alkupäivä muodossa pp.kk.vvvv");
        TextField TFAlkuVuosille = new TextField();
        TextField TFAlkuKuukausille = new TextField();
        TextField TFAlkuPaivalle = new TextField();
        paneeliVarauksenAlulle.getChildren().addAll(TFAlkuPaivalle, TFAlkuKuukausille, TFAlkuVuosille);
        HBox paneeliVarauksenLopulle = new HBox(10);
        Text loppuPrompt = new Text("Syötä varauksen loppumispäivä muodossa pp.kk.vvvv");
        TextField TFLoppuVuosille = new TextField();
        TextField TFLoppuKuukausille = new TextField();
        TextField TFLoppuPaivalle = new TextField();
        paneeliVarauksenLopulle.getChildren().addAll(TFLoppuPaivalle, TFLoppuKuukausille, TFLoppuVuosille);
        Button tallennusNappi = new Button("Tallenna");
        tallennusNappi.setAlignment(Pos.CENTER);
        Button kotiNappi = main.kotiNappain(varausStage);
        BPvarauksenLisaamiselle.setBottom(kotiNappi);
        paneeliUudenVarauksenTiedoille.getChildren().addAll(annaAsiakas, asiakasidTF, varattavaMokki, mokkiTF, alkuPrompt, paneeliVarauksenAlulle,
                loppuPrompt, paneeliVarauksenLopulle, tallennusNappi);
        paneeliUudenVarauksenTiedoille.setAlignment(Pos.CENTER);
        /*
         * Toiminnallisuus tallennusnappiin.
         * hakee tiedot kaikista textFieldeistä ja lisää palvelun niiden perusteella
         * pelleilty DateTime formattien kanssa oikein huolella mutta nyt toimii
         */
        tallennusNappi.setOnAction(e->{
            String asiakkaanID = asiakasidTF.getText();
            String varattavanMokinID = mokkiTF.getText();
            String formatoituAlkuPaiva = null;
            String formatoituLoppuPaiva = null;
            try {
                LocalDateTime alkupvm = LocalDateTime.parse(TFAlkuVuosille.getText() + "-" + TFAlkuKuukausille.getText() + "-" + TFAlkuPaivalle.getText() + " 15:00:00", sqlKoodiksiFormatter);
                LocalDateTime loppupvm = LocalDateTime.parse(TFLoppuVuosille.getText() + "-" + TFLoppuKuukausille.getText() + "-" + TFLoppuPaivalle.getText() + " 12:00:00", sqlKoodiksiFormatter);

                formatoituAlkuPaiva = alkupvm.format(sqlKoodiksiFormatter);
                formatoituLoppuPaiva = loppupvm.format(sqlKoodiksiFormatter);
            } catch (Exception ex) {
                main.errorPopUp("Virhe päivämäärien tallentamisessa.\nVarmista, että olet syöttänyt päivämäärät oikein ja kokonaan\nEli 1.1.2024->01.01.2024");
            }
            /*
             * Käytetään main instanssissa olemassa olevaa connectionia SQL tietojen muokkaamiseen
             */
            main.connect.insertData("varaus", "asiakas_id, mokki_id, varattu_pvm, vahvistus_pvm, varattu_alkupvm, varattu_loppupvm",
                    (asiakkaanID + ", " + varattavanMokinID + ", " + "NOW()" + ", NOW(), " + "'" + formatoituAlkuPaiva + "'" + ", " + "'" + formatoituLoppuPaiva + "'"));
            main.mainMenuMaker(varausStage);
        });
        BPvarauksenLisaamiselle.setCenter(paneeliUudenVarauksenTiedoille);
        Scene lisaysScene = new Scene(BPvarauksenLisaamiselle);
        varausStage.setScene(lisaysScene);
        varausStage.show();
    }
    public void varauksenPoisto(Stage varausStage){
        VBox varoitusPaneeli = new VBox(30);
        varoitusPaneeli.setPrefSize(300, 300);
        varoitusPaneeli.setPadding(new Insets(10, 10, 10, 10));
        Text varoitusTeksti = new Text("Oletko varma että haluat poistaa varauksen\n" + varaus.SQLToString(valittuIndeksi));
        HBox paneeliValikolle = new HBox(10);
        paneeliValikolle.setAlignment(Pos.CENTER);
        Button haluanPoistaa = new Button("Kyllä");
        Button enHalua = new Button("Ei");
        paneeliValikolle.getChildren().addAll(haluanPoistaa, enHalua);
        varoitusPaneeli.getChildren().addAll(varoitusTeksti, paneeliValikolle);
        Stage popUpStage = new Stage();
        Scene popUpScene = new Scene(varoitusPaneeli);
        haluanPoistaa.setOnAction(e->{
            main.connect.deleteStuff("varaus", "varaus_id", Integer.toString(valittuIndeksi));
            System.out.println("varaus poistettu onnistuneesti");
            popUpStage.close();
            main.mainMenuMaker(varausStage);
        });
        enHalua.setOnAction(e->{
            System.out.println("varausta ei poistettu");
            popUpStage.close();
        });

        popUpStage.setScene(popUpScene);
        popUpStage.setTitle("VAROITUS");
        popUpStage.show();
    }
    public void varauksenMuokkausMetodi(Stage muokkausStage){
        BorderPane BPvarauksenMuokkaukselle = new BorderPane();
        VBox paneeliMuokattavilleTiedoille = new VBox(10);
        Text muokattavaVaraus = new Text("MUOKATTAVA VARAUS:\n" + varaus.SQLToString(valittuIndeksi));
        Text asiakkaanIdMuokkausTeksti = new Text("Muuta asiakkaan id");
        TextField asiakkaanIdTF = new TextField();
        Text uusiMokkiTeksti = new Text("Uusi mökki");
        TextField uusiMokkiTF = new TextField();
        HBox paneeliVarauksenAlulle = new HBox(10);
        Text alkuPrompt = new Text("Syötä varauksen alkupäivä muodossa pp.kk.vvvv");
        TextField TFAlkuVuosille = new TextField();
        TextField TFAlkuKuukausille = new TextField();
        TextField TFAlkuPaivalle = new TextField();
        paneeliVarauksenAlulle.getChildren().addAll(TFAlkuPaivalle, TFAlkuKuukausille, TFAlkuVuosille);
        HBox paneeliVarauksenLopulle = new HBox(10);
        Text loppuPrompt = new Text("Syötä varauksen loppumispäivä muodossa pp.kk.vvvv");
        TextField TFLoppuVuosille = new TextField();
        TextField TFLoppuKuukausille = new TextField();
        TextField TFLoppuPaivalle = new TextField();
        paneeliVarauksenLopulle.getChildren().addAll(TFLoppuPaivalle, TFLoppuKuukausille, TFLoppuVuosille);

        Button tallennusNappi = new Button("Tallenna");
        tallennusNappi.setOnAction(e->{
            if (!asiakkaanIdTF.getText().isEmpty())
                main.connect.updateTable("varaus", "asiakas_id", asiakkaanIdTF.getText(), ("varaus_id = " + valittuIndeksi));
            if (!uusiMokkiTF.getText().isEmpty())
                main.connect.updateTable("varaus", "mokki_id", ("\"" + uusiMokkiTF.getText()) + "\"", ("varaus_id = " + valittuIndeksi));
            if (!TFAlkuVuosille.getText().isEmpty()){
                String formatoituAlkuPaiva = null;
                try {
                    LocalDateTime alkupvm = LocalDateTime.parse(TFAlkuVuosille.getText() + "-" + TFAlkuKuukausille.getText() + "-" + TFAlkuPaivalle.getText() + " 15:00:00", sqlKoodiksiFormatter);
                    formatoituAlkuPaiva = ("'" + alkupvm.format(sqlKoodiksiFormatter) + "'");
                } catch (Exception ex) {
                    main.errorPopUp("Virhe alkupäivämäärän kanssa. Tarkista syötteet!\n" + ex);
                }
                main.connect.updateTable("varaus", "varattu_alkupvm", formatoituAlkuPaiva, "varaus_id = " + valittuIndeksi);
            }
            if (!TFLoppuVuosille.getText().isEmpty()){
                String formatoituLoppuPaiva = null;
                try {
                    LocalDateTime loppupvm = LocalDateTime.parse(TFLoppuVuosille.getText() + "-" + TFLoppuKuukausille.getText() + "-" + TFLoppuPaivalle.getText() + " 12:00:00", sqlKoodiksiFormatter);
                    formatoituLoppuPaiva = ("'" + loppupvm.format(sqlKoodiksiFormatter) + "'");
                } catch (Exception ex) {
                    main.errorPopUp("Virhe loppupäivämäärän kanssa. Tarkista syötteet!\n" + ex);
                }
                main.connect.updateTable("varaus", "varattu_loppupvm", formatoituLoppuPaiva, "varaus_id = " + valittuIndeksi);
            }
            main.mainMenuMaker(muokkausStage);
        });
        paneeliMuokattavilleTiedoille.getChildren().addAll(muokattavaVaraus, asiakkaanIdMuokkausTeksti, asiakkaanIdTF, uusiMokkiTeksti, uusiMokkiTF,
                alkuPrompt, paneeliVarauksenAlulle, loppuPrompt, paneeliVarauksenLopulle, tallennusNappi);
        Button kotiNappula = main.kotiNappain(muokkausStage);
        BPvarauksenMuokkaukselle.setLeft(kotiNappula);
        paneeliMuokattavilleTiedoille.setAlignment(Pos.CENTER);
        paneeliMuokattavilleTiedoille.setPadding(new Insets(10, 10, 10, 10));
        BPvarauksenMuokkaukselle.setCenter(paneeliMuokattavilleTiedoille);
        Scene scene = new Scene(BPvarauksenMuokkaukselle);
        muokkausStage.setTitle("Palvelun tietojen muokkaus");
        muokkausStage.setScene(scene);
        muokkausStage.show();
    }
    protected void varauksenEtsintaMetodi(Stage etsintaStage){
        BorderPane BPVarauksenEtsinnalle = new BorderPane();
        VBox paneeliEtsintaKriteereille = new VBox(10);
        paneeliEtsintaKriteereille.setAlignment(Pos.CENTER);
        Text alkuHopina = new Text("Kirjoita haluamasi kriteerit alla oleviin kenttiin.\nVoit jättää kentän tyhjäksi jos et halua käyttää kyseistä kriteeriä");
        Text asiakasIDKriteeri = new Text("Asiakas id");
        TextField asiakasIDTF = new TextField();
        Text mokkiKriteeri = new Text("Mökin id");
        TextField mokkiTF = new TextField();
        HBox paneeliVarauksenAlulle = new HBox(10);
        Text alkuPrompt = new Text("etsittävä alkupvm muodossa pp.kk.vvvv");
        TextField TFAlkuVuosille = new TextField();
        TextField TFAlkuKuukausille = new TextField();
        TextField TFAlkuPaivalle = new TextField();
        paneeliVarauksenAlulle.getChildren().addAll(TFAlkuPaivalle, TFAlkuKuukausille, TFAlkuVuosille);
        HBox paneeliVarauksenLopulle = new HBox(10);
        Text loppuPrompt = new Text("etsittävä loppumispvm muodossa pp.kk.vvvv");
        TextField TFLoppuVuosille = new TextField();
        TextField TFLoppuKuukausille = new TextField();
        TextField TFLoppuPaivalle = new TextField();
        paneeliVarauksenLopulle.getChildren().addAll(TFLoppuPaivalle, TFLoppuKuukausille, TFLoppuVuosille);
        Button etsi = new Button("Etsi");
        //toiminnallisuus
        etsi.setOnAction(e->{
            List<String> kriteeriLista = new ArrayList<>();
            if (!asiakasIDTF.getText().isEmpty()) {
                kriteeriLista.add("asiakas_id = " + asiakasIDTF.getText());
            }

            if (!mokkiTF.getText().isEmpty()) {
                kriteeriLista.add("mokki_id = " + mokkiTF.getText());
            }
            if (!TFAlkuVuosille.getText().isEmpty()){
                String formatoituAlkuPaiva = null;
                try {
                    LocalDateTime alkupvm = LocalDateTime.parse(TFAlkuVuosille.getText() + "-" + TFAlkuKuukausille.getText() + "-" + TFAlkuPaivalle.getText() + " 15:00:00", sqlKoodiksiFormatter);
                    formatoituAlkuPaiva = ("'" + alkupvm.format(sqlKoodiksiFormatter) + "'");
                } catch (Exception ex) {
                    main.errorPopUp("Virhe alkupäivämäärän kanssa. Tarkista syötteet!\n" + ex);
                }
                kriteeriLista.add("varattu_alkupvm >= " + formatoituAlkuPaiva);
            }
            if (!TFLoppuVuosille.getText().isEmpty()){
                String formatoituLoppuPaiva = null;
                try {
                    LocalDateTime loppupvm = LocalDateTime.parse(TFLoppuVuosille.getText() + "-" + TFLoppuKuukausille.getText() + "-" + TFLoppuPaivalle.getText() + " 12:00:00", sqlKoodiksiFormatter);
                    formatoituLoppuPaiva = ("'" + loppupvm.format(sqlKoodiksiFormatter) + "'");
                } catch (Exception ex) {
                    main.errorPopUp("Virhe loppupäivämäärän kanssa. Tarkista syötteet!\n" + ex);
                }
                kriteeriLista.add("varattu_loppupvm <= " + formatoituLoppuPaiva);
            }
            String kriteerit = String.join(" AND ", kriteeriLista);
            varausMetodi(etsintaStage, main.connect.searchForStuff("varaus", kriteerit));
        });
        Button kotiNappi = main.kotiNappain(etsintaStage);
        paneeliEtsintaKriteereille.getChildren().addAll(alkuHopina, asiakasIDKriteeri, asiakasIDTF, mokkiKriteeri, mokkiTF, alkuPrompt, paneeliVarauksenAlulle,
                loppuPrompt, paneeliVarauksenLopulle, etsi);
        BPVarauksenEtsinnalle.setCenter(paneeliEtsintaKriteereille);
        BPVarauksenEtsinnalle.setLeft(kotiNappi);
        Scene scene = new Scene(BPVarauksenEtsinnalle);
        etsintaStage.setTitle("Palvelun etsintä");
        etsintaStage.setScene(scene);
        etsintaStage.show();
    }

    public static void main(String[] args) {
        launch(args);
    }

    @Override
    public void start(Stage primaryStage) {

    }
}
