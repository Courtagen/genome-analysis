package com.github.mylons.util.vcf

import collection.mutable.ListBuffer

/**
 * Author: Mike Lyons
 * Date: 1/24/13
 * Time: 2:40 PM
 * Description: 
 */
class VCFHeader {


  private val lines = new ListBuffer[String]()
  def addToHeader( s: String ) = {
    lines += s
  }


}

