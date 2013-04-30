package com.github.mylons.read.analysis


/**
 * Author: Mike Lyons
 * Date: 12/7/12
 * Time: 8:21 AM
 * Description: 
 */

import java.io.File
import net.sf.samtools.{SAMRecord, SAMFileReader, AlignmentBlock}
import com.github.mylons.bed.{BED, BEDBuilder}
import scala.collection.mutable.ListBuffer

class AlignedQV( val sam: SAMFileReader, val regionBedFile: String ) {

  val beds = new BEDBuilder(regionBedFile).fromExonBed

  var totalTargetLength = 0  //denominator
  var totalReads = 0
  var totalBases = 0
  var totalQV = new ListBuffer[Double]() //numerator

  def avgerageQV = (totalQV.sum.toDouble) / totalQV.length.toDouble
  def geoMeanQV = {
    val logG = totalQV.sum / 1/totalQV.length.toDouble
    println("logG=%.2f".format(logG))
    math.pow( 10, logG )
  }
  def processSam ={
    for (bed <- beds) {
      totalTargetLength += ( (bed.stop - bed.start) - 1 )
      val itr = sam.query(bed.name, bed.start, bed.stop - 1, false)
      while (itr.hasNext) {
        sumQV(itr.next())
      }
      itr.close()
    }
  }

  private def sumQV( s: SAMRecord) = {
    val quals = s.getBaseQualityString
    var totalLength = 0
    val itr  = s.getAlignmentBlocks.iterator()
    //println("sam=%s".format(s.getReadName))
    while (itr.hasNext) {
      val alignmentBlock = itr.next()
      sumQuals(quals, alignmentBlock.getReadStart - 1, ((alignmentBlock.getReadStart - 1) + (alignmentBlock.getLength - 1) ) )
      //println("\tstart=%d end=%d".format(alignmentBlock.getReadStart,alignmentBlock.getReadStart+alignmentBlock.getLength))
    }
  }

  def dePhred( q: Double) = scala.math.pow(10.0, (  q  / -10.0 ) )

  def rePhred( p: Double ) = -10 * scala.math.log10(p)

  private def sumQuals( qual: String, start: Int, stop: Int )  = {
    for (i <- start to stop ) {
      val q = qual.charAt(i).toDouble - 33
      totalQV += q
    }
  }



}



object AlignedQV extends App {

  val samFileReader = new SAMFileReader(new File(args(0)))
  val q = new AlignedQV(samFileReader, args(1))
  q.processSam
  println("totalQV=" + q.totalQV.sum)
  println("totalBases=" + q.totalBases )
  println("totalTargetLength=" + q.totalTargetLength)
  println("regular avg: " + q.avgerageQV)
  println("geo-mean: " + q.geoMeanQV)
}