package learn.demo;

import java.awt.*;
import java.awt.datatransfer.Clipboard;
import java.awt.datatransfer.StringSelection;
import java.awt.event.KeyEvent;
import java.util.Map;

/**
 * @author Zephyr
 * @date 2021/11/21.
 */
public class RobotTest {

    public static void main(String[] args) throws AWTException {
        // 获取系统剪切板
        Clipboard clip = Toolkit.getDefaultToolkit().getSystemClipboard();

        Robot robot = new Robot();
        // 必要的延迟，事件生成后自动休眠指定的时间间隔。不能删
        robot.setAutoDelay(200);
        //robot.delay(500);


        clip.setContents(new StringSelection("wechat"), null);
        openSoftware(robot);

        String fang_id = "z1994fang";
        clip.setContents(new StringSelection("文件传输助手"), null);
        weChatSearch(robot);

        String[] mottoes = {
                //"我只爱你四天，春天夏天秋天冬天！",
                //"我只爱你三天，昨天,今天,明天！",
                //"我只爱你两天，白天，黑天！",
                //"我只爱你一天，每一天！",
                //"[玫瑰]爱你么么哒！",
                //"[呲牙][坏笑]",
                "[奸笑]"
        };
        for (String message : mottoes) {
            //weChatSendMessage(robot, clip, message);
        }
    }

    private static void weChatSendMessage(Robot robot, Clipboard clip, String content) {
        clip.setContents(new StringSelection(content), null);
        //robot.delay(300);
        ctrl(robot, KeyEvent.VK_V);
        keyPressAndRelease(robot, KeyEvent.VK_ENTER);
    }


    private static void weChatSearch(Robot robot) {
        //robot.delay(800);
        ctrl(robot, KeyEvent.VK_F);
        ctrl(robot, KeyEvent.VK_V);

        //robot.delay(500);
        keyPressAndRelease(robot, KeyEvent.VK_ENTER);
    }

    public static void openSoftware(Robot robot) {
        ctrl(robot, KeyEvent.VK_SPACE);

        //robot.delay(100);
        ctrl(robot, KeyEvent.VK_V);
        robot.delay(400);
        keyPressAndRelease(robot, KeyEvent.VK_ENTER);
    }



    private static void keyPressAndRelease(Robot robot, int... keyEvent) {
        for (int k : keyEvent) {
            robot.keyPress(k);
            robot.keyRelease(k);
        }
    }

    private static void ctrl(Robot robot, int keyEvent) {
        Map<String, String> env = System.getenv();
        boolean isMac = env.getOrDefault("PATH", "").startsWith("/") || env.getOrDefault("HOME", "").startsWith("/");
        int ctrlKeyEvent = isMac ? KeyEvent.VK_META : KeyEvent.VK_CONTROL; // mac上对应command键
        robot.keyPress(ctrlKeyEvent);
        robot.keyPress(keyEvent);
        robot.keyRelease(keyEvent);
        robot.keyRelease(ctrlKeyEvent);
    }


}
