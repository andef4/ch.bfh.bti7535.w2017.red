package ch.bfh.bti7535.w2017.red;

import org.rauschig.jarchivelib.ArchiveFormat;
import org.rauschig.jarchivelib.Archiver;
import org.rauschig.jarchivelib.ArchiverFactory;
import org.rauschig.jarchivelib.CompressionType;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.nio.file.StandardCopyOption;

public class Download {
    public static void main(String[] args) throws IOException {
        // download file
        URL url = new URL("http://www.cs.cornell.edu/people/pabo/movie-review-data/review_polarity.tar.gz");
        File file = new File("data/review_polarity.tar.gz");
        Files.copy(url.openStream(), file.toPath(), StandardCopyOption.REPLACE_EXISTING);

        // unpack file
        Archiver archiver = ArchiverFactory.createArchiver(ArchiveFormat.TAR, CompressionType.GZIP);
        File destination = new File("data/");
        archiver.extract(file, destination);

        // delete temporary files
        boolean result = file.delete();
        if (!result) {
            throw new RuntimeException("Cannot delete temporary file.");
        }
    }
}
