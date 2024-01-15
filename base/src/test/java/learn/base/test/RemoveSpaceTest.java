package learn.base.test;

import learn.simulation.CommandUtil;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.StringSelection;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.IOException;
import java.time.LocalTime;
import java.util.Arrays;

public class RemoveSpaceTest {

    public static void main(String[] args) throws IOException, UnsupportedFlavorException, InterruptedException {
        String content = "";
        for (;;) {
            try {
                Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();
                DataFlavor[] availableDataFlavors = clip.getAvailableDataFlavors();
                //System.out.println(availableDataFlavors);
                if (availableDataFlavors != null && Arrays.asList(availableDataFlavors).contains(DataFlavor.stringFlavor)) {
                    content = (String) clip.getData(DataFlavor.stringFlavor);
                } else {
                    continue;
                }

                boolean replaced = false;
                if (content != null) {
                    int extIndex = content.indexOf(" ");
                    if (extIndex >= 0) {
                        StringBuilder builder = new StringBuilder(content.length());
                        for (int i = 0; i < content.length(); i++) {
                            if (content.charAt(i) == ' ' && notNearLetter(content, i)) {
                                replaced = true;
                            } else {
                                builder.append(content.charAt(i));
                            }
                        }
                        content = builder.toString();
                    }

                    while (content.contains(";")) {
                        content = content.replaceAll(";", "；");
                        replaced = true;
                    }
                }
                if (replaced) {
                    CommandUtil.copyToClipboard(new StringSelection(content));
                    System.out.println(LocalTime.now() + "  replaced..");
                }
            } catch (Exception e) {
                System.err.println(e.getMessage());
            }
            Thread.sleep(100);

        }

    }

    private static boolean notNearLetter(String content, int index) {
        if (index - 1 >= 0 && (Character.isLowerCase(content.charAt(index - 1)) || Character.isUpperCase(content.charAt(index - 1)))) {
            return false;
        }
        if (index + 1 < content.length() && (Character.isLowerCase(content.charAt(index + 1)) || Character.isUpperCase(content.charAt(index + 1)))) {
            return false;
        }
        return true;
    }


}
