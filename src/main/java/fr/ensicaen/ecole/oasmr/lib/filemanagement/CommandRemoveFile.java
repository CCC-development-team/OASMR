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

package fr.ensicaen.ecole.oasmr.lib.filemanagement;


import fr.ensicaen.ecole.oasmr.lib.command.Command;

import java.io.File;
import java.io.Serializable;

/**
 * A command that removes a file
 */
public class CommandRemoveFile extends Command {

    /**
     * The name of the path of the file to be removed.
     */
    private String fileNamePathToBeRemoved;

    /**
     *
     * @param fileNamePathToBeRemoved The name of the path of the file to be removed.
     */
    public CommandRemoveFile(String fileNamePathToBeRemoved) {
        this.fileNamePathToBeRemoved = fileNamePathToBeRemoved;
    }

    @Override
    protected Serializable execute(Object... params) throws Exception {
        File file = new File(fileNamePathToBeRemoved);

        return file.delete();
    }

    @Override
    public String toString() {
        return null;
    }
}
