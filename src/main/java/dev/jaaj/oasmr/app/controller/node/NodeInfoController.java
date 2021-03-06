/*
 * Copyright (c) 2019. JaaJ-dev
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package dev.jaaj.oasmr.app.controller.node;

import dev.jaaj.oasmr.lib.network.exception.ExceptionPortInvalid;
import dev.jaaj.oasmr.lib.system.CommandGetModel;
import dev.jaaj.oasmr.lib.system.CommandGetProcessor;
import dev.jaaj.oasmr.lib.system.CommandGetSystem;
import dev.jaaj.oasmr.app.Config;
import dev.jaaj.oasmr.app.view.NodesModel;
import dev.jaaj.oasmr.app.view.View;
import dev.jaaj.oasmr.supervisor.node.NodeData;
import dev.jaaj.oasmr.supervisor.node.command.request.RequestExecuteCommand;
import dev.jaaj.oasmr.supervisor.request.RequestManager;
import dev.jaaj.oasmr.supervisor.request.RequestManagerFlyweightFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeInfoController extends View {

    @FXML
    VBox nodeInfoVbox;

    private RequestManager requestManager = null;
    private Config config = null;
    private NodesModel nodesModel = null;

    public NodeInfoController(View parent) throws IOException {
        super("NodeInfo", parent);
        onCreate();
    }

    @Override
    public void onCreate() {

    }

    @Override
    protected void onStart() {

        if(requestManager == null && nodesModel == null){
            try {
                config = Config.getInstance();
                requestManager = RequestManagerFlyweightFactory.getInstance().getRequestManager(InetAddress.getByName(config.getIP()), config.getPort());
                nodesModel = NodesModel.getInstance();
            } catch (ExceptionPortInvalid | UnknownHostException exceptionPortInvalid) {
                exceptionPortInvalid.printStackTrace();
            }
        }

        nodeInfoVbox.getChildren().clear();

        if (nodesModel.getSelectedAmount() > 1) {
            //TODO : Differences between tag and multiselection
            nodeInfoVbox.getChildren().add(
                    new Label("Group of nodes")
            );
            for(NodeData n : nodesModel.getCurrentNodeData()){
                nodeInfoVbox.getChildren().add(
                        new Label(n.getName() + "(" + n.getNodeAddress() + ":" + n.getPort() + ")")
                );
            }
        } else if (nodesModel.getSelectedAmount() == 1) {
            NodeData node = nodesModel.getCurrentNodeData().iterator().next();
            nodeInfoVbox.getChildren().add(
                    new Label(node.getName())
            );
            nodeInfoVbox.getChildren().add(
                    new Label("IP : " + node.getNodeAddress() + ":" + node.getPort())
            );
            try {
                String model = (String) requestManager.sendRequest(
                        new RequestExecuteCommand(
                                nodesModel.getCurrentNodeData().iterator().next().getId(),
                                new CommandGetModel()
                        ));
                nodeInfoVbox.getChildren().add(
                        new Label("Model : " + model)
                );
                String system = (String) requestManager.sendRequest(
                        new RequestExecuteCommand(
                                nodesModel.getCurrentNodeData().iterator().next().getId(),
                                new CommandGetSystem()
                        ));
                nodeInfoVbox.getChildren().add(
                        new Label("System : " + system)
                );
                String cpu = (String) requestManager.sendRequest(
                        new RequestExecuteCommand(
                                nodesModel.getCurrentNodeData().iterator().next().getId(),
                                new CommandGetProcessor()
                        ));
                nodeInfoVbox.getChildren().add(
                        new Label("Processor : " + cpu)
                );
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onUpdate() {

    }

    @Override
    public void onStop() {
        config = null;
        requestManager = null;
        nodesModel = null;
    }

}
