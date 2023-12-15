package storage.hbase.compactor;

import lombok.extern.slf4j.Slf4j;
import storage.hbase.DiskFile;
import storage.hbase.DiskStore;
import storage.hbase.KeyValue;
import storage.hbase.compactor.Compactor;
import storage.hbase.scanner.Scanner;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static storage.hbase.DiskStore.FILE_NAME_ARCHIVE_SUFFIX;

@Slf4j
public class DefaultCompactor extends Compactor {

    private DiskStore diskStore;
    private volatile boolean running = true;

    public DefaultCompactor(DiskStore diskStore) {
        this.diskStore = diskStore;
        this.setDaemon(true);
    }

    private void performCompact(List<DiskFile> filesToCompact) throws IOException {
        String fileName = diskStore.getNextDiskFileName();
        String fileTempName = fileName + DiskStore.FILE_NAME_TMP_SUFFIX;
        try {
            try (DiskFile.DiskFileWriter writer = new DiskFile.DiskFileWriter(fileTempName)) {
                for (Scanner<KeyValue> scanner = diskStore.createScanner(filesToCompact); scanner.hasNext();) {
                    writer.append(scanner.next());
                }
                writer.appendIndex();
                writer.appendTrailer();
            }
            File file = new File(fileTempName);

            if (!file.renameTo(new File(fileName))) {
                throw new IOException("Rename " + fileTempName + " to " + fileName + " failed");
            }

            // Rename the data files to archive files.
            // TODO when rename the files, will we effect the scan ?
            for (DiskFile df : filesToCompact) {
                df.close();
                File f = new File(df.getFileName());
                File archiveFile = new File(df.getFileName() + FILE_NAME_ARCHIVE_SUFFIX);
                if (!f.renameTo(archiveFile)) {
                    log.error("Rename " + df.getFileName() + " to " + archiveFile.getName() + " failed.");
                }
            }
            diskStore.removeDiskFiles(filesToCompact);

            // TODO any concurrent issue ?
            diskStore.addDiskFile(fileName);
        } finally {
            File f = new File(fileTempName);

            if (f.exists()) {
                f.delete();
            }
        }
    }

    @Override
    public void compact() throws IOException {
        List<DiskFile> filesToCompact = new ArrayList<>();
        filesToCompact.addAll(diskStore.getDiskFiles());
        performCompact(filesToCompact);
    }

    public void run() {
        while (running) {
            try {
                boolean isCompacted = false;

                if (diskStore.getDiskFiles().size() > diskStore.getMaxDiskFiles()) {
                    performCompact(diskStore.getDiskFiles());
                    isCompacted = true;
                }

                if (!isCompacted) {
                    Thread.sleep(1000);
                }
            } catch (IOException e) {
                e.printStackTrace();
                log.error("Major compaction failed: ", e);
            } catch (InterruptedException ie) {
                log.error("InterruptedException happened, stop running: ", ie);
                break;
            }
        }
    }

    public void stopRunning() {
        this.running = false;
    }
}
