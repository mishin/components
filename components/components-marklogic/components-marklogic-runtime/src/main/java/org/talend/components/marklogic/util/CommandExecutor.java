package org.talend.components.marklogic.util;

public class CommandExecutor {

    public static Process executeCommand(String command) throws Exception {
        return Runtime.getRuntime().exec(command);
    }

}
