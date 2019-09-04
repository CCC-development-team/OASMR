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

package dev.jaaj.oasmr.lib.filemanagement;

import dev.jaaj.oasmr.lib.command.Command;

import java.io.File;
import java.io.Serializable;

public class CommandIsDirectory extends Command {

    private String path;

    public CommandIsDirectory(String path) {
        this.path = path;
    }

    @Override
    protected Serializable execute(Object... params) throws Exception {
        return new File(path).isDirectory();
    }

    @Override
    public String toString() {
        return "Command is Directory";
    }

}