package com.github.mylons.bed

import collection.mutable
import collection.mutable.Map
import java.lang.String

/**
 * Author: Mike Lyons
 * Date: 11/1/12
 * Time: 2:37 PM
 * Description: 
 */
class BED( val name: String, val start: Int, val stop: Int, val namedFields: Map[String, Any] ) {

  /*def this( coverageBedLine: String ) = {
    val tokens = coverageBedLine.split('\t')
    this( tokens(0).toString, tokens(1).toInt, tokens(2).toInt, Map( "targetName" -> tokens(3).toString, "targetRelativeOffset" -> tokens(4).toInt, "depth" -> tokens(5).toInt ) )
  }*/

  val queryString: String = name + ":" + start + "-" + stop
  val hashString: String = {
    val targetName = if (namedFields.contains("targetName")) {
      namedFields("targetName")
    } else {
      name
    }
    targetName + ":" + start + "-" + stop
  }
  def covered( position: Int ) = start >= position && position < stop

  def depth: Int = {
    if (namedFields.contains("depth")) {
      return namedFields("depth").asInstanceOf[Int]
    } else {
      -1
    }
  }


  override def toString = queryString

}
