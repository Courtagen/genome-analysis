package com.github.mylons.bed

/**
 * Author: Mike Lyons
 * Date: 11/1/12
 * Time: 2:44 PM
 * Description: 
 */

import samtools.tabix.TabixReader

import scala.collection.mutable.ListBuffer
import java.lang.String
import collection.mutable.Map

class BEDBuilder( bedFile: String ) {


  private val t = new TabixReader(bedFile)

  private def parseBed( f: String => BED): List[BED] = {
    var line = ""
    val beds = new ListBuffer[BED]()

    line = t.readLine()
    while ( line != null ){
      if (! line.contains("#")) //skip header
        beds += f(line)
      line = t.readLine()
    }
    beds.toList
  }

  def fromCoverageBed =
    parseBed( BEDBuilder.bedFromCoverageBedLine )


  def fromExonBed =
    parseBed( BEDBuilder.bedFromExonBedLine )



}

object BEDBuilder {

  def bedFromCoverageBedLine( coverageBedLine: String ): BED = {
    val tokens = coverageBedLine.split('\t')
    new BED(tokens(0).toString, tokens(1).toInt, tokens(2).toInt,
      Map(
      "targetName" -> tokens(3).toString,
      "targetRelativeOffset" -> tokens(4).toInt,
      "depth" -> tokens(5).toInt )
    )
  }
  def bedFromExonBedLine( coverageBedLine: String ): BED = {
    val tokens = coverageBedLine.split('\t')
    new BED( tokens(0).toString, tokens(1).toInt, tokens(2).toInt, Map( "targetName" -> tokens(3).toString ) )
  }
}

