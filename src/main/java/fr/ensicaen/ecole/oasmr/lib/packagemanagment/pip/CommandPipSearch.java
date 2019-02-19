package fr.ensicaen.ecole.oasmr.lib.packagemanagment.pip;

import fr.ensicaen.ecole.oasmr.lib.ProcessBuilderUtil;
import fr.ensicaen.ecole.oasmr.lib.command.Command;
import fr.ensicaen.ecole.oasmr.lib.packagemanagment.pip.exceptions.PipException;

import java.io.IOException;
import java.io.Serializable;

public class CommandPipSearch extends Command {

    private String searchingTerms;

    public CommandPipSearch(String searchingTerms) {
        this.searchingTerms = searchingTerms;
    }

    @Override
    public Serializable execute(Object... params) throws Exception {
        ProcessBuilder processBuilder = new ProcessBuilder("pip", "search", searchingTerms);
        try {
            Process p = processBuilder.start();
            p.waitFor();
            int ret = p.exitValue();
            System.out.println(ret);
            switch (ret) {
                case 0:
                    return ProcessBuilderUtil.getOutput(p);
                default:
                    throw new PipException(ProcessBuilderUtil.getOutputError(p));
            }

        } catch (IOException | InterruptedException e) {
            e.printStackTrace();
            return e;
        }
    }

    @Override
    public String toString() {
        return "pip" + " " + "search" + " " + searchingTerms;
    }
}
