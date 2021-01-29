package learn.springcloud.config.client.stat;

import learn.base.utils.StopWatch;
import learn.datasource.entity.gk.GkAccount;
import learn.datasource.entity.gk.GkMiddleDailyActive;
import learn.datasource.entity.jk.JkAccount;
import learn.datasource.entity.jk.JkMiddleDailyActive;
import learn.datasource.mapper.gk.GkAccountMapper;
import learn.datasource.mapper.gk.GkMiddleDailyActiveMapper;
import learn.datasource.mapper.jk.JkAccountMapper;
import learn.datasource.mapper.jk.JkMiddleDailyActiveMapper;
import learn.springcloud.config.client.ConfigClientApp;
import learn.springcloud.config.client.entity.GkUser;
import learn.springcloud.config.client.entity.JkUser;
import learn.springcloud.config.client.entity.User;
import learn.springcloud.config.client.mapper.GkUserMapper;
import learn.springcloud.config.client.mapper.JkUserMapper;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.ToString;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.collections4.MapUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ConcurrentMap;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @author Zephyr
 * @date 2021/1/12.
 */
//@ActiveProfiles("stat")
@RunWith(SpringRunner.class)
@SpringBootTest(classes = ConfigClientApp.class)
public class DataStatTest {
    static long max_jk_id = 1532_6000;
    static long max_gk_id = 202_3549;
    static volatile boolean gkPrepareFinished = false;
    static volatile boolean jkPrepareFinished = false;
    static volatile boolean gkFinished = false;
    static volatile boolean jkFinished = false;

    static ConcurrentMap<String, User> createTimeMap = new ConcurrentHashMap<>(8096);

    @Autowired
    JkUserMapper jkUserMapper;  // dev jk_user
    @Autowired
    GkUserMapper gkUserMapper;  // dev gk_user
    @Autowired
    JkAccountMapper jkAccountMapper;  // prod jk raw_account
    @Autowired
    GkAccountMapper gkAccountMapper;  // prod gk raw_account
    @Autowired
    GkMiddleDailyActiveMapper gkMiddleDailyActiveMapper;
    @Autowired
    JkMiddleDailyActiveMapper jkMiddleDailyActiveMapper;

    @Test
    public void updateAccountCreateTime() throws InterruptedException {

        int segmentSize = 800;

        new Thread(() -> {
            long beginId = 1L; //2765001
            List<JkUser> jkUsers;
            do
            {
                jkUsers = jkUserMapper.selectBatchByIdBetween(beginId, beginId + segmentSize);
                beginId += segmentSize;
                if (CollectionUtils.isNotEmpty(jkUsers)) {
                    // 查询线上数据
                    List<Long> jkIds = jkUsers.stream().map(JkUser::getId).collect(Collectors.toList());
                    List<JkAccount> jkAccounts = jkAccountMapper.selectBatchIds(jkIds);
                    if (CollectionUtils.isNotEmpty(jkAccounts)) {

                        // 组装数据并更新到开发库
                        Map<Long, JkAccount> idAccountMap = jkAccounts.stream().collect(Collectors.toMap(JkAccount::getId, Function.identity()));
                        jkUsers.forEach(user -> {
                            JkAccount account = idAccountMap.get(user.getId());
                            if (account != null) {
                                user.setCreateTime(account.getCreateTime());
                                user.setUpdateTime(account.getUpdateTime());
                                user.setDataCenterId(account.getDataCenterId());
                                user.setLatestRequestTime(account.getLatestRequestTime());
                                user.setStatus(account.getStatus());

                            }
                        });
                        jkUserMapper.updateBatchById(jkUsers, segmentSize);
                    }
                }
                System.out.println("jk-id = " + beginId);
            } while (beginId <= max_jk_id);
            jkFinished = true;
        });


        new Thread(() -> {
            long beginId = 5111L; //336001
            List<GkUser> users;
            do
            {
                users = gkUserMapper.selectBatchByIdBetween(beginId, beginId + segmentSize);
                beginId += segmentSize;
                if (CollectionUtils.isNotEmpty(users)) {
                    // 查询线上数据
                    List<Long> userIds = users.stream().map(GkUser::getId).collect(Collectors.toList());
                    List<GkAccount> accounts = gkAccountMapper.selectBatchIds(userIds);

                    if (CollectionUtils.isNotEmpty(accounts)) {
                        // 组装数据并更新到开发库
                        Map<Long, GkAccount> idAccountMap = accounts.stream().collect(Collectors.toMap(GkAccount::getId, Function.identity()));
                        users.forEach(user -> {
                            GkAccount account = idAccountMap.get(user.getId());
                            if (account != null) {
                                user.setCreateTime(account.getCreateTime());
                                user.setUpdateTime(account.getUpdateTime());
                                user.setDataCenterId(account.getDataCenterId());
                                user.setLatestRequestTime(account.getLatestRequestTime());
                                user.setStatus(account.getStatus());
                            }
                        });
                        gkUserMapper.updateBatchById(users, segmentSize);
                    }
                }
                System.out.println("gk-id = " + beginId);
            } while (beginId <= max_gk_id);
            gkFinished = true;
        }).start();


        while (true) {
            TimeUnit.SECONDS.sleep(20);
            if (gkFinished) {
                return;
            }
        }
    }

    // ======================================


    /**
     * 统计用户创建时间相关数据
     */
    @Test
    public void statUserCreateTime() throws InterruptedException {
        StopWatch stopWatch = StopWatch.createAndStart("统计用户注册时间");
        int segmentSize = 800;
        Map<String, Timestamp> phoneCreateTimeMap = new ConcurrentHashMap<>(8096);
        Set<String> phoneSet = new HashSet<>(8096);

        new Thread(() -> {
            long startId = 1L;
            List<JkUser> users;
            do
            {
                users = jkUserMapper.selectBatchByIdBetween(startId, startId + segmentSize);
                startId += segmentSize;
                users.stream()
                        .filter(user -> Objects.nonNull(user) && user.getLatestRequestTime() != null && user.getDataCenterId() != null)
                        .forEach(user -> {
                            phoneSet.add(user.getPhone());
                            phoneCreateTimeMap.put("jk_" + user.getPhone(), user.getCreateTime());
                        });
                System.out.println("jk_id = " + startId + ", current user size = " + (users == null ? 0 : users.size()));
            } while (startId <= max_jk_id);
            jkFinished = true;
        }).start();

        new Thread(() -> {
            long startId = 1L;
            List<GkUser> users;
            do
            {
                users = gkUserMapper.selectBatchByIdBetween(startId, startId + segmentSize);
                startId += segmentSize;
                users.stream().filter(user -> Objects.nonNull(user) && user.getLatestRequestTime() != null && user.getDataCenterId() != null)
                        .forEach(user -> {
                            phoneSet.add(user.getPhone());
                            phoneCreateTimeMap.put("gk_" + user.getPhone(), user.getCreateTime());
                        });
                System.out.println("gk_id = " + startId + ", current user size = " + (users == null ? 0 : users.size()));
            } while (startId <= max_gk_id);
            gkFinished = true;
        }).start();

        while (true) {
            TimeUnit.SECONDS.sleep(10);
            if (jkFinished && gkFinished) {

                System.out.println(stopWatch.prettyPrint());
                this.printAppUserCreateTimeStats(phoneCreateTimeMap, phoneSet);
                System.out.println(stopWatch.stopAndPrint());
                return;
            }
        }
    }

    private void printAppUserCreateTimeStats(Map<String, Timestamp> timestampMap, Set<String> phoneSet) {
        System.out.println("timeMap = " + timestampMap.size());
        System.out.println("phone = " + phoneSet.size());
        System.out.println();
        System.out.println();
        Map<Integer, Map<Integer, AtomicInteger>> statMap = new HashMap<>();

        final Counter counter = new Counter();
        phoneSet.forEach(phone -> {
            Timestamp jk = timestampMap.get("jk_" + phone);
            Timestamp gk = timestampMap.get("gk_" + phone);

            if (jk != null && gk != null) {
                int year = jk.getYear() + 1900;
                if (year > 2017) {

                    Map<Integer, AtomicInteger> subMap = statMap.getOrDefault(year, new HashMap<>());
                    AtomicInteger atomicInteger = subMap.getOrDefault(gk.getYear() + 1900, new AtomicInteger(0));
                    atomicInteger.getAndIncrement();
                    subMap.put(gk.getYear() + 1900, atomicInteger);
                    statMap.put(year, subMap);
                }

                //Counter counter = statMap.getOrDefault(year, new Counter());
                //statMap.put(year, jk.compareTo(gk) < 0 ? counter.teacherFirst() : counter.teacherBehind());
            }
        });
        //System.out.println(counter); //{总用户数=347500, 其中教师先注册=233719} 67.15%
        System.err.println(statMap);
    }

    // =====================================


    /**
     * 同步用户创建一个月后的活跃天数
     */
    @Test
    public void syncDailyActive() throws InterruptedException {
        StopWatch stopWatch = StopWatch.createAndStart("同步用户创建一个月后的活跃天数");
        new Thread(() -> {
            int segmentSize = 1000;
            long startId = 1502_0000L;
            Timestamp activeTableExistTimestamp = new Timestamp(117, 4, 14, 0, 0, 0, 0);
            Timestamp crossTableTimestamp = new Timestamp(120, 9, 23, 0, 0, 0, 0);
            List<JkUser> users;
            do
            {
                users = jkUserMapper.selectBatchByIdBetween(startId, startId + segmentSize);
                startId += segmentSize;
                boolean activeTableHasValue = users.stream().allMatch(user -> user.getCreateTime().compareTo(activeTableExistTimestamp) > 0);
                if (!activeTableHasValue) {
                    for (JkUser user : users) {
                        user.setActiveTimesOneMonth(0);
                    }
                    jkUserMapper.updateBatchById(users, 300);
                    continue;
                }
                if (CollectionUtils.isEmpty(users)) {
                    continue;
                }

                String uids = users.stream().map(user -> String.valueOf(user.getId())).collect(Collectors.joining(","));
                List<JkMiddleDailyActive> activeList = jkMiddleDailyActiveMapper.countUidByUidInAndOneMonthRecently(uids);
                System.out.println(String.format("jk  startId = %d, users.size = %d, activeList.size = %d", startId, users.size(), activeList.size()));
                Map<Long, Integer> uidAndCountMap = activeList.stream().collect(
                        Collectors.toMap(JkMiddleDailyActive::getUid, JkMiddleDailyActive::getCount));

                // 用户创建日期对应的活跃时间跨表了
                List<Long> crossTableUsers = users.stream()
                        .filter(user -> user.getCreateTime().compareTo(crossTableTimestamp) > 0)
                        .map(JkUser::getId).collect(Collectors.toList());
                if (CollectionUtils.isNotEmpty(crossTableUsers)) {
                    String sub_uids = DataStatTest.toSqlString(crossTableUsers);
                    List<JkMiddleDailyActive> subActiveList = jkMiddleDailyActiveMapper.count2020UidByUidInAndOneMonthRecently(sub_uids);
                    subActiveList.forEach(jkMiddleDailyActive -> {
                        Integer count = uidAndCountMap.getOrDefault(jkMiddleDailyActive.getUid(), 0);
                        count += jkMiddleDailyActive.getCount();
                        uidAndCountMap.put(jkMiddleDailyActive.getUid(), count);
                    });
                }
                for (JkUser user : users) {
                    user.setActiveTimesOneMonth(uidAndCountMap.getOrDefault(user.getId(), 0));
                }
                jkUserMapper.updateBatchById(users, 300);
            } while (startId < max_jk_id);
            jkFinished = true;
        }).start();

        new Thread(() -> {
            int segmentSize = 400;
            long startId = 143_0000L;
            List<GkUser> users;
            do
            {
                users = gkUserMapper.selectBatchByIdBetween(startId, startId + segmentSize);
                startId += segmentSize;

                String uids = users.stream().map(user -> String.valueOf(user.getId())).collect(Collectors.joining(","));
                List<GkMiddleDailyActive> activeList = gkMiddleDailyActiveMapper.countUidByUidInAndOneMonthRecently(uids);
                System.out.println(String.format("gk  startId = %d, users.size = %d, activeList.size = %d", startId, users.size(), activeList.size()));

                Map<Long, Integer> uidAndCountMap = activeList.stream().collect(
                        Collectors.toMap(GkMiddleDailyActive::getUid, GkMiddleDailyActive::getCount));

                for (GkUser user : users) {
                    user.setActiveTimesOneMonth(uidAndCountMap.getOrDefault(user.getId(), 0));
                }
                gkUserMapper.updateBatchById(users, 300);
            } while (startId < max_gk_id);
            gkFinished = true;
        });

        while (true) {
            TimeUnit.SECONDS.sleep(10);
            if (jkFinished) {
                System.out.println(stopWatch.stopAndPrint());
                return;
            }
        }
    }

    // =====================================


    /**
     * 同步用户创建一个月后的活跃天数
     */
    @Test
    public void syncDailyActiveAfterRegisterSecondApp() throws InterruptedException {
        StopWatch stopWatch = StopWatch.createAndStart("同步两个 app 都注册后用户一个月内在两个 app 的活跃情况");
        Map<String, JkUser> jkUserMap = new ConcurrentHashMap<>();
        Map<String, GkUser> gkUserMap = new ConcurrentHashMap<>();

        new Thread(() -> {
            int segmentSize = 400;
            long startId = 1L;
            List<GkUser> users;
            do
            {
                users = gkUserMapper.selectBatchByIdBetween(startId, startId + segmentSize);
                startId += segmentSize;
                Map<String, GkUser> userMap = users.stream().collect(Collectors.toMap(user -> user.getPhone(), Function.identity(),
                        (oldVal, newVal) -> oldVal.getCreateTime().compareTo(newVal.getCreateTime())  > 0 ? oldVal : newVal));
                if (MapUtils.isNotEmpty(userMap)) {
                    gkUserMap.putAll(userMap);
                }
                System.out.println(String.format("gk  startId = %d", startId));
                users = null;
            } while (startId < max_gk_id);
            gkPrepareFinished = true;
        }).start();

        new Thread(() -> {
            int segmentSize = 1000;
            long startId = 1L;
            List<JkUser> jkUsers;
            do
            {
                jkUsers = jkUserMapper.selectBatchByIdBetween(startId, startId + segmentSize);
                startId += segmentSize;
                Map<String, JkUser> userMap = jkUsers.stream().collect(Collectors.toMap(user -> user.getPhone(), Function.identity(),
                        (oldVal, newVal) -> oldVal.getCreateTime().compareTo(newVal.getCreateTime())  > 0 ? oldVal : newVal));
                if (MapUtils.isNotEmpty(userMap)) {
                    jkUserMap.putAll(userMap);
                }
                System.out.println(String.format("jk  startId = %d", startId));
                jkUsers = null;
            } while (startId < max_jk_id);
            jkPrepareFinished = true;
        }).start();

        do
        {
            TimeUnit.SECONDS.sleep(5);
        } while (!gkPrepareFinished || !jkPrepareFinished);
        System.out.println("准备工作完成");

        System.out.println(stopWatch.prettyPrint());

        List<PairUser> pairUsers = new ArrayList<>();
        for (Map.Entry<String, GkUser> entry : gkUserMap.entrySet()) {
            String phone = entry.getKey();
            JkUser jkUser = jkUserMap.get(phone);
            if (jkUser != null) {
                GkUser gkUser = entry.getValue();
                User maxCreateTimeUser = this.getMaxCreateTimeUser(gkUser, jkUser);
                pairUsers.add(PairUser.builder().phone(phone)
                        .jkId(jkUser.getId()).gkId(gkUser.getId()).maxCreateTime(maxCreateTimeUser.getCreateTime()).build());
            }
        }
        System.out.println(stopWatch.prettyPrint());
        jkUserMap.clear();
        gkUserMap.clear();

        int maxListSize = 100;
        Timestamp activeTableExistTimestamp = new Timestamp(117, 4, 14, 0, 0, 0, 0);
        Timestamp crossTableTimestamp = new Timestamp(120, 9, 23, 0, 0, 0, 0);

        Runnable jkRunnable = () -> {
            List<JkUser> userList = new ArrayList<>();
            for (PairUser pairUser : pairUsers) {
                Timestamp maxCreateTime = pairUser.maxCreateTime;
                if (maxCreateTime.compareTo(activeTableExistTimestamp) < 0) {
                    return;
                }

                Integer count = jkMiddleDailyActiveMapper.countUidByUidAndCreateTime(pairUser.jkId, maxCreateTime);
                if (maxCreateTime.compareTo(crossTableTimestamp) > 0) {
                    count += jkMiddleDailyActiveMapper.count2020UidByUidAndCreateTime(pairUser.jkId, maxCreateTime);
                }
                userList.add(new JkUser(pairUser.jkId, count));

                if (userList.size() % maxListSize == 0) {
                    jkUserMapper.updateBatchById(userList, maxListSize);
                    System.out.println("update jk, pairUser = " + pairUser);
                    userList.clear();
                }
            }
            if (!userList.isEmpty()) {
                jkUserMapper.updateBatchById(userList, maxListSize);
            }
            jkFinished = true;
        };
        Runnable gkRunnable = () -> {
            List<GkUser> userList = new ArrayList<>();
            for (PairUser pairUser : pairUsers) {
                Integer count = gkMiddleDailyActiveMapper.countUidByUidAndCreateTime(pairUser.gkId, pairUser.maxCreateTime);
                userList.add(new GkUser(pairUser.gkId, count));

                if (userList.size() % maxListSize == 0) {
                    gkUserMapper.updateBatchById(userList, maxListSize);
                    System.out.println("update gk, pairUser = " + pairUser);
                    userList.clear();
                }
            }
            if (!userList.isEmpty()) {
                gkUserMapper.updateBatchById(userList, maxListSize);
            }
            gkFinished = true;
        };

        new Thread(jkRunnable).start();
        new Thread(gkRunnable).start();

        while (true) {
            TimeUnit.SECONDS.sleep(5);
            if (gkFinished) {
                System.out.println(stopWatch.stopAndPrint());
                return;
            }
        }
    }

    @Test
    public void testUpdate() {
        gkUserMapper.updateBatchById(Collections.singletonList(new GkUser(1L, -2)), 100);
    }

    // =================================

    /**
     * 双端用户在注册第二个平台账户后的活跃天数，对此获取四分位
     */
    @Test
    public void getQuartiles() {
        StopWatch stopWatch = StopWatch.createAndStart("计算双端用户注册后30天内的活跃天数");
        List<Integer> activeTimes = jkUserMapper.selectFieldList(JkUser::getActiveTimesBothRegister);
        System.out.println(stopWatch.prettyPrint());
        Map<Integer, Integer> jkQuartiles = DataStatTest.getQuartiles(activeTimes);
        System.out.println(stopWatch.prettyPrint());
        System.out.println("教考用户活跃天数四分位： " + jkQuartiles);


        List<Integer> gkActiveTimes = gkUserMapper.selectFieldList(GkUser::getActiveTimesBothRegister);
        System.out.println(stopWatch.prettyPrint());
        Map<Integer, Integer> gkQuartiles = DataStatTest.getQuartiles(gkActiveTimes);
        System.out.println("公考用户活跃天数四分位： " + gkQuartiles);
        System.out.println(stopWatch.prettyPrint());
    }


    @ToString
    @Builder
    @AllArgsConstructor
    private static class PairUser {
        String phone;
        Long jkId;
        Long gkId;
        Timestamp maxCreateTime;
    }

    private static class Counter {
        private AtomicInteger total = new AtomicInteger(0);
        private AtomicInteger teacherFirst = new AtomicInteger(0);


        public Counter teacherFirst() {
            teacherFirst.getAndIncrement();
            total.getAndIncrement();
            return this;
        }

        public Counter teacherBehind() {
            total.getAndIncrement();
            return this;
        }

        @Override
        public String toString() {
            return "{" + "总用户数=" + total +
                    ", 其中教师先注册=" + teacherFirst + '}';
        }
    }

    public static String toSqlString(List<Long> coll) {
        Iterator<Long> it = coll.iterator();
        if (! it.hasNext())
            return "";

        StringBuilder sb = new StringBuilder();
        for (;;) {
            Long e = it.next();
            sb.append(e);
            if (! it.hasNext())
                return sb.toString();
            sb.append(',').append(' ');
        }
    }

    public User getMaxCreateTimeUser(User user1, User user2) {
        return user1.getCreateTime().compareTo(user2.getCreateTime()) > 0 ? user1 : user2;
    }

    /**
     * n表示项数
     *      * Q1的位置= (n+1) × 0.25
     *      * Q2的位置= (n+1) × 0.5
     *      * Q3的位置= (n+1) × 0.75
     */
    public static Map<Integer, Integer> getQuartiles(List<Integer> array) {
        if (CollectionUtils.isEmpty(array) || array.size() < 4) {
            return Collections.emptyMap();
        }
        array.sort(Integer::compareTo);

        int n = array.size();
        double index1 = (n + 1) * 0.25D;
        double index2 = (n + 1) * 0.5D;
        double index3 = (n + 1) * 0.75D;

        Map<Integer, Integer> map = new HashMap<>();
        map.put(1, array.get(new Double(index1).intValue()));
        map.put(2, array.get(new Double(index2).intValue()));
        map.put(3, array.get(new Double(index3).intValue()));

        return map;
    }
}
