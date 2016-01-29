package com.haystaxs.ui.util;

import org.apache.commons.compress.archivers.tar.TarArchiveEntry;
import org.apache.commons.compress.archivers.tar.TarArchiveInputStream;
import org.apache.commons.io.IOUtils;
import org.apache.commons.net.ftp.FTPClient;
import org.codehaus.plexus.archiver.tar.TarGZipUnArchiver;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;

import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.zip.GZIPInputStream;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * Created by Adnan on 10/23/2015.
 */
@Component
public class FileUtil {
    final static Logger logger = LoggerFactory.getLogger(FileUtil.class);

    @Autowired
    private AppConfig appConfig;

    public void saveMultipartFileToPath(MultipartFile file, String baseDir, String fileName) throws IOException {
        byte[] bytes = file.getBytes();

        saveToFile(bytes, baseDir, fileName);
    }

    public void unZip(String zipFile, String outputFolder) {

        byte[] buffer = new byte[1024];

        try {
            //create output directory is not exists
            File folder = new File(outputFolder);
            if (!folder.exists()) {
                folder.mkdir();
            }

            //get the zip file content
            ZipInputStream zis =
                    new ZipInputStream(new FileInputStream(zipFile));
            //get the zipped file list entry
            ZipEntry ze = zis.getNextEntry();

            while (ze != null) {

                String fileName = ze.getName();
                File newFile = new File(outputFolder + File.separator + fileName);

                System.out.println("file unzip : " + newFile.getAbsoluteFile());

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
        } catch (IOException ex) {
            ex.printStackTrace();
        }
    }

    // TODO: Should we force the user to upload QueryLogs without any subdirectories in the TAR File ?
    public void unGZipTarArchive(String tarGzipFile, String outputFolder) {
        try {
            File directory = new File(outputFolder);

            /* Read TAR File into TarArchiveInputStream */
            TarArchiveInputStream tarArchiveInputStream = new TarArchiveInputStream(
                    new GZIPInputStream(
                            new FileInputStream(tarGzipFile)));

            List<String> result = new ArrayList<String>();

            TarArchiveEntry entry = tarArchiveInputStream.getNextTarEntry();

            while (entry != null) {
                if (entry.isDirectory()) {
                    entry = tarArchiveInputStream.getNextTarEntry();
                    continue;
                }
                File curfile = new File(directory, entry.getName());
                File parent = curfile.getParentFile();
                if (!parent.exists()) {
                    parent.mkdirs();
                }
                OutputStream out = new FileOutputStream(curfile);
                IOUtils.copy(tarArchiveInputStream, out);
                out.close();
                result.add(entry.getName());
                entry = tarArchiveInputStream.getNextTarEntry();
            }
            tarArchiveInputStream.close();
        } catch (Exception ex) {
            logger.error("Cannot UnArchive file.", ex);
        }
    }

    public void saveToFile(byte[] bytes, String baseDir, String fileName) throws IOException {
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

        for (int i = 0; i < dirs.length; i++) {
            if (ftpClient.makeDirectory(dirs[i])) {
                ftpClient.changeWorkingDirectory(dirs[i]);
            } else {
                throw new IOException(String.format("FTP directory creation failed for %s", dirs[i]));
            }
        }
    }
}
