package com.multirkh.chimhahaclone;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import org.springframework.context.annotation.Configuration;

@Configuration
@RequiredArgsConstructor
public class AppConfig {

    private final SSHConnection sshConnection;

    @PostConstruct
    public void init() throws Exception {
        sshConnection.connect();
    }
}
