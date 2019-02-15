package fr.ensicaen.ecole.oasmr.app.controller;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXListView;
import fr.ensicaen.ecole.oasmr.app.Config;
import fr.ensicaen.ecole.oasmr.app.view.NodesModel;
import fr.ensicaen.ecole.oasmr.app.view.View;
import fr.ensicaen.ecole.oasmr.lib.network.exception.ExceptionPortInvalid;
import fr.ensicaen.ecole.oasmr.supervisor.node.NodeBean;
import fr.ensicaen.ecole.oasmr.supervisor.node.Tag;
import fr.ensicaen.ecole.oasmr.supervisor.node.request.RequestGetAllTags;
import fr.ensicaen.ecole.oasmr.supervisor.request.RequestManager;
import fr.ensicaen.ecole.oasmr.supervisor.request.RequestManagerFlyweightFactory;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.SelectionMode;
import javafx.scene.layout.VBox;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeListController extends View {

    @FXML
    JFXButton refreshBtn;

    @FXML
    JFXChipView filter;

    @FXML
    VBox vbox;

    @FXML
    VBox vboxList;

    @FXML
    JFXListView nodeListView;

    private NodesModel nodesModel;
    private RequestManager requestManager;
    private MainController mainController;

    public NodeListController() throws IOException {
        super("NodeList");
    }

    public void setDataModel(NodesModel nodesModel) {
        this.nodesModel = nodesModel;
    }

    public void setMainController(MainController mainController) {
        this.mainController = mainController;
    }


    public void refreshNodes(ActionEvent actionEvent) throws Exception {
        mainController.onStart();
    }

    @Override
    public void onCreate() {
        try {
            Config config = Config.getInstance();
            requestManager = RequestManagerFlyweightFactory.getInstance().getRequestManager(InetAddress.getByName(config.getIP()), config.getPort());
        } catch (ExceptionPortInvalid | UnknownHostException exceptionPortInvalid) {
            exceptionPortInvalid.printStackTrace();
        }
    }

    @Override
    public void onStart() {
        nodeListView.getItems().clear();
        nodeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        nodeListView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        nodeListView.setOnMouseClicked(event -> {
            ObservableList<NodeBean> l = nodeListView.getSelectionModel().getSelectedItems();
            nodesModel.getCurrentNodeBeans().clear();
            for (NodeBean node : l) {
                nodesModel.addCurrentNodes(node);
            }
        });
        nodeListView.setItems(nodesModel.getAllNodeBeans());
        try {
            Tag[] tags = (Tag[]) requestManager.sendRequest(new RequestGetAllTags());
            filter.getSuggestions().addAll(tags);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {

    }
}