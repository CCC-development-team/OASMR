/*
 *  Copyright (c) 2019. CCC-Development-Team
 *  Licensed under the Apache License, Version 2.0 (the "License");
 *  you may not use this file except in compliance with the License.
 *  You may obtain a copy of the License at
 *
 *        http://www.apache.org/licenses/LICENSE-2.0
 *
 *  Unless required by applicable law or agreed to in writing, software
 *  distributed under the License is distributed on an "AS IS" BASIS,
 *  WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 *  See the License for the specific language governing permissions and
 *  limitations under the License.
 */

package fr.ensicaen.ecole.oasmr.app.controller.node;

import com.jfoenix.controls.JFXButton;
import com.jfoenix.controls.JFXChipView;
import com.jfoenix.controls.JFXListView;
import fr.ensicaen.ecole.oasmr.app.Config;
import fr.ensicaen.ecole.oasmr.app.view.NodesModel;
import fr.ensicaen.ecole.oasmr.app.view.View;
import fr.ensicaen.ecole.oasmr.lib.network.exception.ExceptionPortInvalid;
import fr.ensicaen.ecole.oasmr.supervisor.node.NodeData;
import fr.ensicaen.ecole.oasmr.supervisor.node.Tag;
import fr.ensicaen.ecole.oasmr.supervisor.node.command.request.RequestGetAllTags;
import fr.ensicaen.ecole.oasmr.supervisor.node.command.request.RequestGetNodes;
import fr.ensicaen.ecole.oasmr.supervisor.request.RequestManager;
import fr.ensicaen.ecole.oasmr.supervisor.request.RequestManagerFlyweightFactory;
import javafx.collections.FXCollections;
import javafx.collections.ListChangeListener;
import javafx.collections.ObservableList;
import javafx.collections.ObservableSet;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

public class NodeListController extends View {

    @FXML
    JFXButton refreshBtn;

    @FXML
    JFXChipView<String> filter;

    @FXML
    JFXListView nodeListView;

    private RequestManager requestManager = null;
    private Config config = null;
    private NodesModel nodesModel = null;

    public NodeListController(View parent) throws IOException {
        super("NodeList", parent);
        onCreate();
    }

    @Override
    public void onCreate() {
        refreshBtn.setOnAction(e -> {
            parent.start();
        });
        nodeListView.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        nodeListView.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        nodeListView.getSelectionModel().getSelectedItems().addListener((ListChangeListener) change -> {
            change.next();
            for(Object n : change.getRemoved()){
                nodesModel.removeCurrentNodes((NodeData) n);
            }
            for(Object n : change.getAddedSubList()){
                nodesModel.addCurrentNodes((NodeData) n);
            }
        });
        filter.getChips().addListener((ListChangeListener<? super String>) change -> {
            ObservableList<? extends String> list = change.getList();
            ObservableSet<NodeData> filterList;
            if (list.isEmpty()) {
                filterList = nodesModel.getAllNodeData();
            } else {
                List<Tag> tags = list.stream().map(Tag::new).collect(Collectors.toList());
                filterList = filterNodeData(nodesModel.getAllNodeData(), tags);
            }
            nodeListView.getItems().clear();
            nodeListView.getItems().addAll(filterList);
        });
    }

    private ObservableSet<NodeData> filterNodeData(ObservableSet<NodeData> nodeDataList, List<Tag> tags) {
        return FXCollections.observableSet(nodeDataList.stream().filter(n -> n.getTags().containsAll(tags)).collect(Collectors.toSet()));
    }

    @Override
    public void onStart() {

        if (requestManager == null && nodesModel == null) {
            try {
                config = Config.getInstance();
                requestManager = RequestManagerFlyweightFactory.getInstance().getRequestManager(InetAddress.getByName(config.getIP()), config.getPort());
                nodesModel = NodesModel.getInstance();
            } catch (ExceptionPortInvalid | UnknownHostException exceptionPortInvalid) {
                exceptionPortInvalid.printStackTrace();
            }
        }

        try {
            NodeData[] nodeList = (NodeData[]) requestManager.sendRequest(new RequestGetNodes());
            nodesModel.update(nodeList);
        } catch (Exception e) {
            e.printStackTrace();
        }
        nodeListView.getItems().clear();
        nodeListView.getItems().addAll(nodesModel.getAllNodeData());
        try {
            updateTags();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void updateTags() throws Exception {
        filter.getSuggestions().clear();
        Tag[] tags = (Tag[]) requestManager.sendRequest(new RequestGetAllTags());
        List<Tag> tagArrayList = Arrays.asList(tags);
        filter.getSuggestions().addAll(tagArrayList.stream().map(Tag::getName).collect(Collectors.toList()));
    }

    @Override
    protected void onUpdate() {
        try {
            NodeData[] nodeList = (NodeData[]) requestManager.sendRequest(new RequestGetNodes());
            nodesModel.update(nodeList);
            updateTags();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onStop() {
        config = null;
        requestManager = null;
        nodesModel = null;
    }

}
