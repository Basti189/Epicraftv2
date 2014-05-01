package de.wolfsline.backup;

import java.io.File;
import java.text.ParsePosition;
import java.text.SimpleDateFormat;
import java.util.*;
import java.util.logging.Logger;

import de.wolfsline.Epicraft.Epicraft;

public abstract class BackupFileManager implements IBackupFileManager {
    protected File backupFolder;
    protected SimpleDateFormat fileNameDateFormat;
    protected Epicraft plugin;

    protected BackupFileManager(String backupFolder, String fileNameDateFormat, Epicraft plugin) {
        this.backupFolder = new File(backupFolder);
        this.fileNameDateFormat = new SimpleDateFormat(fileNameDateFormat);
        this.plugin = plugin;
    }

    protected String formatDate(Date date) {
        return fileNameDateFormat.format(date);
    }

    @Override
    public SortedSet<Date> backupList() {
        File[] files = backupFolder.listFiles();
        if (files == null) {
            return new TreeSet<Date>();
        }
        SortedSet<Date> backups = new TreeSet<Date>();
        for (File file : files) {
            Date date = fileNameDateFormat.parse(file.getName(), new ParsePosition(0));
            if (date != null && file.getName().equals(getFileName(date))) {
                backups.add(date);
            }
        }
        return backups;
    }

    protected abstract String getFileName(Date date);
}