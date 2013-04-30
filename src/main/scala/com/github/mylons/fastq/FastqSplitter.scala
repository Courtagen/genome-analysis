package com.github.mylons.fastq


import _root_.net.sf.picard.io.IoUtil
import java.io.File
import net.sf.picard.fastq.{FastqWriter, FastqRecord, FastqReader, FastqWriterFactory}
import com.typesafe.scalalogging.log4j._
import collection.mutable.ListBuffer

/**
 * Author: Mike Lyons
 * Date: 4/3/13
 * Time: 8:52 AM
 * Description: 
 */


class FastqSplitter( fastqFile: String, numberOfReadsPerFile: Int ) /*with Logging */{

/*
  logger.info("fastqFile: " + fastqFile )
  private val fastqReader = {
    //TODO add exception handling here
    val f = new java.io.File(fastqFile)
    new FastqReader(f)
  }


  private val filePrefix = {
    val tokens = fastqReader.getFile.getName.split('.')
    if (tokens.last == "fq" || tokens.last == "fastq" ){
      SuperPipeConfig.conf.SUPER_PIPE_FASTQ_SPLIT_DIR + "/" + tokens(tokens.length - 2) + "-"
    } else {
      SuperPipeConfig.conf.SUPER_PIPE_FASTQ_SPLIT_DIR + "/" + tokens(tokens.length - 3) + "-"
    }
  }

  private val writerFactory = new FastqWriterFactory
  //TODO validate that Async io is faster
  //TODO check if this is the cause of SGE errors I'm seeing
  //writerFactory.setUseAsyncIo(true) //should be faster this way


  def split(): ListBuffer[File] = {
    var c = 0
    var i = 0
    val itr = fastqReader.iterator()
    var ofile: FastqWriter = writerFactory.newWriter(new File("/tmp/unused.fq") )
    var ofileOpen = false
    val splitFiles = new ListBuffer[File]()

    while( itr.hasNext ) {
      i += 1
      if (i % numberOfReadsPerFile == 1) {
        c += 1
        if (ofileOpen) {
          ofile.close() //hopefully this flushes the buffer
          ofileOpen = false
        }
        val newFileName = filePrefix.toString()+"S%06d".format(c)+".fq.gz"
        logger.info("writing to: " +newFileName)
        splitFiles += ( new File(newFileName) )
        ofile = writerFactory.newWriter( splitFiles.last )
        ofileOpen = true
      }
      ofile.write(itr.next())//advances iterator, and writes asynchronously
    }
    //close if still open
    if (ofileOpen) ofile.close(); ofileOpen = false
    fastqReader.close()
    return splitFiles
  }*/

}