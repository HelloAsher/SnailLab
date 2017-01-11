package Spark.logsessionanalysis

import java.util.UUID

import org.apache.spark.sql.types.{StringType, StructField, StructType}
import org.apache.spark.sql.{DataFrame, Dataset, Row, SparkSession}
import utils.TimeUtils


/**
  * Created by Asher on 2016/12/20.
  * 第一步：将log封装成为DS
  */
object AnalysisMain {

  case class IISLog(date: String, time: String, s_ip: String,
                    cs_method: String, sc_uri_stem: String,
                    sc_uri_query: String, s_port: String,
                    sc_username: String, c_ip: String, ua: String,
                    sc_status: String, sc_substatus: String,
                    sc_win32_status: String, time_taken: String)

  case class WebLogBean(IP_addr: String, time: String, method: String, request_URL: String,
                        request_port: String, response_code: String, response_time_take: String,
                        browser: String, sessionID: String)

  case class Dic(sessionID: String, IP_addr: String, startTime: String, endTime: String)


  def checkArgs(args: Array[String]) = {
    if (args.length < 4) {
      System.err.println("参数错误！")
      System.exit(1)
    }
  }

  def parseLog(spark: SparkSession, url: String): Dataset[WebLogBean] = {
    import spark.implicits._
    val webLogOrigin = spark.sparkContext.textFile(url)

    val webLog = webLogOrigin.map(line => {
      val line_list = line.split(" ")
      Row.fromTuple(line_list(8), line_list(0) + " " + line_list(1), line_list(3), line_list(4),
        line_list(6), line_list(10), line_list(13), line_list(9), null)
    })
    val schema = StructType(Array(StructField("IP_addr", StringType), StructField("time", StringType),
      StructField("method", StringType), StructField("request_URL", StringType),
      StructField("request_port", StringType), StructField("response_code", StringType),
      StructField("response_time_take", StringType), StructField("browser", StringType),
      StructField("sessionID", StringType)))
    val webLogDS = spark.createDataFrame(webLog, schema).as[WebLogBean]

    return webLogDS
  }

  def parseLog2(spark: SparkSession, url: String): Dataset[IISLog] = {
    import spark.implicits._
    val IISLogDS = spark.read.option("header", "true").option("inferSchema","true").textFile(url).as[IISLog]
    return IISLogDS
  }

  def fillSessionID(webLog: Dataset[WebLogBean], sparkSession: SparkSession): Dataset[List[WebLogBean]] = {
    import sparkSession.implicits._
    val ss = webLog.groupByKey(_.IP_addr).mapGroups((x: String, it: Iterator[WebLogBean]) => {
      var startTime = "1991-01-01 00:00:00"
      import scala.collection.mutable.Set
      val liskov: Set[WebLogBean] = Set()
      var sessionID = UUID.randomUUID().toString
      val aa = it.toArray
      println(" aa.size::::::" + aa.size)

      for (a <- aa) {
        println("nihao ::::" + a)
        if (startTime.equals("1991-01-01 00:00:00")) {
          //用户第一次访问
          startTime = a.time
          val bb = WebLogBean(a.IP_addr, a.time, a.method, a.request_URL, a.request_port, a.response_code, a.response_time_take, a.browser, sessionID)
          liskov.add(bb)
        } else {
          val endTime = TimeUtils.timeAddMinutes(startTime, 30, "yyyy-MM-dd HH:mm:ss")
          if (TimeUtils.timeCompareBefore(a.time, endTime, "yyyy-MM-dd HH:mm:ss")) {
            startTime = a.time
            val bb = WebLogBean(a.IP_addr, a.time, a.method, a.request_URL, a.request_port, a.response_code, a.response_time_take, a.browser, sessionID)
            liskov.add(bb)
          } else {
            startTime = a.time
            sessionID = UUID.randomUUID().toString
            val bb = WebLogBean(a.IP_addr, a.time, a.method, a.request_URL, a.request_port, a.response_code, a.response_time_take, a.browser, sessionID)
            liskov.add(bb)
          }
        }
      }
      liskov.toList
    })
    ss
  }

  def getPV(webLog: Dataset[WebLogBean], date: String, sparkSession: SparkSession): DataFrame = {
    import sparkSession.implicits._
    val ss = webLog.filter(x => {
      if (x.time.split(" ")(0) == date) {
        true
      } else {
        false
      }
    })
    return ss.groupBy("request_URL").count().sort($"count".desc)
  }

  def getUV(webLog: Dataset[WebLogBean], sparkSession: SparkSession): DataFrame = {
    import sparkSession.implicits._
    val ss = webLog.map(x => {
      WebLogBean(x.IP_addr, x.time.split(" ")(0).trim, x.method, x.request_URL, x.request_port, x.response_code, x.response_time_take, x.browser, null)
    })
    val sss = ss.groupBy("time", "IP_addr").count().groupBy("time").count().sort($"time")

    return sss
  }

  def main(args: Array[String]): Unit = {

    //checkArgs(args)

    //    val master = args(0)
    //    val appName = args(1)
    //    val url = args(2)
    //    val warehouse = args(3)

    val master = "local[4]"
    val appName = "sessionAnalysis"
    val url = "D:\\scal\\test\\*.log"
    val warehouse = "spark-warehouse"

    val spark = SparkSession.builder()
      .appName(appName)
      .master(master)
      .config("spark.sql.warehouse.dir", warehouse)
      .getOrCreate()

//    val webLog = parseLog(spark, url)
//        val PV = getPV(webLog, "2012-12-29", spark)
//        val UV = getUV(webLog, spark)
//        PV.show(100)
//        UV.show(100)
//    val sss = fillSessionID(webLog, spark)
//    sss.show(10)
    val IISLog = parseLog2(spark, url)
    IISLog.show(10)


  }

}
