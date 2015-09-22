package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.INodeSet;
import com.puppetlabs.puppetdb.javaclient.model.Node;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;

/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetNodesToRundeckConverterTest {

    public static final String NODE_NAME = "rundeck.local";
    public static final String USERNAME = "rundeck";

    @Test
    public void testConvert() throws Exception {

        Node puppetNode = new Node();
        puppetNode.setName(NODE_NAME);

        List<Node> puppetNodes = new ArrayList<>();
        puppetNodes.add(puppetNode);

        PuppetNodesToRundeckConverter sut = new PuppetNodesToRundeckConverter(USERNAME);

        INodeSet rundeckNodes = sut.convert(puppetNodes);

        INodeEntry rundeckNode = rundeckNodes.getNode(NODE_NAME);

        assertThat(rundeckNode.getNodename(), is(NODE_NAME));
        assertThat(rundeckNode.getHostname(), is(NODE_NAME));
        assertThat(rundeckNode.getUsername(), is(USERNAME));


    }
}