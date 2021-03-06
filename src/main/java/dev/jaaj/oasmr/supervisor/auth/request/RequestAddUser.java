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

package dev.jaaj.oasmr.supervisor.auth.request;

import dev.jaaj.oasmr.supervisor.request.Request;
import dev.jaaj.oasmr.supervisor.Supervisor;
import dev.jaaj.oasmr.supervisor.auth.User;

import java.io.Serializable;

public class RequestAddUser extends Request {
    private final String login;
    private final String password;

    public RequestAddUser(String login, String password) {
        this.login = login;
        this.password = password;
    }

    @Override
    public Serializable execute(Supervisor supervisor) throws Exception {
        supervisor.getUserList().addUser(new User(login, password));
        return 0;
    }

    @Override
    public String toString() {
        return login + " RequestAddUser";
    }
}
