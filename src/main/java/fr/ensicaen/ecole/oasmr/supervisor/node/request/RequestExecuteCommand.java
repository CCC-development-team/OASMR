/*
 *  Copyright (c) 2018. CCC-Development-Team
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

package fr.ensicaen.ecole.oasmr.supervisor.node.request;

import fr.ensicaen.ecole.oasmr.lib.command.Command;
import fr.ensicaen.ecole.oasmr.supervisor.Supervisor;
import fr.ensicaen.ecole.oasmr.supervisor.request.Request;

import java.io.Serializable;

public class RequestExecuteCommand extends Request {
    private final Command command;
    private final Integer id;

    public RequestExecuteCommand(Integer id, Command command) {
        this.command = command;
        this.id = id;
    }

    @Override
    public Serializable execute(Supervisor supervisor) throws Exception {
        return supervisor.getNodeFlyweightFactory().getNode(id).executeCommand(command);
    }

    @Override
    public String toString() {
        return "execute : " + command;
    }

}
