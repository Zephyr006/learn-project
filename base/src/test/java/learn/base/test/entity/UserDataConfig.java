package learn.base.test.entity;

import lombok.Getter;
import org.apache.commons.lang3.tuple.ImmutablePair;
import org.apache.commons.lang3.tuple.Pair;

/**
 * @author Zephyr
 * @date 2021/4/14.
 */
public interface UserDataConfig {
    Pair<String, String> JK_DbHostAndUsername = new ImmutablePair<>("", "questionbank_r");
    Pair<String, String> GK_DbHostAndUsername = new ImmutablePair<>("", "public_server_r");

    double doQuestionRate = 0.001;  // 答题覆盖率，低于此做题比例的学员不参与统计
    int doQuestionSize = 5;  // 最少做5道题
    int separate = 5;  // 5%为一档，统计每档做题情况
    boolean COMPARE_SPEED = false; // 按正确率排序还是按答题速度排序

    /**
     * 要查询标签树，还是知识点树
     */
    boolean isForTag();
    /**
     * 是来自"tree"表的id，还是来自标签表的parentId
     */
    boolean isTreeId();
    String getExcelPath();
    Pair<Long, String> getTreeRoot();
    default String getExcelSavePath() {
        return "/Users/wangshidong/Desktop/学员做题数据统计.xlsx";
    }
    // 教考生产
    Pair<String, String> dbHostAndUsername();


    @Getter
    class GkSearch1 implements UserDataConfig {
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
    class GkSearch2 implements UserDataConfig {
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(4L, "判断推理");
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
        public String getExcelPath() {
            return "/Users/wangshidong/Downloads/公考-知识点树-用户.xlsx";
        }
        @Override
        public Pair<String, String> dbHostAndUsername() {
            return GK_DbHostAndUsername;
        }
    }

    @Getter
    class JkSearch3 implements UserDataConfig {
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
    class JkSearch4 implements UserDataConfig {
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(268L, "教学原则和教学方法");
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
            return "/Users/wangshidong/Downloads/教考-知识树-用户.xlsx";
        }
    }

    @Getter
    class JkSearch5 implements UserDataConfig {
        @Override
        public Pair<Long, String> getTreeRoot() {
            return ImmutablePair.of(381L, "行为主义学习理论");
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
            return "/Users/wangshidong/Downloads/教考-知识树-用户.xlsx";
        }
    }

    @Getter
    class JkSearch6 implements UserDataConfig {
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



}
