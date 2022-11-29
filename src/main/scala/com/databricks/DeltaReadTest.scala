package com.databricks

import io.delta.tables._
import org.apache.spark.sql.functions._
import org.apache.hadoop.fs.{FileSystem, Path}
import org.apache.spark.sql.{Dataset, SparkSession}
import org.apache.spark.util

import java.io.{Closeable, File}
import java.lang
import scala.io.Source._

object DeltaReadTest {

  private val NPARAMS = 1
  private var dfsDirPath: String = ""

  private def printUsage(): Unit = {
    val usage =
      """Delta Write Test
        |dfsDir - (string) DFS directory for delta reading tests""".stripMargin
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

    println("Read delta tab")
    val dfsFilename = s"$dfsDirPath/delta"

    val df = spark.read.format("delta").load(dfsFilename)
    df.show(5)

    spark.stop()

  }
}
