package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import org.junit.Test;


/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSourceTest {

    @Test(expected = ResourceModelSourceException.class)
    public void testGetNodes() throws ResourceModelSourceException {

        new PuppetDBResourceModelSource().getNodes();
    }
}