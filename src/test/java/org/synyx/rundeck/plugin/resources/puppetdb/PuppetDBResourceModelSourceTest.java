package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import org.junit.Test;

import java.util.Properties;

import static org.hamcrest.CoreMatchers.is;
import static org.junit.Assert.assertThat;
import static org.mockito.Mockito.mock;


/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSourceTest {

    @Test
    public void testGetNodes() throws ResourceModelSourceException {
        Properties configuration = mock(Properties.class);

        PuppetDBResourceModelSource sut = new PuppetDBResourceModelSource(configuration);

        INodeSet nodes = sut.getNodes();

        assertThat(nodes.getNodes().size(), is(3));
    }
}