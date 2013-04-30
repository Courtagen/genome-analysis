package com.github.mylons.util.vcf


import java.io.File
import io.Source
import collection.mutable.{ListBuffer, HashMap}

/**
 * Author: Mike Lyons
 * Date: 1/24/13
 * Time: 2:36 PM
 * Description: 
 */
class VCFReader ( val vcfFile: String ) {



  private val lineList = Source.fromFile(vcfFile).getLines.toList
  private val itr = lineList.iterator

  val header = new VCFHeader
  val vcfs = new ListBuffer[VCFRecord]()
  while (itr.hasNext) {
    val line = itr.next()
    if (line.contains("#"))
      header.addToHeader(line)
    else{
      val vcf = line.split("\t")
      val infoMap = vcf(7)
      vcfs += new VCFRecord(vcf(0),vcf(1).toInt, vcf(2), vcf(3), vcf(4), vcf(5), VCFRecord.newFilter(vcf(6)), VCFRecord.newInfo( vcf(7) ) )
    }
  }

}
