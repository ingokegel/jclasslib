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
        <img alt="jclasslib logo" height="97" width="287" src="images/jclasslib.jpg" />
      </td>
      <td align="right">
        <a href="http://www.ej-technologies.com"><img alt="ej-technologies logo" height="65" width="330" src="images/ej-technologies.gif" border="0" /></a>
      </td>
    </tr>
  </table>
</xsl:template>

<!-- Applications are italic -->
<xsl:template match="application">
  <xsl:call-template name="inline.italicseq"/>
</xsl:template>

<!-- Title is indented -->
<xsl:attribute-set name="article.titlepage.recto.style">
  <xsl:attribute name="style">margin-left:18px</xsl:attribute>
</xsl:attribute-set>

</xsl:stylesheet>

