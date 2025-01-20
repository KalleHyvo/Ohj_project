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

public class BillHandler extends Application {
    private final Lasku lasku;
    private final Main main;
    String valittuNimi = "-1";
    BillPDFer billPdfer;
    DateTimeFormatter sqlKoodiksiFormatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");

    public BillHandler(Main main, Lasku lasku, BillPDFer billPdfer) {
        this.main = main;
        this.lasku = lasku;
        this.billPdfer=billPdfer;
    }
    protected void laskuMetodi(Stage laskuStage, ResultSet rs){
        BorderPane BPlaskuille = new BorderPane();
        TextArea alueLaskujenTiedoille = new TextArea();
        alueLaskujenTiedoille.setText("Klikkaa laskua nähdäksesi sen tarkemmat tiedot :)");
        alueLaskujenTiedoille.setEditable(false);
        /*
         * Logiikka laskujen indeksien näyttämiselle ListViewissä ja tietojen hakemiselle tietokannasta
         */
        ArrayList<String> laskuNimiLista = new ArrayList<>();
        try {
            while (rs.next())
                laskuNimiLista.add(rs.getString("lasku_id"));
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        ListView<String> laskuLista = new ListView<>();
        laskuLista.setItems(FXCollections.observableArrayList(laskuNimiLista));
        laskuLista.getSelectionModel().selectedItemProperty().addListener(ov->{
            valittuNimi =(laskuLista.getSelectionModel().getSelectedItem());
            alueLaskujenTiedoille.setText(lasku.SQLToString(valittuNimi));
        });
        Button kotiNappi = main.kotiNappain(laskuStage);
        Button pdfer = new Button("Luo pdf");
        Button lisaysNappi = new Button("Uusi lasku");
        lisaysNappi.setOnAction(e-> laskunLisaysMetodi(laskuStage));

        Button maksettuNappi = new Button("Merkkaa lasku maksetuksi");
        maksettuNappi.setOnAction(e-> asetaMaksetuksi(laskuStage));
        Button maksamattomatNappi = new Button("Näytä avoimet laskut");
        maksamattomatNappi.setOnAction(e-> naytaAvoimetLaskut(laskuStage));
        Button etsintaNappi = new Button("Etsi laskua");
        etsintaNappi.setOnAction(e-> laskunEtsintaMetodi(laskuStage));
        pdfer.setOnAction(e-> billPdfer.createBillPDF(valittuNimi));
        HBox paneeliAlaValikolle = new HBox(10);
        paneeliAlaValikolle.getChildren().addAll(kotiNappi, pdfer, lisaysNappi, maksettuNappi, maksamattomatNappi, etsintaNappi);
        BPlaskuille.setBottom(paneeliAlaValikolle);
        BPlaskuille.setLeft(laskuLista);
        BPlaskuille.setCenter(alueLaskujenTiedoille);
        Scene scene = new Scene(BPlaskuille);
        laskuStage.setTitle("Laskut");
        laskuStage.setScene(scene);
        laskuStage.show();
    }
    public void naytaAvoimetLaskut(Stage primaryStage){
        laskuMetodi(primaryStage, main.connect.executeQuery("SELECT lasku_id FROM laskutustiedot WHERE maksettu = 0 ORDER BY lasku_id"));
    }
    public void laskunLisaysMetodi(Stage alueenLisaysStage){
        BorderPane BPlaskujenLisaamiselle = new BorderPane();
        BPlaskujenLisaamiselle.setPrefSize(400, 400);
        BPlaskujenLisaamiselle.setPadding(new Insets(10, 10, 10, 10));

        VBox paneeliUudenLaskunTiedoille = new VBox(10);
        Text alkuHopina = new Text("Laskun tiedot täytetään automaattisesti. Syötä laskutettavan varauksen id");
        TextField laskutettavaIdTF = new TextField();
        Button lisaysNappi = new Button("Lisää");
        lisaysNappi.setOnAction(e->{
            int uudenLaskunVarausId = Integer.parseInt(laskutettavaIdTF.getText());
            main.connect.executeQuery("CALL create_lasku(" + uudenLaskunVarausId + ");");
        });
        Button kotiNappula = main.kotiNappain(alueenLisaysStage);
        paneeliUudenLaskunTiedoille.setAlignment(Pos.CENTER);
        paneeliUudenLaskunTiedoille.getChildren().addAll(alkuHopina, laskutettavaIdTF, lisaysNappi, kotiNappula);
        BPlaskujenLisaamiselle.setCenter(paneeliUudenLaskunTiedoille);
        Scene scene = new Scene(BPlaskujenLisaamiselle);
        alueenLisaysStage.setScene(scene);
        alueenLisaysStage.setTitle("Lisää uusi alue");
        alueenLisaysStage.show();
    }
    public void asetaMaksetuksi(Stage primaryStage){
        main.connect.updateTable("lasku", "maksettu", "1", ("lasku_id = " + valittuNimi));
        main.mainMenuMaker(primaryStage);
    }
    protected void laskunEtsintaMetodi(Stage etsintaStage){
        BorderPane BPLaskunEtsinnalle = new BorderPane();
        VBox paneeliEtsintaKriteereille = new VBox(10);
        paneeliEtsintaKriteereille.setAlignment(Pos.CENTER);
        Text alkuHopina = new Text("Kirjoita haluamasi kriteerit alla oleviin kenttiin.\nVoit jättää kentän tyhjäksi jos et halua käyttää kyseistä kriteeriä");
        Text varausidKriteeri = new Text("Varaus id");
        TextField varausidTF = new TextField();
        Text asiakasidKriteeri = new Text("Asiakas id");
        TextField asiakasidTF = new TextField();
        Text asiakasnimiKriteeri = new Text("Asiakkaan nimi");
        TextField asiakasnimiTF = new TextField();
        Text mokinNimiKriteeri = new Text("Mökin nimi");
        TextField mokinNimiTF = new TextField();
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
            boolean wirhe = false;
            List<String> kriteeriLista = new ArrayList<>();
            if (!varausidTF.getText().isEmpty()) {
                kriteeriLista.add("varaus_id = " + varausidTF.getText().toLowerCase());
            }

            if (!asiakasidTF.getText().isEmpty()) {
                kriteeriLista.add("asiakas_id = " + asiakasidTF.getText());
            }

            if (!asiakasnimiTF.getText().isEmpty()) {
                kriteeriLista.add("LOWER(asiakas) LIKE '%" + asiakasnimiTF.getText().toLowerCase() + "%'");
            }

            if (!mokinNimiTF.getText().isEmpty()) {
                kriteeriLista.add("LOWER(mökki) LIKE '%" + mokinNimiTF.getText().toLowerCase() + "%'");
            }
            if (!TFAlkuVuosille.getText().isEmpty()){
                String formatoituAlkuPaiva = null;
                try {
                    LocalDateTime alkupvm = LocalDateTime.parse(TFAlkuVuosille.getText() + "-" + TFAlkuKuukausille.getText() + "-" + TFAlkuPaivalle.getText() + " 15:00:00", sqlKoodiksiFormatter);
                    formatoituAlkuPaiva = ("'" + alkupvm.format(sqlKoodiksiFormatter) + "'");
                } catch (Exception ex) {
                    main.errorPopUp("Tarkista varauksen alkupäivä!");
                    wirhe=true;
                }
                kriteeriLista.add("alkupaiva >= " + formatoituAlkuPaiva);
            }
            if (!TFLoppuVuosille.getText().isEmpty()){
                String formatoituLoppuPaiva = null;
                try {
                    LocalDateTime loppupvm = LocalDateTime.parse(TFLoppuVuosille.getText() + "-" + TFLoppuKuukausille.getText() + "-" + TFLoppuPaivalle.getText() + " 12:00:00", sqlKoodiksiFormatter);
                    formatoituLoppuPaiva = ("'" + loppupvm.format(sqlKoodiksiFormatter) + "'");
                } catch (Exception ex) {
                    main.errorPopUp("Tarkista varauksen loppumispäivä!");
                    wirhe=true;
                }
                kriteeriLista.add("loppupaiva <= " + formatoituLoppuPaiva);
            }
            if (!wirhe) {
                String kriteerit = String.join(" AND ", kriteeriLista);
                laskuMetodi(etsintaStage, main.connect.searchForStuff("laskutustiedot", kriteerit));
            }
        });
        Button kotiNappi = main.kotiNappain(etsintaStage);
        paneeliEtsintaKriteereille.getChildren().addAll(alkuHopina, varausidKriteeri, varausidTF, asiakasidKriteeri, asiakasidTF, asiakasnimiKriteeri, asiakasnimiTF,
                mokinNimiKriteeri, mokinNimiTF, alkuPrompt, paneeliVarauksenAlulle, loppuPrompt, paneeliVarauksenLopulle, etsi);
        BPLaskunEtsinnalle.setCenter(paneeliEtsintaKriteereille);
        BPLaskunEtsinnalle.setLeft(kotiNappi);
        Scene scene = new Scene(BPLaskunEtsinnalle);
        etsintaStage.setTitle("Mökin haku");
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