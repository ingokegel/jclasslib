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
    <meta content="jclasslib" name="keywords"/>
</xsl:template>

<!-- Applications are italic -->
<xsl:template match="application">
  <xsl:call-template name="inline.italicseq"/>
</xsl:template>

</xsl:stylesheet>