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

package fr.ensicaen.ecole.oasmr.lib.packagemanagment.apt;

import fr.ensicaen.ecole.oasmr.lib.ProcessBuilderUtil;
import fr.ensicaen.ecole.oasmr.lib.command.Command;
import fr.ensicaen.ecole.oasmr.lib.packagemanagment.apt.exceptions.ExceptionAptUpgradeFailure;

import java.io.IOException;
import java.io.Serializable;

//ToDO: check if it work

public class CommandAptUpgrade extends Command {

    public CommandAptUpgrade() {

    }

    @Override
    public Serializable execute(Object... params) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("sudo", "apt", "upgrade");
        try {
            Process p = processBuilder.start();
            p.waitFor();
            int ret = p.exitValue();
            //System.out.println(ProcessBuilderUtil.getOutput(p));
            switch (ret) {
                case 0:
                    return ProcessBuilderUtil.getOutput(p);

                default:
                    throw new ExceptionAptUpgradeFailure(ProcessBuilderUtil.getOutputError(p));

            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e;
        }
    }

    @Override
    public String toString() {
        return "sudo" + " " +"apt" + " " + "upgrade";
    }
}
