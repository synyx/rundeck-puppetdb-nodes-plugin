package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import org.junit.Test;

import java.util.Properties;

import static org.junit.Assert.assertNotNull;
import static org.mockito.Mockito.mock;


/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSourceFactoryTest {

    @Test
    public void testCreateResourceModelSource() throws ConfigurationException {

        Properties configuration = mock(Properties.class);

        PuppetDBResourceModelSourceFactory sut = new PuppetDBResourceModelSourceFactory();

        assertNotNull(sut.createResourceModelSource(configuration));
    }
}