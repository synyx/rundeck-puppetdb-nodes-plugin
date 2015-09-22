package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.dtolabs.rundeck.core.common.NodeSetImpl;
import com.puppetlabs.puppetdb.javaclient.model.Node;

import java.util.List;

/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetNodesToRundeckConverter {

    private final String username;

    public PuppetNodesToRundeckConverter(String username) {
        this.username = username;
    }

    public INodeSet convert(List<Node> puppetNodes) {

        final NodeSetImpl rundeckNodes = new NodeSetImpl();

        for (Node puppetNode : puppetNodes) {
            final NodeEntryImpl nodeEntry = new NodeEntryImpl(puppetNode.getName(), puppetNode.getName());
            nodeEntry.setUsername(username);
            rundeckNodes.putNode(nodeEntry);
        }

        return rundeckNodes;
    }
}
