package com.hce.paymentgateway.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.security.NoSuchProviderException;
import java.security.Security;
import java.util.ArrayList;
import java.util.List;
import java.util.Vector;

import org.bouncycastle.jce.provider.BouncyCastleProvider;
import org.bouncycastle.openpgp.PGPException;
import org.bouncycastle.openpgp.PGPPublicKey;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Service;

import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;

/**
 * Created by zonga on 2018/5/24.
 */
@Service
@Slf4j
@PropertySource("classpath:application.properties")
public class SCPFileUtils {

    @Value("${hce.pgw.dbs.remote-server.host}")
    private String dbsHost;

    @Value("${hce.pgw.dbs.remote-server.username}")
    private String dbsUsername;

    @Value("${hce.pgw.dbs.remote-server.port}")
    private String dbsPort;

    @Value("${hce.pgw.dbs.remote-server.inbox}")
    private String inboxDir;

    @Value("${hce.pgw.dbs.remote-server.outbox}")
    private String outboxDir;

    @Value("${hce.pgw.dbs.local.file.location}")
    private String localTempDir;


    public void uploadFileFromServer(String filename, InputStream inputStream)
        throws Exception {

        JSch jsch = new JSch();
        String privateKey ="";
        if(File.separator.equals("/")){//Linux
        	privateKey = System.getProperty("user.home") + "/.ssh/id_rsa";
        }else{//windows
        	privateKey = System.getProperty("user.home") + "\\.ssh\\wsh\\id_rsa";
        }
        //文件加密
        InputStream inputStream1 = null;
        try {
			String fileNameTemp = createFile(filename, inputStream);
			inputStream1=pgpEncrpt(fileNameTemp, filename);
			
		} catch (IOException | NoSuchProviderException | PGPException e) {
			
			e.printStackTrace();
			throw e;
		}
        
        jsch.addIdentity(privateKey);

        Session session = jsch.getSession(dbsUsername, dbsHost, Integer.valueOf(dbsPort));
        session.setConfig("StrictHostKeyChecking", "no");
        log.info("Connecting to remote server: {}@{} ...", dbsUsername, dbsHost);
        session.connect();
        
        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        log.info("[{}]: Change directory: {}", dbsHost, inboxDir);
        channel.cd(inboxDir);
        log.info("[{}]: Upload file with name: {}", dbsHost, filename);
        channel.put(inputStream1, filename);
        channel.disconnect();
        session.disconnect();
        log.info("Disconnect to remote server: {}@{}", dbsUsername, dbsHost);

    }

    public List<File> downloadFilesFromServer(String filenameRegex) throws JSchException, SftpException, FileNotFoundException {

        JSch jsch = new JSch();
        String privateKey;
        if(File.separator.equals("/")){//Linux
        	privateKey = System.getProperty("user.home") + "/.ssh/id_rsa";
        }else{//windows
        	privateKey = System.getProperty("user.home") + "\\.ssh\\wsh\\id_rsa";
        }
        //String privateKey = System.getProperty("user.home") + "/.ssh/id_rsa";
        jsch.addIdentity(privateKey);

        Session session = jsch.getSession(dbsUsername, dbsHost, Integer.valueOf(dbsPort));
        session.setConfig("StrictHostKeyChecking", "no");
        log.info("Connecting to remote server: {}@{} ...", dbsUsername, dbsHost);
        session.connect();

        ChannelSftp channel = (ChannelSftp) session.openChannel("sftp");
        channel.connect();
        log.info("[{}]: Change to directory: {}", dbsHost, outboxDir);
        channel.cd(outboxDir);
        Vector<ChannelSftp.LsEntry> filesOnServer = channel.ls("*"+filenameRegex+"*");
        log.info("[{}]: Found {} file(s) with expression: {}", dbsHost, filesOnServer.size(), filenameRegex);
        List<File> files = new ArrayList<>();
        for (ChannelSftp.LsEntry entry : filesOnServer) {
            String name = entry.getFilename();
            File targetFile = new File(localTempDir + "/" + name);
            log.info("[{}]: Downloading server file: {} to local file: {} ...", dbsHost, name, targetFile.getAbsolutePath());
            OutputStream os = new FileOutputStream(targetFile);
            channel.get(name, os);
            files.add(targetFile);
        }
        channel.disconnect();
        session.disconnect();
        log.info("Disconnect to remote server: {}@{}", dbsUsername, dbsHost);
        
        //解密文件，
        List<File> filesDecode = null;
        for(File file  : files){
        	
        	//获取文件的路径。
        	
        	String filePathEncod = file.getAbsolutePath();//加密传入的文件路径
        	
        	String fileName = file.getName();
        	
        	InputStream  InputStream = new FileInputStream(filePathEncod);//加密传输的输入流
        	
        	//创建加密后的文件路径，和文件名。
        	//1、去掉pgp 后缀
        	String fileNameDecode = null;
        	
        	fileNameDecode = DecodeFiles(fileName);
        	
        	
        	
        	
        	//2、新建解密后文件
        	String filePathDecode;
        	String path;
        	
        	if(File.separator.equals("/")){//Linux
        		path = System.getProperty("user.home") + "/tempFile/";
            }else{//windows
            	path = System.getProperty("user.home") + "\\tempFile\\";
            }
        	
        	File f = new File(path);
        	if(!f.exists()){
        	f.mkdirs();
        	} 
        	
        	
        	File newFile = new File(f,fileNameDecode);
        	if(!newFile.exists()){
        		try {
        			newFile.createNewFile();
        			
        		} catch (IOException e) {
        			// TODO Auto-generated catch block
        			e.printStackTrace();
        		}
        	}
        	
        	filePathDecode = newFile.getAbsolutePath();
        	
        	//进行解密
        	//InputStream inputStream = KeyBasedLargeFileProcessor.class.getClassLoader().getResourceAsStream("private.asc");
        	try {
        		Security.addProvider(new BouncyCastleProvider());
				KeyBasedLargeFileProcessor.decryptFile(filePathEncod, "C:\\Users\\acer\\Desktop\\ceshi\\HCE-PGP.asc", "HKHCEH-DBS".toCharArray(), "C:\\Users\\acer\\Desktop\\ceshi\\decode.txt");
			} catch (NoSuchProviderException | IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				
				
			}
        	
        	
        	filesDecode.add(newFile);
        
        	
        }

        return filesDecode;
        //return files;
    }
    
    public String createFile(String fileName,InputStream inputStream) throws IOException{
    	
    	fileName = fileName+".txt";
    	//path表示你所创建文件的路径
    	String path;
    	if(File.separator.equals("/")){//Linux
    		path = System.getProperty("user.home") + "/tempFile/";
        }else{//windows
        	path = System.getProperty("user.home") + "\\tempFile\\";
        }
    	
    	File f = new File(path);
    	if(!f.exists()){
    	f.mkdirs();
    	} 
    	// fileName表示你创建的文件名；为txt类型；
    	//String fileName="test.txt";
    	File file = new File(f,fileName);
    	if(!file.exists()){
    		try {
    			file.createNewFile();
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	OutputStream  outputStream = new FileOutputStream(path+fileName);  
    	  
    	int bytesWritten = 0;  
    	int byteCount = 0;  
    	  
    	byte[] bytes = new byte[1024];  
    	  
    	while ((byteCount = inputStream.read(bytes)) != -1)  
    	{  
    	          outputStream.write(bytes, bytesWritten, byteCount);  
    	           bytesWritten += byteCount;  
    	}  
    	inputStream.close();  
    	outputStream.close();  
    	
    	return path+fileName;
    }
    
    public InputStream pgpEncrpt(String fileNameTemp,String fileName) throws IOException, PGPException, NoSuchProviderException{
    	String path;
    	if(File.separator.equals("/")){//Linux
    		path = System.getProperty("user.home") + "/tempFile/";
        }else{//windows
        	path = System.getProperty("user.home") + "\\tempFile\\";
        }
    	File f = new File(path);
    	/*if(!f.exists()){
    	f.mkdirs();
    	} */
    	// fileName表示你创建的文件名；为txt类型；
    	//String fileName="test.txt";
    	File file = new File(f,fileName);
    	if(!file.exists()){
    		try {
    			file.createNewFile();
    			
    		} catch (IOException e) {
    			// TODO Auto-generated catch block
    			e.printStackTrace();
    		}
    	}
    	
    	//加密过程
    	Security.addProvider(new BouncyCastleProvider());
        String encode = path + fileName;//"C:\\Users\\acer\\Desktop\\ceshi\\encode.txt";
        //String test = "C:\\Users\\acer\\Desktop\\ceshi\\test.txt";
        String publicKey;
        if(File.separator.equals("/")){//Linux
        	publicKey = System.getProperty("user.home") + "/pgp/DSGJPMUAT-Public.asc";
    		//publicKey = "C:\\Users\\acer\\Desktop\\ceshi\\test.asc";
        }else{//windows
        	publicKey = System.getProperty("user.home") + "\\pgp\\HCE-PGP-PUB.asc";
        }
        //publicKey = "C:\\Users\\acer\\Desktop\\ceshi\\test.asc";
        //String text = "Hello how are you";
        OutputStream out = new BufferedOutputStream(new FileOutputStream(encode));
        PGPPublicKey encKey = PGPExampleUtil.readPublicKey(publicKey);
        KeyBasedLargeFileProcessor.encryptFile(out, fileNameTemp, encKey, true, false);
        out.close();
		
        
        //将加密后的文件读出
        InputStream keyIn = new BufferedInputStream(new FileInputStream(encode));
        return keyIn;
       
    }
    
//将下载下的文件的名后缀。pgp去掉。然后进行解密
    
    public String DecodeFiles (String fileNames){
		
    	String fileName = fileNames;
    	
    	if ((fileName != null) && (fileName.length() > 0)) { 
            int dot = fileName.lastIndexOf('.'); 
            if ((dot >-1) && (dot < (fileName.length()))) { 
                return fileName.substring(0, dot); 
            } 
        } 
        return fileName; 

    }
}
