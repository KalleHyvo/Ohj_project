package student.example.mokkivarausjarjestelmajava_ht;

import javafx.application.Application;
import javafx.geometry.Insets;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.scene.layout.BorderPane;
import javafx.scene.layout.GridPane;
import javafx.scene.layout.VBox;
import javafx.scene.text.Text;
import javafx.scene.text.TextAlignment;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.sql.SQLException;


public class Main extends Application {
    private Mokki mokki = new Mokki(this);
    private Alue alue = new Alue(this);
    private Palvelu palvelu = new Palvelu(this);
    private Varaus varaus = new Varaus(this);
    private Lasku lasku = new Lasku(this);
    private Asiakas asiakas = new Asiakas(this);
    private BillPDFer billPDFer = new BillPDFer(this);
    private VarausHandler varausHandler = new VarausHandler(this, varaus);
    private BillHandler billHandler = new BillHandler(this, lasku, billPDFer);
    private CottageHandler cottageHandler = new CottageHandler(this, mokki);
    private CustomerHandler customerHandler = new CustomerHandler(this, asiakas);
    private AlueHandler alueHandler = new AlueHandler(this, alue);
    private PalveluHandler palveluHandler = new PalveluHandler(this, palvelu);

    public SqlConnect connect = new SqlConnect(this);
    public Main() {
    }

    public static void main(String[] args) {
        launch(args);
    }

    /**
     * Käynnistää ohjelman
     * @param primaryStage Stage, jossa käyttöliittymä pyörii
     */
    @Override
    public void start(Stage primaryStage) {
        /**
         * Käynnistetään main menu
         */
        showLoginScreen(primaryStage);
    }

    /**
     * Luo main menun ohjelmalle tai palauttaa ohjelman alkutilanteeseen.
     * @param primaryStage Stage, jossa käyttöliittymä pyörii
     */
    public void mainMenuMaker(Stage primaryStage) {
        connect.createConnection();
        BorderPane paneeliAloitusNaytolle = new BorderPane();
        paneeliAloitusNaytolle.setPrefSize(500, 500);
        paneeliAloitusNaytolle.setPadding(new Insets(10, 10, 10, 10));
        VBox paneeliKeskiNapeille = new VBox(10);
        paneeliKeskiNapeille.setAlignment(Pos.CENTER);
        Button laskujenKatsomisNappi = new Button("Laskut");
        Button mokkiNappi = new Button("Mökit");
        Button asiakasNappi = new Button("Asiakkaat");
        Button alueNappi = new Button("Alueet");
        Button palveluNappi = new Button("Palvelut");
        Button varauksetNappi = new Button("Varaukset");
        paneeliKeskiNapeille.getChildren().addAll(laskujenKatsomisNappi, mokkiNappi, asiakasNappi, alueNappi, palveluNappi,
                varauksetNappi);
        paneeliAloitusNaytolle.setCenter(paneeliKeskiNapeille);
        paneeliAloitusNaytolle.setLeft(kotiNappain(primaryStage));
        /**
         * Tehdään toiminnallisuus nappeihin, kaikki napit muuttavat stagen oman handlerinsa mukaiseen stageen
         * ja hakee tiedot siihen SQL tietokannasta
         */
        laskujenKatsomisNappi.setOnAction(e->{
            billHandler.laskuMetodi(primaryStage, connect.executeQuery("SELECT lasku_id FROM laskutustiedot ORDER BY lasku_id"));
        });
        mokkiNappi.setOnAction(e->{
            cottageHandler.mokkiMetodi(primaryStage, connect.executeQuery("SELECT mokki_id, mokkinimi FROM mokki ORDER BY mokki_id"));
        });
        asiakasNappi.setOnAction(e->{
            customerHandler.asiakasMetodi(primaryStage, connect.executeQuery("SELECT asiakas_id FROM asiakas ORDER BY asiakas_id"));
        });
        alueNappi.setOnAction(e->{
            alueHandler.alueMetodi(primaryStage, connect.executeQuery("SELECT alue_id, nimi FROM alue ORDER BY alue_id"));
        });
        palveluNappi.setOnAction(e->{
            palveluHandler.palveluMetodi(primaryStage, connect.executeQuery("SELECT palvelu_id, nimi FROM palvelu ORDER BY palvelu_id"));
        });
        varauksetNappi.setOnAction(e->{
            varausHandler.varausMetodi(primaryStage, connect.executeQuery("SELECT varaus_id FROM varaus ORDER BY varaus_id"));
        });

        Scene scene = new Scene(paneeliAloitusNaytolle);
        primaryStage.setScene(scene);
        primaryStage.setTitle("Mökkienvarausohjelma 1.0");
        primaryStage.show();
    }

    /**
     * Metodi, jolla pystyy helposti tekemään popup ilmoituksia virheestä
     * @param prompt virheviesti, joka käyttäjälle näytetään
     */
    public void errorPopUp(String prompt){
        BorderPane paneeliPopUpille = new BorderPane();
        paneeliPopUpille.setPrefSize(300, 300);
        paneeliPopUpille.setPadding(new Insets(10, 10, 10, 10));
        Text errori = new Text(prompt);
        errori.setTextAlignment(TextAlignment.CENTER);
        Button okNappi = new Button("OK");
        okNappi.setAlignment(Pos.CENTER);
        paneeliPopUpille.setTop(errori);
        paneeliPopUpille.setBottom(okNappi);
        Scene scene = new Scene(paneeliPopUpille);
        Stage popUpStage = new Stage();
        okNappi.setOnAction(e->{
            popUpStage.close();
        });
        popUpStage.setTitle("Error");
        popUpStage.setScene(scene);
        popUpStage.show();
    }
    private void showLoginScreen(Stage primaryStage) {
        Stage loginStage = new Stage();
        loginStage.initModality(Modality.APPLICATION_MODAL);
        loginStage.setTitle("Login");

        GridPane loginPane = new GridPane();
        loginPane.setAlignment(Pos.CENTER);
        loginPane.setPadding(new Insets(10, 10, 10, 10));
        loginPane.setHgap(10);
        loginPane.setVgap(10);

        Label userLabel = new Label("Username:");
        TextField userField = new TextField();
        Label passLabel = new Label("Password:");
        PasswordField passField = new PasswordField();
        Button loginButton = new Button("Login");

        loginPane.add(userLabel, 0, 0);
        loginPane.add(userField, 1, 0);
        loginPane.add(passLabel, 0, 1);
        loginPane.add(passField, 1, 1);
        loginPane.add(loginButton, 1, 2);

        loginButton.setOnAction(e -> {
            String username = userField.getText();
            String password = passField.getText();
            connect.setPassword(password);
            connect.setUser(username);
            try{
                connect.createConnection();
                loginStage.close();
                mainMenuMaker(primaryStage);}
            catch (Exception ex) {
                Label errorLabel = new Label("Invalid username or password");
                errorLabel.setStyle("-fx-text-fill: red;");
                loginPane.add(errorLabel, 1, 3);}

        });

        Scene loginScene = new Scene(loginPane, 300, 200);
        loginStage.setScene(loginScene);
        loginStage.showAndWait();
    }




    /**
     * Luo napin, josta painamalla pääsee aloitusnäyttöön
     * @param primaryStage Stage, jota muokataan
     * @return Nappi, jossa lukee "koti" ja on toiminnallisuus
     */
    public Button kotiNappain(Stage primaryStage){
        Button kotiNappi = new Button("Koti");
        kotiNappi.setOnAction(e->{
            mainMenuMaker(primaryStage);
        });
        return kotiNappi;
    }
}
