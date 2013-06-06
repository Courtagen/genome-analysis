package com.github.mylons.coverage

import com.github.mylons.bed.BEDBuilder
import com.github.mylons.bed.BED
import collection.mutable.{ListBuffer, Map, HashMap}
import collection.mutable
import java.io.File


import spray.json._
import DefaultJsonProtocol._
import com.github.mylons.coverage.ComputeCoverageStats

/**
 * Author: Mike Lyons
 * Date: 11/1/12
 * Time: 2:41 PM
 * Description: 
 */


case class Target( name: String, beds: List[BED] )
case class Summary()

class ComputeCoverageStats2( targetsFile: String, coverageFile: String, outputFile: String ) {

  //setup
  private val targetBeds = new BEDBuilder(targetsFile).fromExonBed

  //sets up a map of Gene -> List of beds
  val genes = targetBeds.groupBy( _.namedFields("targetName").toString )

  private val covBeds = new BEDBuilder(coverageFile).fromCoverageBed
  val c = covBeds(0).namedFields("targetRelativeOffset")


  //val coverage = covBeds.groupBy( _namedFields("targetName").toString )


}
class ComputeCoverageStats( inputFile: String, outputFile: String ) {

  /*def testJson() = {
    //https://s3.amazonaws.com/bigskydata/input-fastq/121018_MISEQ3_NUCSEEK/sample_metadata.json
    val s = Source.fromURL("https://s3.amazonaws.com/bigskydata/input-fastq/121018_MISEQ3_NUCSEEK/sample_metadata.json").mkString
    val j = parse(s)
    //println(j("experiment_name"))
    println (j \\ "experiment_name")

  }*/

  def initTarget(  ) = {
     Map("C100 depth" -> 0.0, "C20 depth" -> 0.0, "C10 depth" -> 0.0, "C1 depth" -> 0.0, "total depth" -> 0.0)
  }

  def initRatio(  ) = {
    Map("C100 ratio" -> 0.0, "C20 ratio" -> 0.0, "C10 ratio" -> 0.0, "C1 ratio" -> 0.0, "total ratio" -> 0.0)
  }
  private val empircalBed = new BEDBuilder(inputFile)
  //private val targetBed = new BEDBuilder(targetsFile)
  val targetMap = new HashMap[String, Map[String, Double] ]()
  val targetLengthMap = new HashMap[String, Double]()
  val superTargetLengthMap = new HashMap[String, ListBuffer[Double]]()
  val jsonSummary = new mutable.HashMap[String, Map[String, Map[String, Double] ] ]()
  /*
    "Target name" : {
      "1-100" : {
        "C20":0
      }
    }
   */
  /*val C20 = 0
  val C10 = 0
  val C5 = 0
  val C1 = 0*/

  private def hashStringToTarget( hashString: String ) = hashString.split(":")(0)

  private def formattedDivision( numerator: Double, divisor: Double ) = {
    "%.3f" format (numerator / divisor )
  }


  for (bed <- empircalBed.fromCoverageBed ) {

    if ( !jsonSummary.contains(bed.namedFields("targetName").toString ) ) {
      //doesn't contain the target
      jsonSummary.put(bed.namedFields("targetName").toString, new mutable.HashMap[String, Map[String, Double]]() )
    }

    //see if this target range is in the map of ranges, add if necessary
    val coordString = "%d-%d" format(bed.start, bed.stop)
    if ( !jsonSummary(bed.namedFields("targetName").toString ).contains(coordString) ) {
      jsonSummary(bed.namedFields("targetName").toString).put(coordString, initTarget())
    }
    //not sure what this is doing
    if (!superTargetLengthMap.contains( bed.namedFields("targetName").toString ) ) {
      superTargetLengthMap.put(bed.namedFields("targetName").toString, ListBuffer[Double]() )
    }
    // add target length if we haven't seen this target range before
    if (!targetLengthMap.contains(bed.hashString) ) {
      superTargetLengthMap( bed.namedFields("targetName").toString ) += (bed.stop - bed.start)
    }
    //add to individual target length map too
    if ( !targetLengthMap.contains(bed.hashString) )
      targetLengthMap.put( bed.hashString, (bed.stop - bed.start ) )

    //add to list of targets
    if (!targetMap.contains(bed.hashString))
      targetMap.put(bed.hashString, initTarget() )

    if (bed.depth >= 1 ){
      targetMap(bed.hashString)("C1 depth") += 1
      jsonSummary(bed.namedFields("targetName").toString)(coordString)("C1 depth") += 1
    }
    if (bed.depth >= 10 ){
      targetMap(bed.hashString)("C10 depth") += 1
      jsonSummary(bed.namedFields("targetName").toString)(coordString)("C10 depth") += 1
    }
    if (bed.depth >= 20 ){
      targetMap(bed.hashString)("C20 depth") += 1
      jsonSummary(bed.namedFields("targetName").toString)(coordString)("C20 depth") += 1
    }
    if (bed.depth >= 100 ){
      targetMap(bed.hashString)("C100 depth") += 1
      jsonSummary(bed.namedFields("targetName").toString)(coordString)("C100 depth") += 1
    }

    jsonSummary(bed.namedFields("targetName").toString)(coordString)("total depth") += bed.depth
    targetMap(bed.hashString)("total depth") += bed.depth
  }

  val superTargetMap = new HashMap[String, Map[String, Double] ]()
  val outputOrder = List("C100 depth", "C20 depth", "C10 depth", "C1 depth", "total depth" )
  println( outputOrder.mkString("Target\t", " ratio\t", " ratio") )
  for (target <- targetMap.keys ) {
    val tokens = target.split(":")
    val targetName = tokens(0)
    val coordString = tokens(1)
    if (!superTargetMap.contains(hashStringToTarget(target)))
      superTargetMap.put(hashStringToTarget(target), initTarget() )

    for ( key <- targetMap(target).keys ) {
      superTargetMap(hashStringToTarget(target))(key) += targetMap(target)(key)
    }

    val sb = new StringBuilder()
    sb ++= target.toString()
    for (order <- outputOrder){
      sb ++= "\t"
      sb ++= formattedDivision( targetMap(target)(order).toDouble, targetLengthMap(target).toDouble )
      jsonSummary(targetName)(coordString).put(order.replace("depth", "ratio"), (targetMap(target)(order).toDouble / targetLengthMap(target).toDouble ) )
    }
    println(sb.mkString)
  }

  val totalTarget = initTarget()
  var totalLength = 0.0
  for (bed <- superTargetLengthMap ) {
    val targetLen = superTargetLengthMap(bed._1).sum
    totalLength += targetLen
    val sb = new StringBuilder()
    sb ++= bed._1
    jsonSummary(bed._1).put("super", initRatio())
    for (order <- outputOrder) {
      sb ++= "\t"
      sb ++= formattedDivision( superTargetMap(bed._1)(order).toDouble, targetLen.toDouble )
      totalTarget(order) += superTargetMap(bed._1)(order)
      jsonSummary(bed._1)("super")(order.replace("depth", "ratio")) += (superTargetMap(bed._1)(order).toDouble / targetLen.toDouble)

    }
    println(sb.mkString)
  }

  jsonSummary.put("combined", Map("overall" -> initRatio() ) )
  val sb = new StringBuilder()
  sb ++= "Total"
  for (order <- outputOrder) {
    sb ++= "\t"
    sb ++= formattedDivision( totalTarget(order).toDouble, totalLength.toDouble )
    jsonSummary("combined")("overall")(order.replace("depth", "ratio")) = (totalTarget(order).toDouble / totalLength.toDouble)
  }
  println(sb.mkString)
  //println(generate(jsonSummary, new File(outputFile) ) )
  //println(jsonSummary.mkString)
  //println(jsonSummary.toJson.prettyPrint)
}

object ComputeCoverageStats extends App {
  //args
  val c = new ComputeCoverageStats(args(0), args(1))
}
