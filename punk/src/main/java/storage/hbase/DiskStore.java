package storage.hbase;

import lombok.extern.slf4j.Slf4j;
import storage.hbase.scanner.MultiScanner;
import storage.hbase.scanner.SeekScanner;

import java.io.Closeable;
import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.concurrent.atomic.AtomicLong;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@Slf4j
public class DiskStore implements Closeable {

    public static final String FILE_NAME_TMP_SUFFIX = ".tmp";
    public static final String FILE_NAME_ARCHIVE_SUFFIX = ".archive";
    private static final Pattern DATA_FILE_PATTERN = Pattern.compile("data\\.([0-9]+)");

    private String dataDir;
    private List<DiskFile> diskFiles;
    private int maxDiskFiles;
    private volatile AtomicLong maxFileId;

    public DiskStore(String dataDir, int maxDiskFiles) {
        this.dataDir = dataDir;
        this.diskFiles = new ArrayList<>();
        this.maxDiskFiles = maxDiskFiles;
    }

    private File[] listDiskFiles() {
        File file = new File(dataDir);
        return file.listFiles(f -> DATA_FILE_PATTERN.matcher(f.getName()).matches());
    }

    public synchronized long getMaxDiskId() {
        File[] files = listDiskFiles();
        long maxFileId = -1L;

        for (File file : files) {
            Matcher matcher = DATA_FILE_PATTERN.matcher(file.getName());

            if (matcher.matches()) {
                maxFileId = Math.max(Long.parseLong(matcher.group(1)), maxFileId);
            }
        }

        return maxFileId;
    }

    public synchronized long nextDiskFileId() {
        return maxFileId.incrementAndGet();
    }

    public void addDiskFile(DiskFile file) {
        synchronized (diskFiles) {
            diskFiles.add(file);
        }
    }

    public synchronized void addDiskFile(String filename) throws IOException {
        DiskFile file = new DiskFile();
        file.open(filename);
        addDiskFile(file);
    }

    public synchronized String getNextDiskFileName() {
        return new File(this.dataDir, String.format("data.%020d", nextDiskFileId())).toString();
    }

    public void open() throws IOException {
        File[] files = listDiskFiles();

        for (File file : files) {
            DiskFile diskFile = new DiskFile();
            diskFile.open(file.getAbsolutePath());
            diskFiles.add(diskFile);
        }

        maxFileId = new AtomicLong(getMaxDiskId());
    }

    public List<DiskFile> getDiskFiles() {
        synchronized (diskFiles) {
            return new ArrayList<>(diskFiles);
        }
    }

    public void removeDiskFiles(Collection<DiskFile> files) {
        synchronized (diskFiles) {
            diskFiles.removeAll(files);
        }
    }

    public long getMaxDiskFiles() {
        return this.maxDiskFiles;
    }

    @Override
    public void close() throws IOException {
        IOException closedException = null;

        for (DiskFile df : diskFiles) {
            try {
                df.close();
            } catch (IOException e) {
                closedException = e;
            }
        }
        if (closedException != null) {
            throw closedException;
        }
    }

    public SeekScanner createScanner(List<DiskFile> diskFiles) throws IOException {
        List<SeekScanner> scanners = new ArrayList<>();
        diskFiles.forEach(file -> scanners.add(file.iterator()));
        return new MultiScanner(scanners);
    }

    public SeekScanner createScanner() throws IOException {
        return createScanner(getDiskFiles());
    }
}