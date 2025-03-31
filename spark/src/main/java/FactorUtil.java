import org.apache.spark.sql.Row;
import org.apache.spark.sql.SparkSession;
import org.apache.spark.sql.types.DataTypes;
import scala.collection.JavaConverters;

import java.util.List;

/**
 * Created By 19921227 on 2025/3/31 9:59
 */
public class FactorUtil {

    public static void calculateStockAdjustment(SparkSession spark, String udfName) {
//        spark.udf().register(udfName,
//                (Double price, scala.collection.Seq<Row> codeSequence) -> {
//                    double currentPrice = price;
//                    if (codeSequence != null) {
//                        List<Row> factors = JavaConverters.seqAsJavaListConverter(codeSequence).asJava();
//                        for (Row factor : factors) {
//                            // 处理空值
//                            double xjhl = factor.isNullAt(1) ? 0.0 : factor.getDouble(1);
//                            double pgj = factor.isNullAt(2) ? 0.0 : factor.getDouble(2);
//                            double pgbl = factor.isNullAt(3) ? 0.0 : factor.getDouble(3);
//                            double sgbl = factor.isNullAt(4) ? 0.0 : factor.getDouble(4);
//
//                            // 计算分母并校验
//                            double denominator = 1 + pgbl + sgbl;
//                            if (Math.abs(denominator) < 1e-6) {
//                                throw new ArithmeticException("Zero denominator found at date: " + factor.getDate(0));
//                            }
//
//                            // 执行前复权计算公式
//                            currentPrice = ((currentPrice - xjhl) + (pgj + pgbl)) / denominator;
//                        }
//                    }
//                    return currentPrice;
//                },
//                DataTypes.DoubleType
//        );


        spark.udf().register(udfName,
                (Float price, scala.collection.Seq<Row> codeSequence) -> {
                    float currentPrice = price;
                    if (codeSequence != null) {
                        List<Row> factors = JavaConverters.seqAsJavaListConverter(codeSequence).asJava();
                        for (Row factor : factors) {
                            // 处理空值
                            float xjhl = factor.isNullAt(1) ? 0.0f : (float) factor.getDouble(1);
                            float pgj = factor.isNullAt(2) ? 0.0f : (float) factor.getDouble(2);
                            float pgbl = factor.isNullAt(3) ? 0.0f : (float) factor.getDouble(3);
                            float sgbl = factor.isNullAt(4) ? 0.0f : (float) factor.getDouble(4);

                            // 计算分母并校验
                            float denominator = 1 + pgbl + sgbl;
                            if (Math.abs(denominator) < 1e-6f) {
                                throw new ArithmeticException("Zero denominator found at date: " + factor.getDate(0));
                            }

                            // 执行前复权计算公式
                            currentPrice = ((currentPrice - xjhl) + (pgj + pgbl)) / denominator;
                        }
                    }
                    return Math.round(currentPrice * 100f) / 100f; // 四舍五入到小数点后两位
                },
                DataTypes.FloatType
        );
    }
}
