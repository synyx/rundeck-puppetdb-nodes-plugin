package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.dtolabs.rundeck.core.common.NodeSetImpl;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClient;
import com.puppetlabs.puppetdb.javaclient.model.Fact;
import com.puppetlabs.puppetdb.javaclient.model.Node;
import com.puppetlabs.puppetdb.javaclient.query.Expression;
import org.ehcache.Cache;
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
import static org.mockito.Matchers.eq;
import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;


/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSourceTest {

    @Rule
    public ExpectedException expectedException = ExpectedException.none();

    private PuppetDBClient puppetDBClient;
    private PuppetDBResourceModelSource sut;
    private Cache cache;

    @Before
    public void setup() {
        puppetDBClient = mock(PuppetDBClient.class);
        cache = mock(Cache.class);
        sut = new PuppetDBResourceModelSource(puppetDBClient, "rundeck");
    }

    @Test
    public void testGetNodesNoCaching() throws ResourceModelSourceException, IOException {

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
    public void testGetNodesFromCache() throws ResourceModelSourceException, IOException {

        sut = new PuppetDBResourceModelSource(puppetDBClient, cache, "rundeck", null);

        NodeSetImpl cachedNodes = new NodeSetImpl();
        cachedNodes.putNode(new NodeEntryImpl("mypuppetnode.local"));

        when(cache.containsKey(PuppetDBResourceModelSource.CACHE_ENTRY_KEY)).thenReturn(true);
        when(cache.get(PuppetDBResourceModelSource.CACHE_ENTRY_KEY)).thenReturn(cachedNodes);

        INodeSet nodes = sut.getNodes();

        assertNotNull(nodes);
        assertNotNull(nodes.getNode("mypuppetnode.local"));

        verify(puppetDBClient, never()).getActiveNodes(null);
        verify(puppetDBClient, never()).getFacts(any(Expression.class));
        verify(cache, never()).put(eq(PuppetDBResourceModelSource.CACHE_ENTRY_KEY), any(INodeSet.class));
    }

    @Test
    public void testGetNodesFromEmptyCache() throws ResourceModelSourceException, IOException {

        sut = new PuppetDBResourceModelSource(puppetDBClient, cache, "rundeck", null);

        Node node = new Node();
        node.setName("mypuppetnode.local");
        List<Node> puppetNodes = new ArrayList<>();
        puppetNodes.add(node);

        Fact operatingsystem = new Fact();
        operatingsystem.setName("operatingsystem");
        operatingsystem.setValue("myOperatingSystem");
        operatingsystem.setCertname("mypuppetnode.local");
        List<Fact> facts = new ArrayList<>();
        facts.add(operatingsystem);

        when(puppetDBClient.getActiveNodes(null)).thenReturn(puppetNodes);
        when(puppetDBClient.getFacts(any(Expression.class))).thenReturn(facts);
        when(cache.containsKey(PuppetDBResourceModelSource.CACHE_ENTRY_KEY)).thenReturn(false);


        NodeEntryImpl nodeEntry = new NodeEntryImpl("mypuppetnode.local");
        NodeSetImpl rundeckNodes = new NodeSetImpl();
        rundeckNodes.putNode(nodeEntry);
        when(cache.get(PuppetDBResourceModelSource.CACHE_ENTRY_KEY)).thenReturn(rundeckNodes);

        INodeSet nodes = sut.getNodes();

        assertNotNull(nodes);
        assertNotNull(nodes.getNode("mypuppetnode.local"));

        verify(cache, times(1)).put(eq(PuppetDBResourceModelSource.CACHE_ENTRY_KEY), any(INodeSet.class));
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