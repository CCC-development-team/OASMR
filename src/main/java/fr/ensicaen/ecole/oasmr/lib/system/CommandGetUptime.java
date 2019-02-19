package fr.ensicaen.ecole.oasmr.lib.system;

import fr.ensicaen.ecole.oasmr.lib.command.Command;

import java.io.Serializable;

public class CommandGetUptime extends Command {
    @Override
    public Serializable execute(Object... params) throws Exception {
        return SystemInfoSingleton.getHardware().getProcessor().getSystemUptime();
    }

    @Override
    public String toString() {
        return "Command get uptime";
    }
}