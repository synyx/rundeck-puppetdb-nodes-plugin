package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.plugins.configuration.Description;
import org.junit.Before;
import org.junit.Ignore;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;


/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSourceFactoryTest {

    private PuppetDBResourceModelSourceFactory sut;

    @Before
    public void setup() {
        sut = new PuppetDBResourceModelSourceFactory();
    }

    @Test
    @Ignore
    public void testCreateResourceModelSource() throws ConfigurationException {

        Properties configuration = mock(Properties.class);

        assertNotNull(sut.createResourceModelSource(configuration));
    }

    @Test
    @Ignore
    public void testDescribale() {

        Description description = sut.getDescription();

        assertThat(description.getTitle(), is("PuppetDB"));
        assertThat(description.getName(), is("puppetdb"));
    }
}