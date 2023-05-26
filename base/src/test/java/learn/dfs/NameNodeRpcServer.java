package learn.dfs;

/**
 * @author Zephyr
 * @since 2021-12-19.
 */
public class NameNodeRpcServer {
    private FSNameSystem nameSystem;

    public NameNodeRpcServer(FSNameSystem nameSystem) {
        this.nameSystem = nameSystem;
    }

    public boolean mkdir(String path) throws Exception {
        return true;
    }

    public void start() {
        System.out.println("启动rpcServer监听指定地址");
    }
}
