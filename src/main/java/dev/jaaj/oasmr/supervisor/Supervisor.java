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

package dev.jaaj.oasmr.supervisor;

import dev.jaaj.oasmr.lib.command.Command;
import dev.jaaj.oasmr.lib.command.CommandExecutor;
import dev.jaaj.oasmr.lib.command.ServerRunnableCommandExecutor;
import dev.jaaj.oasmr.lib.dateUtil;
import dev.jaaj.oasmr.lib.network.Server;
import dev.jaaj.oasmr.lib.network.exception.ExceptionPortInvalid;
import dev.jaaj.oasmr.lib.network.exception.ExceptionServerRunnableNotEnded;
import dev.jaaj.oasmr.supervisor.node.NodeFlyweightFactory;
import dev.jaaj.oasmr.supervisor.auth.UserList;

import java.io.IOException;
import java.io.Serializable;

public class Supervisor extends CommandExecutor {
    private final NodeFlyweightFactory nodeFlyweightFactory = new NodeFlyweightFactory();
    private Server serverRequestHandler;
    private final UserList userList = new UserList();

    private final CommandFinder finder = new CommandFinder("commands");

    public Supervisor(int portRequests) throws IOException, ExceptionPortInvalid {
        serverRequestHandler = new Server(portRequests, new ServerRunnableCommandExecutor(this));
    }

    public void start() throws InterruptedException, IOException {
        Thread ThreadServerRequestHandler = new Thread(() -> {
            try {
                serverRequestHandler.start();
            } catch (IOException e) {
                e.printStackTrace();
            }
        });

        System.out.print("[" + dateUtil.getFormattedDate() + "]-> CommandExecutorServer (" + serverRequestHandler.getPort() + ") loading... ");
        ThreadServerRequestHandler.start();
        System.out.println("Done !");
        System.out.print("[" + dateUtil.getFormattedDate() + "]-> CommandFinder loading... ");
        finder.start();//TODO FIX
        System.out.println("Done !");
        finder.scan();
        userList.loadUsers();

        ThreadServerRequestHandler.join();
        finder.join();
    }

    public void stop() throws ExceptionServerRunnableNotEnded {
        serverRequestHandler.stop();
        finder.interrupt();
    }

    public NodeFlyweightFactory getNodeFlyweightFactory() {
        return nodeFlyweightFactory;
    }

    public UserList getUserList() {
        return userList;
    }

    public CommandFinder getCommandFinder() {
        return finder;
    }

    @Override
    protected Serializable execute(Command c) throws Exception {
        return c.executeCommand(this);
    }
}
