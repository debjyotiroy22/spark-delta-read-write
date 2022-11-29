package com.databricks

import io.delta._
import org.apache.spark.sql.functions._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.util

import java.io.{Closeable, File}
import java.lang
import scala.io.Source._

object DeltaWriteTest {
  private val NPARAMS = 1
  private var dfsDirPath: String = ""

  private def printUsage(): Unit = {
    val usage =
      """Delta Write Test
        |dfsDir - (string) DFS directory for delta output tests""".stripMargin
    println(usage)
  }

  private def parseArgs(args: Array[String]): Unit = {
    if (args.length != NPARAMS) {
      printUsage()
      System.exit(1)
    }

    var i = 0
    dfsDirPath = args(i)
  }


  def main(args: Array[String]): Unit = {
    parseArgs(args)

    println("Creating SparkSession")
    val spark = SparkSession
      .builder
      .appName("DFS Read Write Test")
      .getOrCreate()

    println("Creating DataSet with random data")
    val data: Dataset[lang.Long] = spark.range(0, 5)

    val dfsFilename = s"$dfsDirPath/delta"
    println("Writing local file to DFS")
    data.write.format("delta").save(dfsFilename)

    spark.stop()

  }
}
