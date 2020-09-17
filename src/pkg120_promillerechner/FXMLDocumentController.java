/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package pkg120_promillerechner;

import java.awt.Desktop;
import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.Spinner;
import javafx.scene.control.SpinnerValueFactory;
import javafx.scene.control.TextField;

/**
 *
 * @author nicis
 */
public class FXMLDocumentController implements Initializable {

    @FXML
    private ListView<String> lvResult;
    @FXML
    private Button btnBerechnen;
    @FXML
    private TextField tbKG;
    @FXML
    private TextField tbCM;
    @FXML
    private ComboBox<String> cbSex;
    @FXML
    private Spinner<Integer> spHours;
    @FXML
    private TextField tbVol;
    @FXML
    private TextField tbLiter;
    @FXML
    private TextField tbAge;

    @FXML
    private void handleButtonAction(ActionEvent event) throws LeereTextBoxException {

        if (tbVol.getText() == null || tbLiter.getText() == null || tbAge.getText() == null || tbKG.getText() == null || tbCM.getText() == null) {
            throw new LeereTextBoxException("bitte alle Boxen ausfüllen.");
        }

        if (Integer.parseInt(tbAge.getText()) < 18) {
            executeTask("https://www.bag.admin.ch/bag/de/home/strategie-und-politik/politische-auftraege-und-aktionsplaene/politische-auftraege-zur-alkoholpraevention/alkoholpolitik/gesetzgebung.html");
        } else {
            double alcoholGrams = getAlcoholInGramm(tbLiter.getText(), tbVol.getText());
            double bodywater = getAnteilKoerperfluessigkeit(tbCM.getText(), tbKG.getText(), tbAge.getText());
            double Promille = getBAK(alcoholGrams, bodywater);
            Promille = round(Promille, 2);

            String ausgabe = Promille + "‰: " + cbSex.getValue() + " drank " + tbLiter.getText() + " liter of alcohol with " + tbVol.getText() + " %Vol ";
            lvResult.getItems().add(ausgabe);
        }

    }

    @Override
    public void initialize(URL url, ResourceBundle rb) {
        spHours.setValueFactory(new SpinnerValueFactory.IntegerSpinnerValueFactory(1, 24, 1));

        cbSex.getItems().add("male");
        cbSex.getItems().add("female");
    }

    private double getAlcoholInGramm(String liter, String VOL) {
        double ml = Double.parseDouble(liter);
        ml = ml * 1000;
        double vol = Double.parseDouble(VOL);

        double alc = ml * (vol / 100) * 0.8;

        return alc;

    }

    private double getAnteilKoerperfluessigkeit(String groesse, String gewicht, String alter) {

        int age = Integer.parseInt(alter);
        int weight = Integer.parseInt(gewicht);
        int height = Integer.parseInt(groesse);
        double bodywater;

        if ("male".equals(cbSex.getValue())) {
            bodywater = 2.447 - 0.09516 * age + 0.1074 * height + 0.3362 * weight;
        } else {

            bodywater = -2.097 + 0.1069 * height + 0.2466 * weight;
        }

        return bodywater;

    }

    private double getBAK(double alcInGramm, double bodywater) {
        int hours = spHours.getValue();
        hours = hours / 10;

        double bak = ((alcInGramm * 0.8) / bodywater) - hours;

        return bak;
    }

    private double round(final double value, final int frac) {
        return Math.round(Math.pow(10.0, frac) * value) / Math.pow(10.0, frac);
    }

    private void executeTask(String url) {
        try {
            Desktop.getDesktop().browse(new URI(url));
        } catch (URISyntaxException | IOException ex) {
            Logger.getLogger(FXMLDocumentController.class.getName()).log(Level.SEVERE, null, ex);
        }
    }

}
