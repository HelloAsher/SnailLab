package Spark.sparksql

import org.apache.spark.sql.SparkSession
import utils.JDBCUtils

/**
  * Created by Asher on 2016/12/8.
  */
object IISLogAnalysis {

  case class IISLog(date: String, time: String, s_ip: String,
                    cs_method: String, sc_uri_stem: String,
                    sc_uri_query: String, s_port: String,
                    sc_username: String, c_ip: String, ua: String,
                    sc_status: String, sc_substatus: String,
                    sc_win32_status: String, time_taken: String)

  def main(args: Array[String]): Unit = {
    val sparkSession = SparkSession.builder()
      .appName("IISLogAnalysis-with-spark-sql")
      .master("local[4]")
      .config("spark.sql.warehouse.dir", "spark-warehouse")
      .getOrCreate()
    //iphoneUserAnalysis(sparkSession)
    iphoneUserAnalysisDS(sparkSession)
  }

  private def iphoneUserAnalysisDS(sparkSession: SparkSession): Unit = {
    import sparkSession.implicits._
    val IISLogDS = sparkSession.read.textFile("src/main/resources/u_ex16030616.log").filter(item => item.size > 10).map(_.split(" "))
    IISLogDS.show(10)
    val iPhoneIISLogDS = IISLogDS.filter(_(9).contains("iPhone"))
    val keyvalueDS = iPhoneIISLogDS.map(_(8))
    val reducedKeyValueRDD = keyvalueDS.groupByKey(item => item).count()

    reducedKeyValueRDD.foreachPartition(partition => {
      val JDBCConnection = JDBCUtils.getConnection("localhost", 3306, "iisloganalysis", "root", "root")
      val st = JDBCConnection.createStatement()
      partition.foreach(record => {
        val sql = s"insert into iphone_user(user_ip, times) values('${record._1}', '${record._2}')"
        val flag = st.execute(sql)
      })
      JDBCUtils.release(JDBCConnection, st, null)
    })

  }

  private def iphoneUserAnalysis(sparkSession: SparkSession) = {
    import sparkSession.implicits._

    val logDF = sparkSession.sparkContext
      .textFile("hdfs://MNode01:9000/file-house/spark-sql/iislog/*.log")
      //.textFile("src/main/resources/u_ex16030616.log")
      .filter(s => s.length > 100)
      .map(_.split(" "))
      .map(attributes => IISLog(attributes(0), attributes(1), attributes(2), attributes(3),
        attributes(4), attributes(5), attributes(6), attributes(7),
        attributes(8), attributes(9), attributes(10), attributes(11),
        attributes(12), attributes(13)))
      .toDF()
    logDF.createOrReplaceTempView("IISLog")
    val user_of_iPhone = sparkSession.sql("select c_ip, count(c_ip) as times from IISLog where ua like '%iPhone%' group by c_ip")

    //user_of_iPhone.show()
    val user_of_iPhone_rdd = user_of_iPhone.rdd

    user_of_iPhone_rdd.foreachPartition(partition => {
      val JDBCConnection = JDBCUtils.getConnection("MNode01", 3306, "iisloganalysis", "root", "123456")
      val st = JDBCConnection.createStatement()
      partition.foreach(record => {
        val sql = s"insert into iphone_user(user_ip, times) values('${record.get(0)}', '${record.get(1)}')"
        val flag = st.execute(sql)
      })
      JDBCUtils.release(JDBCConnection, st, null)
    })
  }
}
