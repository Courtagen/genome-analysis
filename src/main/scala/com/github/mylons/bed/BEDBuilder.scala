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
  private var line = ""

  val beds = new ListBuffer[BED]()

  line = t.readLine()
  while ( line != null ){
    beds += BEDBuilder.bedFromCoverageBedLine( line )
    line = t.readLine()
  }

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
}

