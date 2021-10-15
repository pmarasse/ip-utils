<?xml version="1.0" encoding="UTF-8"?>
<!-- edited with XMLSPY v2004 rel. 3 U (http://www.xmlspy.com) by Philippe MARASSE (private) -->
<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform" xmlns:fo="http://www.w3.org/1999/XSL/Format">
	<xsl:output method="text" omit-xml-declaration="yes" indent="no"/>
	<xsl:template match="/">JaCoCo summary&#10;<xsl:apply-templates select="*"/>
	</xsl:template>
	<xsl:template match="/report/counter">
		<xsl:variable name="missed">
			<xsl:value-of select="@missed"/>
		</xsl:variable>
		<xsl:variable name="covered">
			<xsl:value-of select="@covered"/>
		</xsl:variable>Type <xsl:value-of select="@type"/> coverage: <xsl:value-of select="$covered div ($covered + $missed) * 100.0"/> % &#10;</xsl:template>
</xsl:stylesheet>
