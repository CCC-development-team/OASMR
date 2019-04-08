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

import eu.hansolo.tilesfx.Tile;
import eu.hansolo.tilesfx.TileBuilder;
import fr.ensicaen.ecole.oasmr.app.Config;
import fr.ensicaen.ecole.oasmr.app.view.NodesModel;
import fr.ensicaen.ecole.oasmr.app.view.View;
import fr.ensicaen.ecole.oasmr.lib.network.exception.ExceptionPortInvalid;
import fr.ensicaen.ecole.oasmr.lib.system.CommandGetAvailableRAM;
import fr.ensicaen.ecole.oasmr.lib.system.CommandGetTotalRAM;
import fr.ensicaen.ecole.oasmr.supervisor.node.command.request.RequestExecuteCommand;
import fr.ensicaen.ecole.oasmr.supervisor.request.RequestManager;
import fr.ensicaen.ecole.oasmr.supervisor.request.RequestManagerFlyweightFactory;
import javafx.animation.KeyFrame;
import javafx.animation.Timeline;
import javafx.fxml.FXML;
import javafx.scene.layout.VBox;
import javafx.util.Duration;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;

public class NodeRamInfoController extends View {

    @FXML
    VBox ramVBox;

    private RequestManager requestManager = null;
    private Config config = null;
    private NodesModel nodesModel = null;

    private Tile ramGraph;
    private double totalRam = 0;

    public NodeRamInfoController(View parent) throws IOException {
        super("NodeRamInfo", parent);
        onCreate();
    }

    @Override
    public void onCreate() {
        ramGraph = TileBuilder.create()
                .skinType(Tile.SkinType.CIRCULAR_PROGRESS)
                .prefSize(150, 150)
                .title("RAM usage")
                .valueVisible(false)
                .build();
        ramVBox.getChildren().add(ramGraph);
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
        if (nodesModel.getSelectedAmount() == 1) {
            try {
                totalRam = (double) (long) requestManager.sendRequest(
                        new RequestExecuteCommand(
                                nodesModel.getCurrentNodeData().iterator().next().getId(),
                                new CommandGetTotalRAM()
                        ));
                ramGraph.setMaxValue(totalRam);
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    protected void onUpdate() {
        if (nodesModel.getSelectedAmount() == 1) {
            try {
                ramGraph.setValue(totalRam - (double) (long) requestManager.sendRequest(
                        new RequestExecuteCommand(
                                nodesModel.getCurrentNodeData().iterator().next().getId(),
                                new CommandGetAvailableRAM()
                        )));
            } catch (Exception e1) {
                e1.printStackTrace();
            }
        }
    }

    @Override
    public void onStop() {
        config = null;
        requestManager = null;
        nodesModel = null;
    }
}