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

import java.io.Serializable;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class CommandMoveFile extends Command {

    /**
     * The source file path name to move.
     */
    private final String sourceFilePathName;

    /**
     * The destination file path name to move.
     */
    private final String destinationFilePathName;

    /**
     * @param sourceFilePathName      The source file path name to move.
     * @param destinationFilePathName The destination file path name to move.
     */
    public CommandMoveFile(String sourceFilePathName, String destinationFilePathName) {
        this.sourceFilePathName = sourceFilePathName;
        this.destinationFilePathName = destinationFilePathName;
    }

    @Override
    protected Serializable execute(Object... params) throws Exception {

        if(sourceFilePathName.equals(destinationFilePathName) || Files.exists(Paths.get(destinationFilePathName))){
            return false;
        }

        //TODO : Dir recursive
        Path result = Files.move(Paths.get(sourceFilePathName), Paths.get(destinationFilePathName));
        return Files.exists(result);
    }

    @Override
    public String toString() {
        return "move file " + sourceFilePathName + " -> " + destinationFilePathName;
    }
}