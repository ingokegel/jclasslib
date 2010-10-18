<?xml version='1.0'?>
<xsl:stylesheet xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
  version='1.0'>


  <!-- Standard DocBook -->
  <xsl:import href="docbook/stylesheets/docbook/html/docbook.xsl"/>

  <!-- CSS -->
  <xsl:param name="html.stylesheet">doc.css</xsl:param>

  <!-- Custom head -->
  <xsl:template name="user.head.content">
    <xsl:param name="node" select="."/>
    <meta content="a library for viewing and manipulating java bytecode" name="description"/>
    <meta content="jclasslib, bytecode, performance, profiling, instrumentation, profiler, java, classes" name="keywords"/>
  </xsl:template>

  <!-- Custom header in body -->
  <xsl:template name="user.header.content">
    <xsl:param name="node" select="."/>
    <table width="100%" border="0" cellspacing="0" cellpadding="0">
      <tr>
        <td align="left">
          <img alt="jclasslib logo" height="97" width="287" src="images/jclasslib.jpg"/>
        </td>
        <td align="right">
          <a href="http://www.ej-technologies.com">
            <img alt="ej-technologies logo" height="80" width="290" src="images/ej-technologies.gif" border="0"/>
          </a>
        </td>
      </tr>
    </table>
  </xsl:template>

  <!-- Application -->
  <xsl:template match="application">
    <xsl:call-template name="inline.italicseq"/>
  </xsl:template>

  <!-- Menu -->
  <xsl:template match="guimenu">
    <xsl:call-template name="inline.italicseq"/>
  </xsl:template>

  <!-- Menu item -->
  <xsl:template match="guimenuitem">
    <xsl:call-template name="inline.italicseq"/>
  </xsl:template>

  <!-- Button -->
  <xsl:template match="guibutton">
    <b>
      <xsl:text>[</xsl:text>
    </b>
    <xsl:call-template name="inline.boldseq"/>
    <b>
      <xsl:text>]</xsl:text>
    </b>
  </xsl:template>

  <!-- Title -->
  <xsl:attribute-set name="article.titlepage.recto.style">
    <xsl:attribute name="style">margin-left:18px</xsl:attribute>
  </xsl:attribute-set>

</xsl:stylesheet>

