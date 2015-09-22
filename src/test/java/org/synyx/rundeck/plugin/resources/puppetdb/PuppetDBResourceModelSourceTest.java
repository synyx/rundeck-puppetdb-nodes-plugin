package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClient;
import com.puppetlabs.puppetdb.javaclient.model.Fact;
import com.puppetlabs.puppetdb.javaclient.model.Node;
import com.puppetlabs.puppetdb.javaclient.query.Expression;
import org.junit.Before;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertNotNull;
import static org.junit.Assert.assertThat;
import static org.mockito.Matchers.any;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;


/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSourceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PuppetDBClient puppetDBClient;
    private PuppetDBResourceModelSource sut;

    @Before
    public void setup() {
        puppetDBClient = mock(PuppetDBClient.class);
        sut = new PuppetDBResourceModelSource(puppetDBClient, "rundeck");
    }

    @Test
    public void testGetNodes() throws ResourceModelSourceException, IOException {

        PuppetDBClient puppetDBClient = mock(PuppetDBClient.class);

        Node node = new Node();
        node.setName("mypuppetnode.local");
        List<Node> puppetNodes = new ArrayList<>();
        puppetNodes.add(node);

        when(puppetDBClient.getActiveNodes(null)).thenReturn(puppetNodes);


        List<Fact> facts = new ArrayList<>();

        Fact operatingsystem = new Fact();
        operatingsystem.setName("operatingsystem");
        operatingsystem.setValue("myOperatingSystem");
        operatingsystem.setCertname("mypuppetnode.local");
        facts.add(operatingsystem);

        when(puppetDBClient.getFacts(any(Expression.class))).thenReturn(facts);

        PuppetDBResourceModelSource sut = new PuppetDBResourceModelSource(puppetDBClient, "rundeck");

        INodeSet nodes = sut.getNodes();

        assertNotNull(nodes);

        INodeEntry myRundecknode = nodes.getNode("mypuppetnode.local");
        assertThat(myRundecknode.getOsName(), is("myOperatingSystem"));
        assertThat(myRundecknode.getUsername(), is("rundeck"));
    }

    @Test
    public void testGetZeroNodesThrowsException() throws ResourceModelSourceException {

        expectedException.expect(ResourceModelSourceException.class);
        expectedException.expectMessage("Received ZERO nodes from PuppetDB!");

        sut.getNodes();
    }

    @Test
    public void testGetZeroFactsThrowsException() throws ResourceModelSourceException, IOException {

        expectedException.expect(ResourceModelSourceException.class);
        expectedException.expectMessage("Received ZERO facts from PuppetDB!");

        Node node = new Node();
        node.setName("mypuppetnode.local");
        List<Node> puppetNodes = new ArrayList<>();
        puppetNodes.add(node);

        when(puppetDBClient.getActiveNodes(null)).thenReturn(puppetNodes);

        sut.getNodes();
    }


    @Test
    public void testPuppetDBCommunicationErrorIsHandled() throws ResourceModelSourceException, IOException {

        expectedException.expect(ResourceModelSourceException.class);
        expectedException.expectMessage("Error requesting PuppetDB!");

        when(puppetDBClient.getActiveNodes(null)).thenThrow(new IOException());

        sut.getNodes();
    }
}