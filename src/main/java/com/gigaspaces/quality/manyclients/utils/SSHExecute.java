package com.gigaspaces.quality.manyclients.utils;

import org.apache.tools.ant.taskdefs.optional.ssh.SSHExec;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class SSHExecute {

    public static String runCommand(String ipAddress, long timeoutMilliseconds, String command, String username, String password, String keyFile ){
        File output = null;
        try {
            System.out.println( "runCommand, ipAddress:" + ipAddress + ", command:" + command + ", username=" + username + ", keyFile=" + keyFile + ", password=" + password);

            output = File.createTempFile("sshCommand", ".txt");
            final SSHExec task = new SSHExec();
            task.setOutput(output);
            // ssh related parameters
            task.setFailonerror(false);
            task.setCommand(command);
            task.setHost(ipAddress);
            task.setTrust(true);
            task.setUsername(username);
            if( keyFile != null ) {
                task.setKeyfile(keyFile);
            }
            if( password != null && password.trim().length() > 0 ){
                task.setPassword( password );
            }
            task.setTimeout(timeoutMilliseconds);
            System.out.println( "runCommand, before execute" );
            task.execute();
            String response = readFileAsString(output);
            return response;
        }catch(IOException e) {
            System.out.println();
        } 
        if (output != null){
            output.delete();
        }
        return null;
    }
    
    private static String readFileAsString(File file) throws IOException {
        StringBuffer fileData = new StringBuffer(1000);
        BufferedReader reader = new BufferedReader(new FileReader(file));
        char[] buf = new char[1024];
        int numRead = 0;
        while ((numRead = reader.read(buf)) != -1) {
            String readData = String.valueOf(buf, 0, numRead);
            fileData.append(readData);
            buf = new char[1024];
        }
        reader.close();
        return fileData.toString();
    }
}
