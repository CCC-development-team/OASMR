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

import com.jfoenix.controls.*;
import com.jfoenix.controls.datamodels.treetable.RecursiveTreeObject;
import dev.jaaj.oasmr.lib.network.exception.ExceptionPortInvalid;
import dev.jaaj.oasmr.lib.system.CommandGetProcesses;
import dev.jaaj.oasmr.lib.system.CommandKillProcess;
import dev.jaaj.oasmr.app.Config;
import dev.jaaj.oasmr.app.view.NodesModel;
import dev.jaaj.oasmr.app.view.View;
import dev.jaaj.oasmr.supervisor.node.command.request.RequestExecuteCommand;
import dev.jaaj.oasmr.supervisor.request.RequestManager;
import dev.jaaj.oasmr.supervisor.request.RequestManagerFlyweightFactory;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.SelectionMode;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeTableColumn;
import javafx.scene.layout.VBox;

import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.*;

public class NodeTaskManagerController extends View {

    private NodesModel nodesModel = null;
    private RequestManager requestManager = null;
    private Config config = null;

    Timer t;
    private String toSearch;
    private ObservableList<InternalProcess> processesList;

    @FXML
    private
    JFXButton kill;

    @FXML
    private
    JFXTextField searchField;

    @FXML
    private
    VBox tableBox;



    NodeTaskManagerController(View parent) throws IOException {
        super("NodeTaskManager", parent);
        toSearch = "";
    }

    @Override
    public void onCreate() {

    }

    //TODO: essayer de garder la selection d'un rafraichissement à l'autre...

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
        processesList = FXCollections.observableArrayList();



        searchField.setOnKeyReleased(event -> {
                toSearch = searchField.getText();
                loadTable();

        });

        if (nodesModel.getSelectedAmount() > 1) {
            //TODO : Configure view for group
        } else if (nodesModel.getSelectedAmount() == 1) {
            loadTable();
            init();
        }
    }

    @Override
    protected void onUpdate() {
        loadTable();
    }


    private void loadTable() {
        if (nodesModel.getSelectedAmount() == 1) {
            CommandGetProcesses getProcesses = new CommandGetProcesses();

            HashMap<String, String>[] processes = new HashMap[0];

            try {
                processes = (HashMap<String, String>[]) requestManager.sendRequest(new RequestExecuteCommand(nodesModel.getCurrentNodeData().iterator().next().getId(), getProcesses));
            } catch (Exception e) {
                e.printStackTrace();
            }

            processesList.removeAll(processesList);

            for (HashMap<String, String> p : processes) {
                if (!toSearch.trim().equals("") && p.get("NAME").contains(toSearch)) {
                    processesList.add(new InternalProcess(p.get("PID"), p.get("CPU"), p.get("MEM"), p.get("NAME")));
                } else if (toSearch.trim().equals("")) {
                    processesList.add(new InternalProcess(p.get("PID"), p.get("CPU"), p.get("MEM"), p.get("NAME")));
                }
            }
        }
    }

    private void init() {
        tableBox.getChildren().clear();

        //ObservableList<InternalProcess> processesList = FXCollections.observableArrayList();

        final TreeItem<InternalProcess> root = new RecursiveTreeItem<>(processesList, RecursiveTreeObject::getChildren);

        JFXTreeTableView processesTable = new JFXTreeTableView(root);

        JFXTreeTableColumn<InternalProcess, String> nameCol = new JFXTreeTableColumn<>("Name");
        nameCol.setPrefWidth(300);
        nameCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<InternalProcess, String> param) -> {
            if (nameCol.validateValue(param)) {
                return param.getValue().getValue().Name;
            } else {
                return nameCol.getComputedValue(param);
            }
        });


        JFXTreeTableColumn<InternalProcess, String> cpuCol = new JFXTreeTableColumn<>("CPU usage");
        cpuCol.setPrefWidth(150);
        cpuCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<InternalProcess, String> param) -> {
            if (cpuCol.validateValue(param)) {
                return param.getValue().getValue().Cpu;
            } else {
                return cpuCol.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<InternalProcess, String> memCol = new JFXTreeTableColumn<>("Memory");
        memCol.setPrefWidth(150);
        memCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<InternalProcess, String> param) -> {
            if (memCol.validateValue(param)) {
                return param.getValue().getValue().Mem;
            } else {
                return memCol.getComputedValue(param);
            }
        });

        JFXTreeTableColumn<InternalProcess, String> pidCol = new JFXTreeTableColumn<>("PID");
        pidCol.setPrefWidth(150);
        pidCol.setCellValueFactory((TreeTableColumn.CellDataFeatures<InternalProcess, String> param) -> {
            if (pidCol.validateValue(param)) {
                return param.getValue().getValue().Pid;
            } else {
                return pidCol.getComputedValue(param);
            }
        });

        processesTable.setShowRoot(false);
        processesTable.setEditable(false);
        processesTable.getColumns().setAll(nameCol, cpuCol, memCol, pidCol);
        processesTable.getSelectionModel().setSelectionMode(SelectionMode.MULTIPLE);
        processesTable.setMaxSize(Double.MAX_VALUE, Double.MAX_VALUE);
        tableBox.getChildren().addAll(processesTable);


        kill.setOnAction(event -> {
            for (Object u : processesTable.getSelectionModel().getSelectedItems()) {
                if (u instanceof TreeItem) {
                    if (((TreeItem) u).getValue() instanceof InternalProcess) {
                        System.out.println(((InternalProcess) ((TreeItem) u).getValue()).getPid());
                        CommandKillProcess killProcess = new CommandKillProcess(((InternalProcess) ((TreeItem) u).getValue()).getPid());
                        try {
                            requestManager.sendRequest(killProcess);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        });

    }

    @Override
    public void onStop() {
        config = null;
        requestManager = null;
        nodesModel = null;
    }

    private static final class InternalProcess extends RecursiveTreeObject<InternalProcess> {
        StringProperty Pid;
        StringProperty Cpu;
        StringProperty Mem;
        StringProperty Name;

        InternalProcess(String pid, String cpu, String mem, String name) {
            Pid = new SimpleStringProperty(pid);
            if (cpu.length() > 4) {
                Cpu = new SimpleStringProperty(cpu.substring(0, 4) + "%");
            } else {
                Cpu = new SimpleStringProperty(cpu + "%");
            }
            if (mem.length() > 4) {
                Mem = new SimpleStringProperty(mem.substring(0, 4) + "%");
            } else {
                Mem = new SimpleStringProperty(mem + "%");
            }
            Name = new SimpleStringProperty(name);
        }

        @Override
        public boolean equals(Object o) {
            if (this == o) return true;
            if (o == null || getClass() != o.getClass()) return false;
            InternalProcess that = (InternalProcess) o;
            return Objects.equals(Pid, that.Pid) &&
                    Objects.equals(Cpu, that.Cpu) &&
                    Objects.equals(Mem, that.Mem) &&
                    Objects.equals(Name, that.Name);
        }

        @Override
        public int hashCode() {
            return Objects.hash(Pid, Cpu, Mem, Name);
        }

        int getPid() {
            return Integer.parseInt(Pid.get());
        }
    }

}
