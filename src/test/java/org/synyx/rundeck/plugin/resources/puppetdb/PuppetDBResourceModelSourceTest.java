package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import org.junit.Test;

import java.util.Properties;

import static org.mockito.Mockito.mock;


/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSourceTest {

    @Test(expected = ResourceModelSourceException.class)
    public void testGetNodes() throws ResourceModelSourceException {
        Properties configuration = mock(Properties.class);

        new PuppetDBResourceModelSource(configuration).getNodes();
    }
}