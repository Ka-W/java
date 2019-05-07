<xsl:stylesheet version="1.0" xmlns:xsl="http://www.w3.org/1999/XSL/Transform"
							  xmlns:hf="xalan://helperFunctions.HelperFunctions"
							  exclude-result-prefixes="icli">
	<xsl:output method="text" encoding="UTF-8" omit-xml-declaration="yes" indent="no"/>
	
			
	<xsl:template match="/">
		<xsl:variable name="skill"><xsl:value-of select="substring-after(//link[@rel='canonical']/@href, 'l/')"/></xsl:variable>	
		<xsl:value-of select="hf:replaceNotAllowedCharactersInJenaWithUnicode($skill)"/>
		<xsl:variable name="skillPreferredLabel"><xsl:value-of select="substring-before(//title, ' |')"/></xsl:variable>
		<xsl:text>&#x9;</xsl:text><xsl:value-of select="hf:replaceAmpersand($skillPreferredLabel)"/>
		<xsl:variable name="industry"><xsl:value-of select="substring-after(//p[@class='primary-industry'], ': ')"/></xsl:variable>		
		<xsl:text>&#x9;</xsl:text><xsl:value-of select="hf:getIndustryCode($industry)"/>
		<xsl:apply-templates select="//ul[@id='related-skills-list']/li/a"/><xsl:text>&#10;</xsl:text>		
	</xsl:template>
	
	<xsl:template match="//ul[@id='related-skills-list']/li/a">
		<xsl:variable name="skill"><xsl:value-of select="substring-before(substring-after(./@href, 'l/'), '?')"/></xsl:variable>		
		<xsl:text>&#x9;</xsl:text><xsl:value-of select="hf:replaceNotAllowedCharactersInJenaWithUnicodeAndRedirects($skill)"/>				
	</xsl:template>
		
</xsl:stylesheet>