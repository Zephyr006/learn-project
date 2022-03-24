package learn.base.test.util;

import learn.example.javase.io.filedb.util.FileUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

/**
 * @author Zephyr
 * @date 2021/12/18.
 */
public class FileUtilsTests {
    private static final String path = "/Users/wang/Desktop/归档";

    //@Test
    public void testUnZip() throws IOException {
        File file = new File(path + ".ccr");
        file = FileUtils.changeSuffix(file, "zip");
        System.out.println(file.getPath());

        String parentPath = file.getParent();
        System.out.println(parentPath);
        ZipInputStream Zin = new ZipInputStream(new FileInputStream(file));
        ZipEntry entry;
        while ((entry = Zin.getNextEntry()) != null && !entry.isDirectory()) {
            File entryFile = new File(parentPath + File.pathSeparator + entry.getName());
            System.out.println(entryFile.getPath());
            System.out.println(entryFile.exists());
        }
    }

    //@Test
    public void toCcr() {
        File file = new File(path + ".zip");
        file = FileUtils.changeSuffix(file, "ccr");
        System.out.println(file.getPath());
    }

}
