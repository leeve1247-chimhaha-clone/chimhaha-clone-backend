package com.multirkh.chimhahaclone;

import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import lombok.Getter;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class SSHConnection {

    @Value("${ssh.username}")
    private String username;

    @Value("${ssh.host}")
    private String host;

    @Value("${ssh.port}")
    private int port;

    @Value("${ssh.privateKeyPath}")
    private String privateKeyPath;

    @Value("${ssh.dbHost}")
    private String dbHost;

    @Value("${ssh.dbPort}")
    private int dbPort;

    @Getter
    private Session session;

    public void connect() throws Exception {
        System.out.println("privateKeyPath = " + privateKeyPath);

        JSch jsch = new JSch();

        // 개인 키 추가
        jsch.addIdentity(privateKeyPath);

        session = jsch.getSession(username, host, port);
        session.setConfig("StrictHostKeyChecking", "no");
        session.connect();
        session.setPortForwardingL(dbPort, dbHost, dbPort);
    }

    public void disconnect() {
        if (session != null && session.isConnected()) {
            session.disconnect();
        }
    }

}

