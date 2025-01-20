module student.example.mokkivarausjarjestelmajava_ht {
    requires javafx.controls;
    requires javafx.fxml;
    requires java.sql;
    requires itextpdf;

    opens student.example.mokkivarausjarjestelmajava_ht to javafx.fxml;
    exports student.example.mokkivarausjarjestelmajava_ht;
}