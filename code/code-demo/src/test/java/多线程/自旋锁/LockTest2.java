package 多线程.自旋锁;

import com.google.common.collect.Maps;
import org.apache.commons.lang3.StringUtils;
import org.joda.time.DateTime;
import org.junit.After;
import org.junit.Before;
import org.junit.Test;
import redis.clients.jedis.Jedis;
import 单元测试.redisDemo.RedisClient;

import java.util.Map;
import java.util.Random;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.TimeUnit;

public class LockTest2 {

    public Jedis client;

    @Before
    public void 开始() {
        client = RedisClient.getClient();
    }

    @After
    public void 结束() {
        client.close();
    }

    private static ExecutorService executorService = Executors.newCachedThreadPool();

    private Random random = new Random();

    private static String key = "test-key";

    private SpinLock spinLock = new SpinLock();

    @Test
    public void SpinLockTest() {

        System.out.println("开始");
        for (int i = 0; i < 100; i++) {
            final int num = i;
//            CompletableFuture.runAsync(() -> {
//                demo(num);
//            });

            executorService.submit(() -> {
                demo(num);
            });

            try {
                TimeUnit.MILLISECONDS.sleep(Long.parseLong(String.valueOf(random.nextInt(200))));
            } catch (InterruptedException e) {
                e.printStackTrace();
            }

        }

        System.out.println("结束");

        try {
            TimeUnit.SECONDS.sleep(5L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }

    }

    public String demo(Object object) {
        spinLock.lock();

        String data = getData();

        printTime(object + " : " + data);
        if (StringUtils.isBlank(data)) {
            data = createData();
        }

        spinLock.unlock();

        return data;
    }


    public String getData() {
        String data = client.get(key);
//        System.out.println("从缓存中获取数据：" + data);
        return data;
    }


    public String createData() {
        try {
            // 模拟网络耗时2s
            TimeUnit.SECONDS.sleep(2L);
        } catch (InterruptedException e) {
        }
        client.set(key, "testData");
        client.expire(key, 5);

        return "testData";
    }


    public static void add() {
//        sleep();
        try {
            TimeUnit.MILLISECONDS.sleep(1000L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }


    public static void sleep() {
        try {
            TimeUnit.SECONDS.sleep(1L);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
    }

    public static void printTime(Object object) {
//        System.out.println(DateTime.now().toString() + " : " + object);
        DateTime now = DateTime.now();
//        System.out.println("打印 " + now.toString("HH:mm:ss:sss") + " : " + object);
        int minuteOfHour = now.getMinuteOfHour();
        int secondOfMinute = now.getSecondOfMinute();
        int millisOfSecond = now.getMillisOfSecond();

        System.out.println("打印 " + minuteOfHour + ":" + secondOfMinute + ":" + millisOfSecond + "" + " : " + object);


    }

    @Test
    public void test1() {

        for (int i = 0; i < 100; i++) {
            int num = random.nextInt(200);
            System.out.println(num);
        }
    }
}
