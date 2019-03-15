/**
 * @version 12
 * @package sun.text.resources.cldr.ext
 * @package sun.text.resources.ext
 * @package sun.util.resources.cldr.ext
 * @package sun.util.resources.cldr.provider
 * @package sun.util.resources.ext
 * @package sun.util.resources.provider
 */
module jdk.localedata {
	/**
	 * @modifiers mandated
	 */
	requires java.base;
	provides sun.util.locale.provider.LocaleDataMetaInfo with 
		sun.util.resources.cldr.provider.CLDRLocaleDataMetaInfo,
		sun.util.resources.provider.NonBaseLocaleDataMetaInfo;
	provides sun.util.resources.LocaleData$CommonResourceBundleProvider with 
		sun.util.resources.provider.LocaleDataProvider;
	provides sun.util.resources.LocaleData$SupplementaryResourceBundleProvider with 
		sun.util.resources.provider.SupplementaryLocaleDataProvider;
}