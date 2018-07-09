package com.hce.paymentgateway;

import com.hce.paymentgateway.util.SCPFileUtils;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.SftpException;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.List;

@RunWith(SpringRunner.class)
@SpringBootTest(classes = SCPFileUtils.class)
public class PaymentGatewayApplicationTests {
    @Autowired
    private SCPFileUtils scpFileUtils;

    @Test
    public void testSCPUploadFileToServer() throws Exception {
        String fileName = "UFF1.STP.HKGTSA.HKGTSA.IPE201704241740.TXT.DHBKHKHH";
        File localFile = new File(fileName);
        InputStream is = new FileInputStream(localFile);
        scpFileUtils.uploadFileFromServer(fileName, is);
    }

    @Test
    public void testSCPDownloadFilesFromServer() throws SftpException, JSchException, IOException, InterruptedException {
        List<File> files = scpFileUtils.downloadFilesFromServerAndDecrypt("UFF1.STP.HKGTSA.HKGTSA.IPE201704241740.TXT*");
        System.out.println("Done: "+files.size());
    }
}