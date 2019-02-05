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

package fr.ensicaen.ecole.oasmr.app;

import fr.ensicaen.ecole.oasmr.lib.PropertiesFactory;

import java.io.BufferedWriter;
import java.io.FileWriter;
import java.io.IOException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Properties;

public final class Config {
    private final static int DEFAULT_PORT = 40404;
    private static String file = "config.properties";
    private Properties properties;

    private static Config ourInstance = new Config();

    public static Config getInstance() {
        return ourInstance;
    }

    private Config() {
        try {
            properties = PropertiesFactory.getProperties(file);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String getProperty(String key) {
        return properties.getProperty(key);
    }

    public void setProperty(String key, String value) throws IOException {
        setAndStoreProperty(key, value);
        properties.store(new FileWriter(file), "save");
    }

    private void setAndStoreProperty(String key, String value) {
        properties.setProperty(key, value);
    }

    public String getIP() {
        return properties.getProperty("ip", "127.0.0.1");
    }

    public void setIP(String ip) {
        setAndStoreProperty("ip", ip);
    }

    public void setIP(InetAddress ip) {
        setAndStoreProperty("ip", ip.toString());
    }

    public String getPortinString() {
        return properties.getProperty("supervisor_port", String.valueOf(DEFAULT_PORT));
    }

    public int getPort() {
        return Integer.parseInt(properties.getProperty("supervisor_port", String.valueOf(DEFAULT_PORT)));
    }

    public void setPort(int port) {
        setAndStoreProperty("port", String.valueOf(port));
    }

    public void setPort(String port) {
        setAndStoreProperty("port", port);
    }

}
