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

package fr.ensicaen.ecole.oasmr.lib.network;


import fr.ensicaen.ecole.oasmr.lib.network.exception.ExceptionPortInvalid;
import fr.ensicaen.ecole.oasmr.lib.network.exception.ExceptionServerRunnableNotEnded;

import java.io.IOException;
import java.io.Serializable;
import java.net.ServerSocket;
import java.net.Socket;

public class Server implements Serializable {
    private int port;
    private ServerSocket serverSocket;
    private ServerRunnable runnable;
    private Boolean run;

    public Server(int port, ServerRunnable runnable) throws IOException, ExceptionPortInvalid {
        if (port < 65536 && port > 0) {
            this.port = port;
            run = false;
        } else {
            throw new ExceptionPortInvalid();
        }
        this.runnable = runnable;
    }

    public void start() throws IOException {
        serverSocket = new ServerSocket(port);
        run = true;
        while (run) {
            try {
                Socket client = serverSocket.accept();
                ServerRunnable r;
                r = (ServerRunnable) runnable.clone();
                r.setClientSocket(client);
                Thread t = new Thread(r);
                t.start();
            } catch (CloneNotSupportedException e) {
                e.printStackTrace();
            }
        }
    }


    public void stop() throws ExceptionServerRunnableNotEnded {
        try {
            serverSocket.close();
        } catch (IOException e) {
            throw new ExceptionServerRunnableNotEnded();
        } finally {
            run = false;
        }
    }

    public ServerSocket getServerSocket() {
        return serverSocket;
    }


    public int getPort() {
        return port;
    }
}
