package de.wolfsline.backup;

import java.io.File;
import java.io.IOException;
import java.util.Date;

import de.wolfsline.Epicraft.Epicraft;

public class CopyBackup extends BackupFileManager {

    public CopyBackup(String backupFolder, String fileNameDateFormat, Epicraft plugin) {
        super(backupFolder, fileNameDateFormat, plugin);
    }

    @Override
    public Date createBackup(Iterable<File> worldFolders) throws IOException {
        Date date = new Date();
        File destination = new File(backupFolder, getFileName(date));
        for (File worldFolder : worldFolders) {
        	plugin.api.sendLog("[Epicraft - Backup] Sichere -> " + worldFolder);
            FileUtils.copyFiles(worldFolder, new File(destination, worldFolder.getName()), plugin);
        }
        return date;
    }

    @Override
    public void deleteBackup(Date date) throws IOException {
        File backupFile = new File(backupFolder, getFileName(date));
        plugin.api.sendLog("[Epicraft - Backup] Lösche -> " + backupFile.getParentFile());
        deleteFile(backupFile);
    }

    @Override
    protected String getFileName(Date date) {
        return formatDate(date);
    }

    void deleteFile(File f) throws IOException {
        if (f.isDirectory()) {
            for (File c : f.listFiles()) {
                deleteFile(c);
            }
        }
        f.delete();
    }
}