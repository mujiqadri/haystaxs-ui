package com.haystaxs.ui.util;

import org.apache.commons.net.ftp.FTPClient;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Adnan on 10/23/2015.
 */
@Component
public class FileUtil {
    @Autowired
    private AppConfig appConfig;

    public void SaveFileToPath(MultipartFile file, String baseDir, String fileName) throws IOException {
        byte[] bytes = file.getBytes();

        // Creating the directory to store file
        File dir = new File(baseDir);
        if (!dir.exists())
            dir.mkdirs();

        // Create the file on server
        File serverFile = new File(dir.getAbsolutePath() + File.separator + fileName);
        BufferedOutputStream stream = new BufferedOutputStream(new FileOutputStream(serverFile));
        stream.write(bytes);
        stream.close();
    }

    public void unZip(String zipFile, String outputFolder){

        byte[] buffer = new byte[1024];

        try{
            //create output directory is not exists
            File folder = new File(outputFolder);
            if(!folder.exists()){
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while(ze!=null){

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                System.out.println("file unzip : "+ newFile.getAbsoluteFile());

                //create all non exists folders
                //else you will hit FileNotFoundException for compressed folder
                new File(newFile.getParent()).mkdirs();

                FileOutputStream fos = new FileOutputStream(newFile);

                int len;
                while ((len = zis.read(buffer)) > 0) {
                    fos.write(buffer, 0, len);
                }

                fos.close();
                ze = zis.getNextEntry();
            }

            zis.closeEntry();
            zis.close();
        }catch(IOException ex){
            ex.printStackTrace();
        }
    }

    public void uploadFileToFtp(MultipartFile file, String baseDir, String fileName) throws IOException {
        byte[] bytes = file.getBytes();
        FTPClient ftpClient = new FTPClient();

        try {
            ftpClient.connect(appConfig.getFtpHost());
            ftpClient.login(appConfig.getFtpUser(), appConfig.getFtpPassword());

            createFtpDirectoryHierarchy(baseDir, ftpClient);

            ByteArrayInputStream bis = new ByteArrayInputStream(bytes);

            ftpClient.storeFile(fileName, bis);
            bis.close();
        } catch (Exception ex) {
            int i = 0;
        } finally {
            ftpClient.logout();
        }
    }

    private void createFtpDirectoryHierarchy(String dirHierarchy, FTPClient ftpClient) throws IOException {
        String[] dirs = dirHierarchy.split("/");

        for(int i=0; i<dirs.length; i++) {
            if(ftpClient.makeDirectory(dirs[i])) {
                ftpClient.changeWorkingDirectory(dirs[i]);
            } else {
                throw new IOException(String.format("FTP directory creation failed for %s", dirs[i]));
            }
        }
    }
}
