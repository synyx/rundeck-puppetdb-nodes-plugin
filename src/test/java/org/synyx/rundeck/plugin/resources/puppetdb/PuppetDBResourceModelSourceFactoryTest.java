package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import org.junit.Test;


/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSourceFactoryTest {

    @Test(expected = ConfigurationException.class)
    public void testCreateResourceModelSource() throws ConfigurationException {

        new PuppetDBResourceModelSourceFactory().createResourceModelSource(null);
    }
}