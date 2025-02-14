import com.alibaba.fastjson.JSONObject;
import com.alibaba.fastjson.TypeReference;
import lombok.extern.slf4j.Slf4j;
import org.apache.hadoop.hive.ql.exec.UDF;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.web.client.RestTemplate;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.util.*;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.stream.Collectors;

@Slf4j
public class StockDataFetcher {

    private static final String BASE_URL = "http://168.63.17.218:6400/reqxml";
    private static final int THREAD_POOL_SIZE = 20;
    private static final ExecutorService executorService = Executors.newFixedThreadPool(THREAD_POOL_SIZE);
    private static final RestTemplate restTemplate = new RestTemplate();

    // Hive配置
    private static final String HIVE_URL = "jdbc:hive2://your-hive-server:10000/default";
    private static final String HIVE_USER = "your-username";
    private static final String HIVE_PASSWORD = "your-password";

    public static void main(String[] args) {
        // 如果传入了特定的股票代码,则只处理该股票
        List<String> stockCodes = args.length > 0 ?
                Collections.singletonList(args[0]) : getStockCodes();

        createHiveTableIfNotExists();

        long startTime = System.currentTimeMillis();

        List<CompletableFuture<Void>> futures = stockCodes.stream()
                .map(code -> CompletableFuture.runAsync(() -> processStockData(code), executorService))
                .collect(Collectors.toList());

        // 等待所有任务完成
        CompletableFuture.allOf(futures.toArray(new CompletableFuture[0])).join();

        long endTime = System.currentTimeMillis();
        log.info("Total processing time: {} ms", (endTime - startTime));

        executorService.shutdown();
    }

    private static void processStockData(String stockCode) {
        try {
            log.info("Processing stock code: {}", stockCode);
            List<Map<String, Object>> stockData = fetchStockData(stockCode);
            writeToHive(stockCode, stockData);
            log.info("Completed processing stock code: {}", stockCode);
        } catch (Exception e) {
            log.error("Error processing stock code: " + stockCode, e);
        }
    }

    private static void createHiveTableIfNotExists() {
        try (Connection conn = getHiveConnection()) {
            String createTableSQL =
                    "CREATE TABLE IF NOT EXISTS stock_minute_data (" +
                            "  trade_time TIMESTAMP," +
                            "  open_price DECIMAL(10,2)," +
                            "  high_price DECIMAL(10,2)," +
                            "  low_price DECIMAL(10,2)," +
                            "  close_price DECIMAL(10,2)," +
                            "  volume BIGINT," +
                            "  amount DECIMAL(20,2)" +
                            ") PARTITIONED BY (stock_code STRING)" +
                            "  STORED AS PARQUET";

            conn.createStatement().execute(createTableSQL);
        } catch (Exception e) {
            log.error("Error creating Hive table", e);
            throw new RuntimeException(e);
        }
    }

    private static void writeToHive(String stockCode, List<Map<String, Object>> data) {
        try (Connection conn = getHiveConnection()) {
            // 先删除该股票的分区数据
            String dropPartitionSQL =
                    String.format("ALTER TABLE stock_minute_data DROP IF EXISTS PARTITION (stock_code='%s')", stockCode);
            conn.createStatement().execute(dropPartitionSQL);

            // 添加新分区
            String addPartitionSQL =
                    String.format("ALTER TABLE stock_minute_data ADD PARTITION (stock_code='%s')", stockCode);
            conn.createStatement().execute(addPartitionSQL);

            // 批量插入数据
            String insertSQL =
                    "INSERT INTO TABLE stock_minute_data PARTITION (stock_code=?) " +
                            "VALUES (?, ?, ?, ?, ?, ?, ?)";

            PreparedStatement pstmt = conn.prepareStatement(insertSQL);

            for (Map<String, Object> record : data) {
                pstmt.setString(1, stockCode);
                pstmt.setTimestamp(2, java.sql.Timestamp.valueOf(record.get("HQDATE").toString()));
                pstmt.setBigDecimal(3, new java.math.BigDecimal(record.get("OPEN").toString()));
                pstmt.setBigDecimal(4, new java.math.BigDecimal(record.get("HIGH").toString()));
                pstmt.setBigDecimal(5, new java.math.BigDecimal(record.get("LOW").toString()));
                pstmt.setBigDecimal(6, new java.math.BigDecimal(record.get("CLOSE").toString()));
                pstmt.setLong(7, Long.parseLong(record.get("VOLUME").toString()));
                pstmt.setBigDecimal(8, new java.math.BigDecimal(record.get("AMOUNT").toString()));

                pstmt.addBatch();
            }

            pstmt.executeBatch();
        } catch (Exception e) {
            log.error("Error writing data to Hive for stock code: " + stockCode, e);
            throw new RuntimeException(e);
        }
    }

    private static Connection getHiveConnection() throws Exception {
        Class.forName("org.apache.hive.jdbc.HiveDriver");
        return DriverManager.getConnection(HIVE_URL, HIVE_USER, HIVE_PASSWORD);
    }

    private static List<Map<String, Object>> fetchStockData(String stockCode) {
        List<Map<String, Object>> allData = new ArrayList<>();
        String currentUrl = buildInitialUrl(stockCode);

        try {
            while (true) {
                Map<String, Object> response = fetchDataFromUrl(currentUrl);
                if (response == null) break;

                // 处理返回的数据
                processResponseData(response, allData);

                // 检查是否需要继续获取历史数据
                String nextLocator = getNextLocator(response);
                if (nextLocator == null || shouldStopFetching(response)) break;

                currentUrl = buildUrlWithLocator(stockCode, nextLocator);
            }
        } catch (Exception e) {
            log.error("Error fetching data for stock code: " + stockCode, e);
        }

        return allData;
    }

    private static Map<String, Object> fetchDataFromUrl(String url) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Content-Type", "application/json;charset=utf8");
        HttpEntity<Object> entity = new HttpEntity<>(headers);

        try {
            return restTemplate.exchange(url, HttpMethod.GET, entity, Map.class).getBody();
        } catch (Exception e) {
            log.error("Error fetching data from URL: " + url, e);
            return null;
        }
    }

    private static void processResponseData(Map<String, Object> response, List<Map<String, Object>> allData) {
        if (response.containsKey("Min1KLine")) {
            List<List<Object>> klineData = (List<List<Object>>) response.get("Min1KLine");
            for (List<Object> item : klineData) {
                String dataStr = item.get(0).toString().replaceAll("=", ":");
                Map<String, Object> dataMap = JSONObject.parseObject(dataStr,
                        new TypeReference<Map<String, Object>>() {});
                allData.add(dataMap);
            }
        }
    }

    private static String getNextLocator(Map<String, Object> response) {
        if (response.containsKey("501.locator")) {
            List<List<Object>> locators = (List<List<Object>>) response.get("501.locator");
            if (!locators.isEmpty() && !locators.get(0).isEmpty()) {
                return locators.get(0).get(0).toString();
            }
        }
        return null;
    }

    private static boolean shouldStopFetching(Map<String, Object> response) {
        if (response.containsKey("Min1KLine")) {
            List<List<Object>> klineData = (List<List<Object>>) response.get("Min1KLine");
            if (!klineData.isEmpty() && !klineData.get(0).isEmpty()) {
                String dataStr = klineData.get(0).get(0).toString().replaceAll("=", ":");
                Map<String, Object> dataMap = JSONObject.parseObject(dataStr,
                        new TypeReference<Map<String, Object>>() {});
                String hqDate = dataMap.get("HQDATE").toString();
                return hqDate.contains("202302");
            }
        }
        return false;
    }

    private static String buildInitialUrl(String stockCode) {
        return String.format("%s?action=10002&code=%s&market=1&klinetype=501&cqtype=0&props=0|1|2&klineunit=1&501.count=-1500",
                BASE_URL, stockCode);
    }

    private static String buildUrlWithLocator(String stockCode, String locator) {
        return String.format("%s?action=10002&code=%s&market=1&klinetype=501&cqtype=0&props=0|1|2&klineunit=1&501.count=-1500&501.locator=%s",
                BASE_URL, stockCode, locator);
    }

    private static List<String> getStockCodes() {
        // 实现获取股票代码列表的逻辑
        // 这里需要返回所有的股票代码
        return new ArrayList<>();
    }

    private static void writeToHive(List<List<Map<String, Object>>> results) {
        // 实现写入Hive的逻辑
        // 可以使用HiveJDBC或其他方式写入数据
    }
}