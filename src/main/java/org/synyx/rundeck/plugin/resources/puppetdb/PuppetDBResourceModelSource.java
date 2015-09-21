package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.dtolabs.rundeck.core.common.NodeSetImpl;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;

import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSource implements ResourceModelSource {

    private final Properties configuration;

    public PuppetDBResourceModelSource(Properties configuration) {
        this.configuration = configuration;
    }

    @Override
    public INodeSet getNodes() throws ResourceModelSourceException {

        final NodeSetImpl nodes = getMockedNodes();

        return nodes;
    }

    private NodeSetImpl getMockedNodes() {
        final NodeSetImpl nodes = new NodeSetImpl();

        nodes.putNode(mockNode("tata-gmo-backend-ci.synyx.coffee", "ci"));
        nodes.putNode(mockNode("tata-gmo-backend-stage.synyx.coffee", "stage"));
        nodes.putNode(mockNode("tata-gmo-backend-qa.synyx.coffee", "qa"));

        return nodes;
    }

    private NodeEntryImpl mockNode(String hostname, String system) {
        final NodeEntryImpl stage = new NodeEntryImpl();
        stage.setHostname(hostname);
        stage.setNodename(hostname);
        stage.setOsArch("x86_64");
        stage.setOsFamily("Debian");
        stage.setOsName("Debian");
        stage.setOsVersion("7.8");
        stage.setUsername("gmo");
        Set<String> tagsStage = new HashSet<>();
        tagsStage.add(system);
        stage.setTags(tagsStage);
        stage.setAttribute("Environment", "production");
        return stage;
    }
}
