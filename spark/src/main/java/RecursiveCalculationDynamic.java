import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.Dataset;
import org.apache.spark.sql.Row;
import org.apache.spark.sql.RowFactory;
import org.apache.spark.sql.types.*;
import org.apache.spark.sql.functions;
import org.apache.spark.sql.api.java.UDF2;

import java.util.List;

/**
 * Created By 19921227 on 2025/3/19 11:15
 */
public class RecursiveCalculationDynamic {
    public static void main(String[] args) {
        // 创建 SparkSession
        SparkSession spark = SparkSession
                .builder()
                .appName("RecursiveCalculationDynamic")
                .master("local[*]")
                .getOrCreate();
        // ======1.数据加载
        // 读取日K线数据
        Dataset<Row> klineDF = spark
                .sql("select HTSCSecurityID, MDDate, MDTime, NumTrades, TotalVolumeTrade, TotalvalueTrade, OpenPx, HighPx, LowPx, ClosePx, SecurityType, SecuritySubType, SecurityID, SecurityIDSource, `Symbol`, AfterHoursNumTrades, AfterHoursTotalVolumeTrade, AfterHoursTotalValueTrade, MacroDetial from htsc_ods_ins.XSHE_Stock_KLine1Day where month>='202301'");

        // 读取factor因子表数据
        Dataset<Row> factorDF = spark
                .sql("select stock_code, `date` AS adjust_date, sgbl, pgbl, pgj, xjhl, zzgbl from htsc_ods_ins.t_stock_split_zl where `date` > '20230301'");

        // =====2.数据预处理
        // 对日K表添加 PreClosePrice（昨收价） 字段
        // 创建窗口函数，按照 SecurityID 分区，并按照 MDDate 升序排列
        WindowSpec klineWindow = Window.partitionBy("SecurityID").orderBy("MDDate");
        // 计算 PreClosePrice（昨收价）
        klineDF = klineDF.withColumn(
                "PreClosePrice",
                functions.lag("ClosePx", 1).over(klineWindow)
        );

        // 对factor因子表数据按照stock_code分区并按日期排序
        WindowSpec factorWindow = Window
                .partitionBy("stock_code")
                .orderBy(factorDF.col("adjust_date"));

        Dataset<Row> sortedFactorDF = factorDF
                .withColumn("rn", functions.row_number().over(factorWindow))
                .drop("rn");

        // =====3.时间范围关联
        Column joinCondition = klineDF.col("SecurityID").equalTo(factorDF.col("stock_code"))
                .and(klineDF.col("MDDate").lt(factorDF.col("adjust_date")));

        Dataset<Row> joinedDF = klineDF.join(sortedFactorDF, joinCondition, "left")
                .select(
                        klineDF.col("HTSCSecurityID"),
                        klineDF.col("MDDate"),
                        klineDF.col("MDTime"),
                        klineDF.col("NumTrades"),
                        klineDF.col("TotalVolumeTrade"),
                        klineDF.col("TotalValueTrade"),
                        klineDF.col("OpenPx"),
                        klineDF.col("HighPx"),
                        klineDF.col("LowPx"),
                        klineDF.col("ClosePx"),
                        klineDF.col("SecurityType"),
                        klineDF.col("SecuritySubType"),
                        klineDF.col("SecurityID"),
                        klineDF.col("SecurityIDSource"),
                        klineDF.col("`Symbol`"),
                        klineDF.col("AfterHoursNumTrades"),
                        klineDF.col("AfterHoursTotalVolumeTrade"),
                        klineDF.col("AfterHoursTotalValueTrade"),
                        klineDF.col("MacroDetail"),
                        sortedFactorDF.col("adjust_date"),
                        sortedFactorDF.col("xihl"),
                        sortedFactorDF.col("pgj"),
                        sortedFactorDF.col("pgbl"),
                        sortedFactorDF.col("sgbl")
                );

        // =====4.聚合排序
        Dataset<Row> aggregatedDF = joinedDF
                .groupBy(
                        joinedDF.col("HTSCSecurityID"),
                        joinedDF.col("MDDate"),
                        joinedDF.col("MDTime"),
                        joinedDF.col("NumTrades"),
                        joinedDF.col("TotalVolumeTrade"),
                        joinedDF.col("TotalValueTrade"),
                        joinedDF.col("OpenPx"),
                        joinedDF.col("HighPx"),
                        joinedDF.col("LowPx"),
                        joinedDF.col("ClosePx"),
                        joinedDF.col("SecurityType"),
                        joinedDF.col("SecuritySubType"),
                        joinedDF.col("SecurityID"),
                        joinedDF.col("SecurityIDSource"),
                        joinedDF.col("`Symbol`"),
                        joinedDF.col("AfterHoursNumTrades"),
                        joinedDF.col("AfterHoursTotalVolumeTrade"),
                        joinedDF.col("AfterHoursTotalValueTrade"),
                        joinedDF.col("MacroDetail"))
                .agg(
                        functions.sort_array(
                                functions.collect_list(
                                        functions.struct(
                                                joinedDF.col("adjust_date"),
                                                joinedDF.col("xjhl"),
                                                joinedDF.col("pgj"),
                                                joinedDF.col("pgbl"),
                                                joinedDF.col("sgbl")
                                        )
                                ),
                                true
                        ).alias("code_sequence")
                );

        // =====5.注册UDF
        spark.udf().register("calculate_adjustment",
                (Double price, scala.collection.Seq<Row> codeSequence) -> {
                    double currentPrice = price;
                    if (codeSequence != null) {
                        List<Row> factors = JavaConverters.seqAsJavaListConverter(codeSequence).asJava();
                        for (Row factor : factors) {
                            // 处理空值
                            double xjhl = factor.isNullAt(1) ? 0.0 : factor.getDouble(1);
                            double pgj = factor.isNullAt(2) ? 0.0 : factor.getDouble(2);
                            double pgbl = factor.isNullAt(3) ? 0.0 : factor.getDouble(3);
                            double sgbl = factor.isNullAt(4) ? 0.0 : factor.getDouble(4);

                            // 计算分母并校验
                            double denominator = 1 + pgbl + sgbl;
                            if (Math.abs(denominator) < 1e-6) {
                                throw new ArithmeticException("Zero denominator found at date: " + factor.getDate(0));
                            }

                            // 执行前复权计算公式
                            currentPrice = ((currentPrice - xjhl) + (pgj + pgbl)) / denominator;
                        }
                    }
                    return currentPrice;
                },
                DataTypes.DoubleType
        );

        // =====6.计算结果
        Dataset<Row> resultDF = aggregatedDF.select(
                // TODO 开头8个字段先用MDDate的值填充
                functions.lit("xxxYYy").alias("CrtBy"),
                functions.lit("xxxYYy").alias("CrtDate"),
                functions.lit("xxxYYY").alias("LastModfBy"),
                functions.lit("xxxYYY").alias("LastModfDate"),
                functions.lit("xxxYYY").alias("CrtBusiDate"),
                functions.lit("xxxYYY").alias("LastModfBusiDate"),
                functions.lit("xxxYYy").alias("EtlSrcTable"),
                functions.lit("xxxYYY").alias("PeriodId"),

                aggregatedDF.col("HTSCSecurityID"),
                aggregatedDF.col("MDDate"),
                aggregatedDF.col("MDTime"),
                aggregatedDF.col("NumTrades"),
                aggregatedDF.col("TotalVolumeTrade"),
                aggregatedDF.col("TotalValueTrade"),
                aggregatedDF.col("OpenPx"),
                aggregatedDF.col("HighPx"),
                aggregatedDF.col("LowPx"),
                aggregatedDF.col("ClosePx"),

                functions.callUDF("calculate_adjustment",
                        aggregatedDF.col("OpenPx"),
                        aggregatedDF.col("code_sequence")
                ).alias("BadJustOpeningPrice"),

                functions.callUDF("calculate_adjustment",
                        aggregatedDF.col("ClosePx"),
                        aggregatedDF.col("code_sequence")
                ).alias("BadJustClosingPrice"),

                functions.callUDF("calculate_adjustment",
                        aggregatedDF.col("HighPx"),
                        aggregatedDF.col("code_sequence")
                ).alias("BadJustHighsetPrice"),

                functions.callUDF("calculate_adjustment",
                        aggregatedDF.col("LowPx"),
                        aggregatedDF.col("code_sequence")
                ).alias("BadJustLowsetPrice"),

                // TODO 没有如下两个字段 PreClosePrice 昨收价 和 BadJustPreClosingPrice 前复权昨收价
                // 因此，先给常量值 或者先用 CLosePx 收盘价 填充
                functions.lit(1.3).alias("PreClosePrice"),

                functions.callUDF("calculate_adjustment",
                        aggregatedDF.col("ClosePx"),
                        aggregatedDF.col("code_sequence")
                ).alias("BadJustPreClosingPrice"),

                aggregatedDF.col("SecurityType"),
                aggregatedDF.col("SecuritySubType"),
                aggregatedDF.col("SecurityID"),
                aggregatedDF.col("SecurityIDSource"),
                aggregatedDF.col("`Symbol`"),
                aggregatedDF.col("AfterHoursNumTrades"),
                aggregatedDF.col("AfterHoursTotalVolumeTrade"),
                aggregatedDF.col("AfterHoursTotalValueTrade"),
                aggregatedDF.col("MacroDetail")
        );

        // =====7.结果写入到 htsc_ods_ins.SR_QTT_XSHE_Stock_KLine1Day_PREBADJUST_DI_Test 表
        resultDF
                .write()
                .mode(SaveMode.Overwrite)
                .option("compression", "GzIP")
                .parquet("hdfs://nameservice1/user/hive/warehouse/htsc_ods_ins.db/sr_qtt_xshe_stock_kline1day-prebadjust_di_test/tradingday=20230301/");

        // 打印示例结果
        System.out.println("========== 计算结果样例 ==========");
        resultDF.show(5, false);

        // 停止Spark
        spark.close();
    }
}