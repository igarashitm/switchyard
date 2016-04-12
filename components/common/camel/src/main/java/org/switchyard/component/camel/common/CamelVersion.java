package org.switchyard.component.camel.common;

import java.util.Comparator;

public class CamelVersion implements Comparator<String> {
    private static final String CAMEL_PACKAGE = "org.apache.camel";
    private static String _camelVersion = null;

    public static String getCamelVersion() {
    	if (_camelVersion == null) {
            Package pkg = Package.getPackage(CAMEL_PACKAGE);
            if (pkg != null) {
            	_camelVersion = pkg.getImplementationVersion();
            	if (_camelVersion == null) {
            		_camelVersion = pkg.getSpecificationVersion();
            	}
            }    		
    	} else {
    		return _camelVersion;
    	}
    	return _camelVersion;
    }

	@Override
    public int compare(String versionOne, String versionTwo) {
		String[] versionOneSplit = versionOne.split("\\.");
		String[] versionTwoSplit = versionTwo.split("\\.");
		
		int length = (versionOneSplit.length > versionTwoSplit.length) ?
				versionOneSplit.length : versionTwoSplit.length;
				
		for (int i=0; i<length; i++) {
			int oneSubversion = 0;
			int twoSubversion = 0;
			
			if (i < versionOneSplit.length) {
				try {
				    oneSubversion = Integer.parseInt(versionOneSplit[i]);
				} catch (Exception nfe) {
					oneSubversion = -1;
				}
			}
			if (i < versionTwoSplit.length) {
				try {
				    twoSubversion = Integer.parseInt(versionTwoSplit[i]);
				} catch (Exception nfe) {
					twoSubversion = -1;
				}
			}
			if (oneSubversion > twoSubversion) {
				return 1;
			} else if (twoSubversion > oneSubversion) {
				return -1;
			}
		}
	    return 0;
    }
}
