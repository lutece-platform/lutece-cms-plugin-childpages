<?xml version="1.0"?>
<xsl:stylesheet version="1.0"
	xmlns:xsl="http://www.w3.org/1999/XSL/Transform">

	<xsl:param name="site-path" select="site-path" />

	<xsl:template match="portlet">
	
	<xsl:variable name="device_class">
	<xsl:choose>
		<xsl:when test="string(display-on-small-device)='0'">hidden-phone</xsl:when>
		<xsl:otherwise></xsl:otherwise>
	</xsl:choose>
	</xsl:variable>
		<div class="portlet  {$device_class} append-bottom -lutece-border-radius">
			<h2>
				<xsl:if test="not(string(display-portlet-title)='1')">
					<xsl:value-of disable-output-escaping="yes" select="portlet-name" />
				</xsl:if>
				<xsl:if test="not(string(display-portlet-title)='0')">		
					&#160;
				</xsl:if>				
			</h2>		
	
			<xsl:apply-templates select="child-pages-portlet" />
		
	</div>
	<br />
</xsl:template>

<xsl:template match="child-pages-portlet">
	<ul>
	    <xsl:apply-templates select="child-page" />
	</ul>
</xsl:template>

<xsl:template match="child-page">
	<li>
		<a href="{$site-path}?page_id={child-page-id}" target="_top">
			<b><xsl:value-of select="child-page-name" /></b>
		</a><br/>
		<xsl:value-of select="child-page-description" /><br/>
	</li>
</xsl:template>

</xsl:stylesheet>
