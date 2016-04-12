package org.switchyard.component.camel.common;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.junit.Test;

public class CamelVersionTest {

    @Test
    public void testCamelVersion() {
    	String camelVersion = CamelVersion.getCamelVersion();
    	assertTrue(camelVersion.startsWith("2"));
    }
	
    public void testComparator() {
    	CamelVersion cv = new CamelVersion();
    	assertTrue(cv.compare("1.0", "1.1") == 1);
    	assertTrue(cv.compare("1.0.0", "1.0.1") == 1);
    	assertTrue(cv.compare("1.0.1", "1.1") == 1);
    	assertTrue(cv.compare("1.9", "1.10") == 1);
    }
    
}
