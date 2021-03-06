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

import com.jfoenix.animation.alert.JFXAlertAnimation;
import com.jfoenix.controls.*;
import dev.jaaj.oasmr.lib.FXClassInitializer;
import dev.jaaj.oasmr.lib.command.Command;
import dev.jaaj.oasmr.lib.network.exception.ExceptionPortInvalid;
import dev.jaaj.oasmr.app.Config;
import dev.jaaj.oasmr.app.view.NodesModel;
import dev.jaaj.oasmr.app.view.View;
import dev.jaaj.oasmr.supervisor.node.NodeData;
import dev.jaaj.oasmr.supervisor.node.command.request.RequestExecuteCommand;
import dev.jaaj.oasmr.supervisor.request.RequestGetCommands;
import dev.jaaj.oasmr.supervisor.request.RequestManager;
import dev.jaaj.oasmr.supervisor.request.RequestManagerFlyweightFactory;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.control.ScrollPane;
import javafx.scene.layout.FlowPane;
import javafx.stage.Modality;
import javafx.stage.Stage;

import java.io.IOException;
import java.io.Serializable;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Set;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.Future;

public class NodeCommandController extends View {

    @FXML
    private FlowPane commandFlowPane;

    @FXML
    private JFXTextField searchField;

    private RequestManager requestManager = null;
    private Config config = null;
    private NodesModel nodesModel = null;

    private Set<Class<? extends Command>> commands;

    public NodeCommandController(View parent) throws IOException {
        super("NodeCommand", parent);
        onCreate();
    }

    @Override
    public void onCreate() {


        searchField.setOnKeyReleased(event -> {
            try {
                searchCommand(searchField.getText());
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }

        });


    }

    @Override
    protected void onStart() {

        if (requestManager == null && nodesModel == null) {
            try {
                config = Config.getInstance();
                requestManager = RequestManagerFlyweightFactory.getInstance().getRequestManager(InetAddress.getByName(config.getIP()), config.getPort());
                nodesModel = NodesModel.getInstance();
            } catch (ExceptionPortInvalid | UnknownHostException exceptionPortInvalid) {
                exceptionPortInvalid.printStackTrace();
            }
        }


        if (nodesModel.getSelectedAmount() > 0) {
            commandFlowPane.getChildren().clear();
            Future<? extends Serializable> reponseCommandList = requestManager.aSyncSendRequest(new RequestGetCommands());
            try {
                commands = (Set<Class<? extends Command>>) reponseCommandList.get();
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
            try {
                searchCommand("");
            } catch (InterruptedException e) {
                e.printStackTrace();
            } catch (ExecutionException e) {
                e.printStackTrace();
            }
        }

    }

    private void searchCommand(String search) throws InterruptedException, java.util.concurrent.ExecutionException {

        commandFlowPane.getChildren().clear();
        if (search.trim().equals("")) {
            for (Class<? extends Command> command : commands) {
                JFXButton jeej = initButtonFromClass(command);
                commandFlowPane.getChildren().add(jeej);
            }
        } else {
            for (Class<? extends Command> command : commands) {
                if (command.getSimpleName().split("Command")[1].toLowerCase().contains(search.toLowerCase())) {
                    JFXButton jeej = initButtonFromClass(command);
                    commandFlowPane.getChildren().add(jeej);
                }
            }
        }
    }


    @Override
    protected void onUpdate() {

    }

    private JFXButton initButtonFromClass(Class<? extends Command> command) {
        JFXButton jeej = new JFXButton(command.getSimpleName().split("Command")[1]);
        jeej.setStyle("-jfx-button-type: RAISED;-fx-background-color: #FF6026; -fx-text-fill: white;");
        jeej.setOnAction(e -> {
            Stage stage = (Stage) commandFlowPane.getScene().getWindow();

            new FXClassInitializer(stage, command).initFromClass(newObject -> {
                Command c = (Command) newObject;
                String response;
                for (NodeData n : nodesModel.getCurrentNodeData()) {
                    try {
                        response = (String) requestManager.sendRequest(
                                new RequestExecuteCommand(n.getId(), c));
                        System.out.println(response);
                    } catch (Exception e1) {
                        e1.printStackTrace();
                        response = e1.toString();
                    }
                    JFXDialogLayout layout = new JFXDialogLayout();
                    layout.setHeading(new Label("Response"));
                    ScrollPane scrollPane = new ScrollPane();
                    scrollPane.setContent(new Label(response));
                    layout.setBody(scrollPane);

                    JFXAlert alert = new JFXAlert<>(stage);
                    alert.setOverlayClose(true);

                    alert.setAnimation(JFXAlertAnimation.CENTER_ANIMATION);
                    alert.setContent(layout);
                    alert.initModality(Modality.NONE);

                    JFXButton button = new JFXButton("close");
                    button.setOnAction(event -> alert.close());
                    layout.setActions(button);
                    alert.show();
                }

            });
        });
        return jeej;
    }

    @Override
    public void onStop() {
        config = null;
        requestManager = null;
        nodesModel = null;
    }

}
