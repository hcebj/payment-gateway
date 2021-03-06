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
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.PropertySource;
import org.springframework.stereotype.Component;

import com.hce.paymentgateway.service.SecretService;
import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.JSchException;
import com.jcraft.jsch.Session;
import com.jcraft.jsch.SftpException;

import lombok.extern.slf4j.Slf4j;
/**
 * Created by zonga on 2018/5/24.
 */
@Slf4j
@Component
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
    @Autowired
    private SecretService secretService;

    /**
     * @param filename: 上传到SFTP的文件名
     * @param inputStream: 要加密的流
     * @param originFileAbsPath: 要加密文件绝对路径
     * inputStream、originFileAbsPath二选一: 如果不传绝对路径，会自动将流创建txt临时文件
     */
    public void uploadFileFromServer(String filename, InputStream inputStream, String originFileAbsPath) throws JSchException, IOException, SftpException, NoSuchProviderException, PGPException {
        String privateKey = "";
        if(File.separator.equals("/")){//Linux
        	privateKey = System.getProperty("user.home") + "/.ssh/id_rsa";
        }else{//windows
        	privateKey = System.getProperty("user.home") + "\\.ssh\\wsh\\id_rsa";
        }
        JSch jsch = new JSch();
        jsch.addIdentity(privateKey);
        //文件加密
        InputStream inputStream1 = null;
        Session session = null;
        ChannelSftp channel = null;
        String fileNameTemp;//明文绝对路径
		try {
			fileNameTemp = originFileAbsPath==null||originFileAbsPath.trim().length()==0?createFile(filename, inputStream):originFileAbsPath;
			inputStream1 = pgpEncrpt(fileNameTemp, filename);//对明文加密，输出到文件filename中
	        session = jsch.getSession(dbsUsername, dbsHost, Integer.valueOf(dbsPort));
	        session.setConfig("StrictHostKeyChecking", "no");
	        log.info("Connecting to remote server: {}@{} ...", dbsUsername, dbsHost);
	        session.connect();
	        channel = (ChannelSftp) session.openChannel("sftp");
	        channel.connect();
	        log.info("[{}]: Change directory: {}", dbsHost, inboxDir);
	        channel.cd(inboxDir);
	        log.info("[{}]: Upload file with name: {}", dbsHost, filename);
	        channel.put(inputStream1, filename);
		} finally {
			if(inputStream1!=null)
				inputStream1.close();
			if(channel!=null)
				channel.disconnect();
			if(session!=null)
				session.disconnect();
	        log.info("Disconnect to remote server: {}@{}", dbsUsername, dbsHost);
		}
    }

    public List<File> downloadFilesFromServer(String filenameRegex) throws JSchException, SftpException, FileNotFoundException {
        String privateKey;
        if(File.separator.equals("/")) {//Linux
        	privateKey = System.getProperty("user.home") + "/.ssh/id_rsa";
        } else {//windows
        	privateKey = System.getProperty("user.home") + "\\.ssh\\wsh\\id_rsa";
        }
        //String privateKey = System.getProperty("user.home") + "/.ssh/id_rsa";
        JSch jsch = new JSch();
        jsch.addIdentity(privateKey);
        Session session = null;
        ChannelSftp channel = null;
        try {
        	session = jsch.getSession(dbsUsername, dbsHost, Integer.valueOf(dbsPort));
            session.setConfig("StrictHostKeyChecking", "no");
            log.info("Connecting to remote server: {}@{} ...", dbsUsername, dbsHost);
            session.connect();
            channel = (ChannelSftp) session.openChannel("sftp");
            channel.connect();
            log.info("[{}]: Change to directory: {}", dbsHost, outboxDir);
            channel.cd(outboxDir);
            Vector<ChannelSftp.LsEntry> filesOnServer = channel.ls("*"+filenameRegex+"*");
            log.info("[{}]: Found {} file(s) with expression: {}", dbsHost, filesOnServer.size(), filenameRegex);
            List<File> files = new ArrayList<File>(filesOnServer.size());
            for(ChannelSftp.LsEntry entry : filesOnServer) {
                String name = entry.getFilename();
                File targetFile = new File(localTempDir + "/" + name);
                log.info("[{}]: Downloading server file: {} to local file: {} ...", dbsHost, name, targetFile.getAbsolutePath());
                OutputStream os = new FileOutputStream(targetFile);
                channel.get(name, os);
                files.add(targetFile);
            }
            log.info("Disconnect to remote server: {}@{}", dbsUsername, dbsHost);
            return files;
        } finally {
        	if(channel!=null)
        		channel.disconnect();
        	if(session!=null)
        		session.disconnect();
        }
    }

    public List<File> downloadFilesFromServerAndDecrypt(String filenameRegex) throws JSchException, SftpException, IOException, InterruptedException {
    	List<File> files = this.downloadFilesFromServer(filenameRegex);
    	List<File> filesDecode = this.decrypt(files);
        return filesDecode;
    }

    public List<File> decrypt(List<File> files) throws IOException, InterruptedException {
    	List<File> filesDecode = new ArrayList<File>(files.size());
    	log.info("XXX---------size: "+files.size());
        for(File file:files) {
        	//获取文件的路径。
        	String filePathEncod = file.getAbsolutePath();//加密传入的文件路径
        	String fileName = file.getName();
//        	InputStream  InputStream = new FileInputStream(filePathEncod);//加密传输的输入流
        	log.info("XXX---------path: "+filePathEncod);
        	//创建加密后的文件路径，和文件名。
        	//1、去掉pgp 后缀
        	String fileNameDecode = DecodeFiles(fileName);
        	log.info("XXX---------fileNameDecode: "+fileNameDecode);
        	//2、新建解密后文件
        	String filePathDecode;
        	String path;
        	if(File.separator.equals("/")) {//Linux
        		path = System.getProperty("user.home") + "/tempFile/";
            } else {//windows
            	path = System.getProperty("user.home") + "\\tempFile\\";
            }
        	File f = new File(path);
        	if(!f.exists()) {
        		f.mkdirs();
        	} 
        	File newFile = new File(f, fileNameDecode);
        	if(!newFile.exists()){
        		newFile.createNewFile();
        	}
        	filePathDecode = newFile.getAbsolutePath();
        	log.info("XXX---------filePathDecode: "+filePathDecode);
        	//进行解密
        	//InputStream inputStream = KeyBasedLargeFileProcessor.class.getClassLoader().getResourceAsStream("private.asc");
        	Security.addProvider(new BouncyCastleProvider());
//        	log.info(String.format("privateKey:%s,EncrptFile:%s,decryptFile:%s", System.getProperty("user.home") + "/pgp/HCE-PGP.asc",filePathEncod,filePathDecode));
//        	KeyBasedLargeFileProcessor.decryptFile(filePathEncod, System.getProperty("user.home") + "/pgp/HCE-PGP.asc", "HKHCEH-DBS".toCharArray(), filePathDecode);
        	log.info(filePathEncod+"----------"+filePathDecode);
        	secretService.pgp(filePathEncod, filePathDecode);
        	log.info("XXX---------Finished");
        	filesDecode.add(newFile);
        }
        return filesDecode;
    }
    
    private String createFile(String fileName, InputStream inputStream) throws IOException {
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
    	int bytesWritten = 0;
    	int byteCount = 0;
    	byte[] bytes = new byte[1024];
    	OutputStream  outputStream = null;
    	try {
			outputStream = new FileOutputStream(path+fileName);
			while((byteCount = inputStream.read(bytes)) != -1) {  
	    		outputStream.write(bytes, bytesWritten, byteCount);  
	    		bytesWritten += byteCount;  
	    	}
	    	return path+fileName;
		} finally {
			if(outputStream!=null)
				outputStream.close();
		}
    }
    
    public InputStream pgpEncrpt(String fileNameTemp,String fileName) throws NoSuchProviderException, IOException, PGPException {
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
    		file.createNewFile();
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
        PGPPublicKey encKey = PGPExampleUtil.readPublicKey(publicKey);
        OutputStream out = null;
        try {
			out = new BufferedOutputStream(new FileOutputStream(encode));
			KeyBasedLargeFileProcessor.encryptFile(out, fileNameTemp, encKey, true, false);
		} finally {
			if(out!=null)
				out.close();
		}
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
    
    public List<File> testack (List<File> resultFiles){
    	List<File> files = new ArrayList<>();
		//String filePathEncod = temp.getAbsolutePath();//加密传入的文件路径
        String fileName = "UFF1.STP.HKHCEH.HKHCEH.201807090012.txt.DHBKHKHH.D20180709T151007.ACK1";
        //String fileNameDecode = DecodeFiles(fileName);
        String path = System.getProperty("user.home") + "/tempFile/";
        log.info(path+fileName);
        File file = new File(path,fileName);
        if(file.exists()){
        	files.add(file);
        }
    	return files;
    }

    public String getTempFileDir() {
    	String path;
    	if(File.separator.equals("/")) {//Linux
    		path = System.getProperty("user.home") + "/tempFile/";
        } else {//windows
        	path = System.getProperty("user.home") + "\\tempFile\\";
        }
    	return path;
    }
}
