package storage.hbase.flusher;

import storage.hbase.DiskFile;
import storage.hbase.DiskStore;
import storage.hbase.KeyValue;
import storage.hbase.flusher.Flusher;
import storage.hbase.scanner.Scanner;

import java.io.File;
import java.io.IOException;

import static storage.hbase.DiskStore.FILE_NAME_TMP_SUFFIX;

public class DefaultFlusher implements Flusher {

    private DiskStore diskStore;

    public DefaultFlusher(DiskStore diskStore) {
        this.diskStore = diskStore;
    }

    @Override
    public void flush(Scanner<KeyValue> scanner) throws IOException {
        String fileName = diskStore.getNextDiskFileName();
        String tempName = fileName + FILE_NAME_TMP_SUFFIX;

        try {
            try (DiskFile.DiskFileWriter writer = new DiskFile.DiskFileWriter(tempName)) {

                while (scanner.hasNext()) {
                    writer.append(scanner.next());
                }
                writer.appendIndex();
                writer.appendTrailer();
            }
            File file = new File(tempName);

            if (!file.renameTo(new File(fileName))) {
                throw new IOException(
                        "Rename " + tempName + " to " + fileName + " failed when flushing");
            }
            // TODO any concurrent issue ?
            diskStore.addDiskFile(fileName);
        } finally {
            File file = new File(tempName);

            if (file.exists()) {
                file.delete();
            }
        }
    }
}
