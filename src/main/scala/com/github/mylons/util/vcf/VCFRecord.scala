package com.github.mylons.util.vcf

import collection.mutable.HashMap

/**
 * Author: Mike Lyons
 * Date: 1/24/13
 * Time: 2:40 PM
 * Description: 
 */

case class Filter( pass: Boolean, names: List[String] )
case class Info( fields: HashMap[String, Any] )
class VCFRecord(
                 val chrom: String,
                 val pos: Int,
                 val id: String,
                 val ref: String,
                 val alt: String,
                 val qual: String,
                 val filter: Filter,
                 val info: Info )
{

  override def toString = "%s\t%d\t%s\t%s\t%s\t%s\t%s\t%s".format(
    chrom,
    pos,
    id,
    ref,
    alt,
    qual,
    filter.names.mkString(";"),
    info.fields.mkString(";").replace(" -> ", "=")
  )

}

object VCFRecord {
  def newFilter( filterString: String ) = {
    if (filterString.contains("PASS"))
      new Filter(true, List("PASS"))
    else
      new Filter(false, filterString.split(";").toList)
  }

  def newInfo( info: String ) = {
    new Info(infoSplitter(info))
  }
  private def infoSplitter( info: String ): HashMap[String, Any] = {
    val themap = new HashMap[String, Any]()
    val tokens = info.split(";")
    for (token <- tokens) {
      if (token.contains("=")) {
        val tmp = token.split("=")
        themap.put( tmp(0), tmp(1) )
      } else {
        themap.put(token, None)
      }
    }
    return themap
  }

}