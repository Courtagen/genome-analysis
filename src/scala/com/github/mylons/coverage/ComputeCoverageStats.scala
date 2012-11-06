package com.github.mylons.coverage

import com.github.mylons.bed.{BEDBuilder}
import collection.mutable.Map
import collection.mutable.HashMap

/**
 * Author: Mike Lyons
 * Date: 11/1/12
 * Time: 2:41 PM
 * Description: 
 */

class ComputeCoverageStats( inputFile: String ) {

  private val empircalBed = new BEDBuilder(inputFile)
  //private val targetBed = new BEDBuilder(targetsFile)
  val targetMap = new HashMap[String, Map[String, Int] ]()
  val targetLengthMap = new HashMap[String, Int]()
  val superTargetLengthMap = new HashMap[String, Set[Int]]()
  /*val C20 = 0
  val C10 = 0
  val C5 = 0
  val C1 = 0*/

  private def hashStringToTarget( hashString: String ) = hashString.split(":")(0)

  private def formattedDivision( numerator: Double, divisor: Double ) = {
    "%.2f" format (numerator / divisor )
  }

  for (bed <- empircalBed.beds ) {
    if (!superTargetLengthMap.contains( bed.namedFields("targetName").toString ) ) {
      superTargetLengthMap.put(bed.namedFields("targetName").toString, Set[Int]() )
    }
    superTargetLengthMap( bed.namedFields("targetName").toString ) += (bed.stop - bed.start)

    if ( !targetLengthMap.contains(bed.hashString) )
      targetLengthMap.put( bed.hashString, (bed.stop - bed.start ) )
    if (!targetMap.contains(bed.hashString))
      targetMap.put(bed.hashString, Map("C20" -> 0, "C10" -> 0, "C5" -> 0, "C1" -> 0, "total" -> 0) )
    if (bed.depth >= 1 )
      targetMap(bed.hashString)("C1") += 1
    if (bed.depth >= 5 )
      targetMap(bed.hashString)("C5") += 1
    if (bed.depth >= 10 )
      targetMap(bed.hashString)("C10") += 1
    if (bed.depth >= 20 )
      targetMap(bed.hashString)("C20") += 1

    targetMap(bed.hashString)("total") += bed.depth
  }

  val superTargetMap = new HashMap[String, Map[String, Int] ]()
  val outputOrder = List("C20", "C10", "C5", "C1", "total" )
  println("Target\tC20 ratio\tC10 ratio\tC5 ratio\tC1 ratio\ttotal ratio")
  for (target <- targetMap.keys ) {

    if (!superTargetMap.contains(hashStringToTarget(target)))
      superTargetMap.put(hashStringToTarget(target), Map("C20" -> 0, "C10" -> 0, "C5" -> 0, "C1" -> 0, "total" -> 0 ) )

    for ( key <- targetMap(target).keys )
      superTargetMap(hashStringToTarget(target))(key) += targetMap(target)(key)

    val sb = new StringBuilder()
    sb ++= target.toString()
    for (order <- outputOrder){
      sb ++= "\t"
      sb ++= formattedDivision( targetMap(target)(order).toDouble, targetLengthMap(target).toDouble )
    }
    println(sb.mkString)
  }

  val totalTarget = Map("C20" -> 0, "C10" -> 0, "C5" -> 0, "C1" -> 0, "total" -> 0 )
  var totalLength = 0
  for (bed <- superTargetLengthMap ) {
    val targetLen = superTargetLengthMap(bed._1).sum
    totalLength += targetLen
    val sb = new StringBuilder()
    sb ++= bed._1
    for (order <- outputOrder) {
      sb ++= "\t"
      sb ++= formattedDivision( superTargetMap(bed._1)(order).toDouble, targetLen.toDouble )
      totalTarget(order) += superTargetMap(bed._1)(order)
    }
    println(sb.mkString)
  }

  val sb = new StringBuilder()
  sb ++= "Total"
  for (order <- outputOrder) {
    sb ++= "\t"
    sb ++= formattedDivision( totalTarget(order).toDouble, totalLength.toDouble )
  }
  println(sb.mkString)
}

object ComputeCoverageStats extends App {
  //args
  val c = new ComputeCoverageStats(args(0))
}
