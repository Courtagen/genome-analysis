package com.github.mylons.read.analysis

import java.io.File
import net.sf.samtools.SAMFileReader

/**
 * Author: Mike Lyons
 * Date: 1/8/13
 * Time: 10:59 AM
 * Description: 
 */
class BamInfo( val samFile: SAMFileReader ) {

  def displayContigsFromHeader = {
    val dict = samFile.getFileHeader.getSequenceDictionary
    val seqs = dict.getSequences
    val itr = seqs.iterator()
    while (itr.hasNext){
      val seq = itr.next()
      println("seq index=%d name=%s".format(seq.getSequenceIndex,seq.getSequenceName) )
    }
  }
}

object BamInfo extends App {
  val samFileReader = new SAMFileReader(new File(args(0)))
  val b = new BamInfo(samFileReader)
  b.displayContigsFromHeader
}
