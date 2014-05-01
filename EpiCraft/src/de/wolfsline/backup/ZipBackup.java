package de.wolfsline.backup;

import java.io.*;
import java.net.URI;
import java.util.Date;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import de.wolfsline.Epicraft.Epicraft;

public class ZipBackup extends BackupFileManager {

    public ZipBackup(String backupFolder, String fileNameDateFormat, Epicraft plugin) {
        super(backupFolder, fileNameDateFormat, plugin);
    }

    @Override
    public Date createBackup(Iterable<File> worldFolders) throws IOException {
        if (!backupFolder.exists()) {
            backupFolder.mkdirs();
        }
        Date date = new Date();
        File backupFile = new File(backupFolder, getFileName(date));
        ZipOutputStream zip = new ZipOutputStream(new FileOutputStream(backupFile));
        try {
            for (File worldFolder : worldFolders) {
            	plugin.api.sendLog("[Epicraft - Backup] Erstelle -> " + worldFolder);
                zipFiles(worldFolder.getParentFile().toURI(), worldFolder, zip);
            }
        } finally {
            try {
                zip.close();
            } catch (IOException e) {
               
            }
        }
        return date;
    }

    @Override
    public void deleteBackup(Date date) {
        File backupFile = new File(backupFolder, getFileName(date));
        plugin.api.sendLog("[Epicraft - Backup] Lösche -> " + backupFile.getPath());
        backupFile.delete();
    }

    @Override
    protected String getFileName(Date date) {
        return formatDate(date) + ".zip";
    }

    private void zipFiles(URI root, File source, ZipOutputStream zip) throws IOException {
        if (source.isDirectory()) {
            for (String file : source.list()) {
                zipFiles(root, new File(source, file), zip);
            }
        } else {
            ZipEntry entry = new ZipEntry(root.relativize(source.toURI()).getPath());
            zip.putNextEntry(entry);
            InputStream in = null;
            try {
                in = new FileInputStream(source);
                byte[] buffer = new byte[4096];
                int bytesRead;
                while ((bytesRead = in.read(buffer)) > 0) {
                    zip.write(buffer, 0, bytesRead);
                }
            } catch (IOException e) {
            	plugin.api.sendLog("[Epicraft - Backup] Fehler beim Erstellen -> " + source.getAbsolutePath());
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        }
    }
}