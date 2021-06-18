package learn.base.test.entity;

import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Zephyr
 * @date 2021/4/14.
 */
public interface UserDataConfig {
    String dbName = "relation";
    String password = "";
    Pair<String, String> JK_DbHostAndUsername = new ImmutablePair<>("", "");
    Pair<String, String> GK_DbHostAndUsername = new ImmutablePair<>("", "");

    double doQuestionRate = 0.001;  // 答题覆盖率，低于此做题比例的学员不参与统计
    int levelSeparate = 5;                  // 5%为一档，统计每档做题情况
    boolean COMPARE_SPEED = false;     // 按正确率排序还是按答题速度排序
    boolean getLowestUserData = true;  // 输出统计数据时，取每个分档中最后一名学员的做题数据，而不是取每个分档的平均值
    Boolean getSubjective = false;      // 是否只查主观题，null为不区分主客观题


    Pair<String, String> dbHostAndUsername();
    String getExcelPath();
    default String getExcelSavePath() {
        return "/Users/wangshidong/Desktop/学员做题数据统计.xlsx";
    }
    /**
     * 学员对一批题的最少做题数量，低于此做题数据的学员不纳入统计范围
     */
    default int getDoQuestionSize() {
        return 5;
    }
    /**
     * 是否只查询叶子节点，如果只查询叶子节点，则每个节点分开统计；如果false，查询所有节点并按整棵树统计
     */
    default boolean onlyLeafNode() {
        return false;
    }
    /**
     * 要查询标签树，还是知识点树
     */
    boolean isForTag();
    /**
     * 是来自"tree"表的id，还是来自节点（标签或知识点）表的parentId
     */
    boolean isTreeId();
    /**
     * Pair<要查询的根节点ID, 要查询的根节点名称>
     */
    Pair<Long, String> getTreeRoot();


    @Getter
    class GkSearch7 implements UserDataConfig {
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(30L, "判断推理 整体题库");
        }
        @Override
        public boolean isForTag() {
            return true;
        }
        @Override
        public boolean isTreeId() {
            return true;
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/公考-标签树-用户.xlsx";
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return GK_DbHostAndUsername;
        }
    }

    @Getter
    class GkSearch1 implements UserDataConfig {
        @Override
        public boolean onlyLeafNode() {
            return false;
        }
        @Override
        public int getDoQuestionSize() {
            return 100;
        }
        @Override
        public boolean isForTag() {
            return false;
        }
        @Override
        public boolean isTreeId() {
            return true;
        }
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(1L, "常识判断");
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/公考去年购买练习班的用户.xlsx";
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return GK_DbHostAndUsername;
        }
    }

    @Getter
    class GkSearch2 implements UserDataConfig {
        @Override
        public boolean onlyLeafNode() {
            return false;
        }
        @Override
        public int getDoQuestionSize() {
            return 100;
        }
        @Override
        public boolean isForTag() {
            return false;
        }
        @Override
        public boolean isTreeId() {
            return true;
        }
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(2L, "言语理解与表达");
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/公考去年购买练习班的用户.xlsx";
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return GK_DbHostAndUsername;
        }
    }

    @Getter
    class GkSearch3 implements UserDataConfig {
        @Override
        public boolean onlyLeafNode() {
            return false;
        }
        @Override
        public int getDoQuestionSize() {
            return 50;
        }
        @Override
        public boolean isForTag() {
            return false;
        }
        @Override
        public boolean isTreeId() {
            return true;
        }
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(3L, "数量关系");
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/公考去年购买练习班的用户.xlsx";
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return GK_DbHostAndUsername;
        }
    }

    @Getter
    class GkSearch4 implements UserDataConfig {
        @Override
        public boolean onlyLeafNode() {
            return false;
        }
        @Override
        public int getDoQuestionSize() {
            return 100;
        }
        @Override
        public boolean isForTag() {
            return false;
        }
        @Override
        public boolean isTreeId() {
            return true;
        }
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(4L, "判断推理");
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/公考去年购买练习班的用户.xlsx";
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return GK_DbHostAndUsername;
        }
    }

    @Getter
    class GkSearch5 implements UserDataConfig {
        @Override
        public boolean onlyLeafNode() {
            return false;
        }
        @Override
        public int getDoQuestionSize() {
            return 50;
        }
        @Override
        public boolean isForTag() {
            return false;
        }
        @Override
        public boolean isTreeId() {
            return true;
        }
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(5L, "资料分析");
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/公考去年购买练习班的用户.xlsx";
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return GK_DbHostAndUsername;
        }
    }


    @Getter
    class JkSearch1 implements UserDataConfig {
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(14L, "教师职业道德");
        }
        @Override
        public boolean isForTag() {
            return false;
        }
        @Override
        public boolean isTreeId() {
            return true;
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return JK_DbHostAndUsername;
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/教考-知识树-用户.xlsx";
        }
    }

    @Getter
    class JkSearch2 implements UserDataConfig {
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(268L, "教学原则和教学方法"); // 627 - 46
        }
        @Override
        public int getDoQuestionSize() {
            return 50;
        }
        @Override
        public boolean isForTag() {
            return true;
        }
        @Override
        public boolean isTreeId() {
            return false;
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return JK_DbHostAndUsername;
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/教考-用户中台id.xlsx";
        }
    }

    @Getter
    class JkSearch3 implements UserDataConfig {
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(381L, "行为主义学习理论");  // 408 - 13
        }
        @Override
        public int getDoQuestionSize() {
            return 50;
        }
        @Override
        public boolean isForTag() {
            return true;
        }
        @Override
        public boolean isTreeId() {
            return false;
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return JK_DbHostAndUsername;
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/教考-用户中台id.xlsx";
        }
    }

    @Getter
    class JkSearch4 implements UserDataConfig {
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(14L, "教师职业道德");  // 3281 - 462
        }
        @Override
        public int getDoQuestionSize() {
            return 50;
        }
        @Override
        public boolean isForTag() {
            return false;
        }
        @Override
        public boolean isTreeId() {
            return true;
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return JK_DbHostAndUsername;
        }
        @Override
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/教考-用户中台id.xlsx";
        }
    }



}
