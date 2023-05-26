package learn.dfs;

import java.util.concurrent.TimeUnit;

/**
 * @author Zephyr
 * @since 2021-12-19.
 */
public class NameNode {
    private volatile boolean shouldRun = true;

    private FSNameSystem nameSystem;
    private NameNodeRpcServer rpcServer;


    private void init() {
        this.nameSystem = new FSNameSystem();
        this.rpcServer = new NameNodeRpcServer(nameSystem);
        this.rpcServer.start();
    }

    private void run() {
        try {
            while (shouldRun) {
                TimeUnit.MILLISECONDS.sleep(2000);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}
