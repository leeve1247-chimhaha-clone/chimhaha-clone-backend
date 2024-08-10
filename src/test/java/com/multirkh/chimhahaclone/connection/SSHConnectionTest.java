package com.multirkh.chimhahaclone.connection;

import com.jcraft.jsch.Session;
import com.multirkh.chimhahaclone.SSHConnection;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import static org.junit.jupiter.api.Assertions.*;

@Slf4j
@SpringBootTest
public class SSHConnectionTest {

    @Autowired
    private SSHConnection sshConnection;

    @AfterEach
    public void tearDown() {
        // 테스트 종료 후 SSH 연결 종료
        sshConnection.disconnect();
    }

    @Test
    public void testSSHConnection() {
        // SSH 연결이 성공적으로 설정되었는지 확인
        Session session = sshConnection.getSession();
        assertNotNull(session, "Session should not be null");
        assertTrue(session.isConnected(), "SSH session should be connected");
    }
}