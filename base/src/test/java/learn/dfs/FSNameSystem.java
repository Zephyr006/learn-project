package learn.dfs;

/**
 * 负责管理集群中文件元数据的核心组件
 * @author Zephyr
 * @since 2021-12-19.
 */
public class FSNameSystem {

    private FSDirectory directory;
    private FSEditLog editLog;

    public FSNameSystem() {
        this.directory = new FSDirectory();
        this.editLog = new FSEditLog();
    }

    public boolean mkdir(String path) {
        directory.mkdir(path);
        editLog.logEdit("创建了目录：" + path);
        return true;
    }
}
