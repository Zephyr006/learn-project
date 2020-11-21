package learn.example.javase.io.filedb.util;

import java.io.File;
import java.io.IOException;

/**
 * @author Zephyr
 * @date 2020/5/27.
 */
public class FileUtils {

    public static void forceMkdir(File directory) throws IOException {
        String message;
        if (directory.exists()) {
            if (directory.isFile()) {
                message = "File " + directory + " exists and is " + "not a directory. Unable to create directory.";
                throw new IOException(message);
            }
        } else if (!directory.mkdirs()) {
            message = "Unable to create directory " + directory;
            throw new IOException(message);
        }

    }
}
