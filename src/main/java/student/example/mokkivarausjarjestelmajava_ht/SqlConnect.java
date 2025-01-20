package student.example.mokkivarausjarjestelmajava_ht;

import java.sql.*;


/**
 * TARVITSEE JCDB AJURIN EXTERNAL LIBRARYYN!!!!
 * 1. Lataa täältä ajuri: https://dev.mysql.com/downloads/connector/j/ (Platfrom independent)
 * 2. Pura ajuri sellaiseen paikkaan mistä löydät
 * 3. Mene ylhäältä intelliJ File -> project structure... -> project settings -> libraries
 * 4. Paina pientä + symbolia ( tai Alt + insert) valitse siitä syntyvästä valikosta "Java"
 * 5. Etsi purkamasi tiedostoa ja valitse mysql-connector-j.jar tiedosto ja klikkaa siitä
 * 6. Nyt pitäis toimia : )
 *
 */
public class SqlConnect {
    Main main;
    private String user;
    private String password;

    public String getUser() {
        return user;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }
    Connection con = null;
    Statement stmt = null;
    ResultSet rs = null;

    /**
     *
     * query sql kieltä, mieluiten SELECT jotta ei ruveta rikkomaan tietokantaa : )
     * @return palauttaa Resultset, eli se pitää sen jälkeen rikkoa rs.getString(int colum) metodilla,
     * int colum tarkoittaa mitä kohtaa taulukosta haluaa ulos
     * ---------------------------------------------------------------------------------------------------
     * Tuo on tällä hetkellä pistetty mulla olevaan random traffic tietokantaan, pitää lisätä dataa
     * vn tietokantaa että pääsee testaamaan, pyritään pitämään portti, salasana ja user samana kaikilla.
     */
    public Connection createConnection() {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection(
                    "jdbc:mysql://localhost:3306/vn", this.user, this.password
            );
            System.out.println("Connection successful!");
            return this.con;
        } catch (ClassNotFoundException | SQLException e) {
            main.errorPopUp("Virhe tietokantaan yhdistämisessä\n" + e);
        }
        return con;
    }

    public ResultSet executeQuery(String query) {
        if (this.con == null) {
            main.errorPopUp("Tietokantaa ei yhdistetty. Ota yhteyttä ryhmään 5");
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            main.errorPopUp("Virhe SQL komennossa\nOta yhteyttä ryhmään 5!\n" + e);
        }
        return null;
    }
    public void closeResources() {
        try {
            if (rs != null) {
                this.rs.close();
            }
            if (stmt != null) {
                this.stmt.close();
            }
            if (con != null) {
                this.con.close();
            }
        } catch (SQLException e) {
            main.errorPopUp("Virhe! Ota yhteyttä ryhmään 5!" + e);
        }
    }
    public void insertData(String table, String values, String data){
        String query = "INSERT INTO "+table+"("+values+")"+
                " VALUES (" + data + ")";
        try {
            this.stmt = con.createStatement();
            this.stmt.executeUpdate(query);
            this.con.close();
        } catch (SQLException e) {
            main.errorPopUp("Virhe! Tarkista syötteet!" + e);
        }
    }
    public ResultSet searchForStuff(String table, String criteria){
        String query = "SELECT * FROM " + table + " WHERE " + criteria + ";";
        if (this.con == null) {
            main.errorPopUp("Tietokantaa ei yhdistetty, ota yhteyttä ryhmään 5!");
        }
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery(query);
            return rs;
        } catch (SQLException e) {
            main.errorPopUp("Virhe tietojen hakemisessa tietokannasta\n " + e);
        }
        return null;
    }
    public void updateTable (String table, String rivi, String data, String where) {
        String query = "UPDATE " + table + " SET " + rivi + " = " + data + " WHERE " + where + ";";
        if (this.con == null) {
            main.errorPopUp("Tietokantaa ei yhdistetty, ota yhteyttä ryhmään 5!");
        }
        try {
            Statement stmt = con.createStatement();
            stmt.executeUpdate(query);
        } catch (SQLException e) {
            main.errorPopUp("Virhe tietokannan päivittämisessä\nOta yhteyttä ryhmään 5!" + e);
        }
    }
    public void deleteStuff (String table, String minkaMukaanPoistetaan, String poistettavaArvo) {
        String query = "DELETE FROM " + table + " WHERE " + minkaMukaanPoistetaan + " = " + poistettavaArvo;
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            this.con = DriverManager.getConnection("jdbc:mysql://localhost:3306/vn", this.user, this.password);
            this.stmt = con.createStatement();
            this.stmt.executeUpdate(query);
            this.con.close();
        } catch (ClassNotFoundException | SQLException e) {
            main.errorPopUp("Virhe tietojen poistamisessa!\nOta yhteyttä ryhmään 5!" + e);
        }
    }
    public  SqlConnect(Main main){
        this.main = main;
    }

        /**
         *
         * @param user Käyttäjä, jos haluaa pelleillä niiden kanssa niin eiku vaan, default "root"
         * @param password :I mulla default "1234", pitää olla Stringinä
         */
    public SqlConnect(String user, String password, Main main) {
            this.user = user;
            this.password = password;
            this.main = main;
        }

}
