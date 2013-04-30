package com.github.mylons.variant

import com.github.mylons.util.vcf.{VCFRecord, VCFReader}
import com.github.mylons.bed.{BED, BEDBuilder}
import collection.mutable
import collection.mutable.ListBuffer

/**
 * Author: Mike Lyons
 * Date: 1/24/13
 * Time: 2:35 PM
 * Description: 
 */
class VCFAnalysis( vcfFile: String, bedFile: String  ) {



  /* make bed map */
  val beds = new BEDBuilder(bedFile).fromExonBed
  val bedMap = new mutable.HashMap[String, ListBuffer[BED]]()

  println("making bed map")

  private def addBedToMap(hash: String, bed: BED) = {
    if (bedMap.contains(hash))
      bedMap(hash).append(bed)
    else{
      val b = new ListBuffer[BED]()
      b.append(bed)
      bedMap.put(hash, b)
    }
  }

  for (bed <- beds){
    for (i <- bed.start until bed.stop if i < bed.start + 8 && i < bed.stop){
      val hash = "%s-%d".format(bed.name, i)
      addBedToMap(hash, bed)
    }
    for (i <- bed.stop until bed.start by -1 if i > bed.stop - 8 && i > bed.start){
      val hash = "%s-%d".format(bed.name, i)
      addBedToMap(hash, bed)
    }
  }
  println("done making bed map")

  val reader = new VCFReader(vcfFile)
  val itr = reader.vcfs.iterator

  while (itr.hasNext){
    val v = itr.next
    val hash = "%s-%d".format(v.chrom, v.pos)
    if (bedMap.contains(hash))
      println("%s\n\t%s".format(v.toString, bedMap(hash)))
  }

}

object VCFAnalysis extends App {
  println(args.toList)
  val a = new VCFAnalysis(args(0), args(1))
}
