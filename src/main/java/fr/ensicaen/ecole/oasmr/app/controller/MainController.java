package fr.ensicaen.ecole.oasmr.app.controller;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.control.SplitPane;
import javafx.scene.layout.BorderPane;
import org.controlsfx.control.MasterDetailPane;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class MainController implements Initializable {

    @FXML
    SplitPane mainPane;

    @Override
    public void initialize(URL location, ResourceBundle resources) {
        final FXMLLoader loaderLeft = new FXMLLoader(getClass().getResource("/fr/ensicaen/ecole/oasmr/app/NodeTree.fxml"));
        final FXMLLoader loaderRight = new FXMLLoader(getClass().getResource("/fr/ensicaen/ecole/oasmr/app/NodeView.fxml"));
        try {
            mainPane.getItems().add(loaderLeft.load());
            mainPane.getItems().add(loaderRight.load());
        } catch (IOException e) {
            e.printStackTrace();
        }
    }



}
