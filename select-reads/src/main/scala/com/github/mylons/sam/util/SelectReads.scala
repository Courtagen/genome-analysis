package com.github.mylons.sam.util

import java.io.File
import net.sf.samtools.{SAMFileWriterFactory, SAMFileSpan, SAMFileReader}
/**
 * Author: Mike Lyons
 * Date: 3/25/13
 * Time: 2:02 PM
 * Description: 
 */
class SelectReads(val inputFile: String, val outputFile: String, val contig: String = "all" ) {

  //make iterator based on contig -- if all just spit back the entire sam/bam file
  def writeFile = {
    val reader = new SAMFileReader(new File(inputFile))

    //set iter to contig of interest
    val itr = reader.query(contig, 0, 0, false) //all reads in this contig

    //setup output file writer
    val factory = new SAMFileWriterFactory
    factory.setCreateIndex(true)
    val writer = factory.makeSAMOrBAMWriter(reader.getFileHeader, true, new File(outputFile))

    //write alignments
    while (itr.hasNext)
      writer.addAlignment(itr.next())

    //clean up file handles
    itr.close()
    reader.close()
    writer.close()
  }
}


object SelectReads extends App {
  val sr = new SelectReads(args(0), args(1), args(2))
  sr.writeFile
}
