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
import learn.springcloud.config.client.mapper.GkUserMapper;
import learn.springcloud.config.client.mapper.JkUserMapper;
import org.apache.commons.collections4.CollectionUtils;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.sql.Timestamp;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
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
    static long max_jk_id = 1508_5549;
    static long max_gk_id = 202_3549;
    static volatile boolean gkFinished = false;
    static volatile boolean jkFinished = false;

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
            long startId = 827_0000L;
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
        }).start();

        while (true) {
            TimeUnit.SECONDS.sleep(10);
            if (jkFinished && gkFinished) {
                System.out.println(stopWatch.stopAndPrint());
                return;
            }
        }
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
            return "{" +
                    "总用户数=" + total +
                    ", 其中教师先注册=" + teacherFirst +
                    '}';
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
}
