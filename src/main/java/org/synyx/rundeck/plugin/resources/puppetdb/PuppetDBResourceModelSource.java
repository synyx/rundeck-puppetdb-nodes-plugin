package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.dtolabs.rundeck.core.common.NodeSetImpl;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import com.puppetlabs.puppetdb.javaclient.BasicAPIPreferences;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClient;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClientFactory;
import com.puppetlabs.puppetdb.javaclient.model.Fact;
import com.puppetlabs.puppetdb.javaclient.model.Node;
import com.puppetlabs.puppetdb.javaclient.query.Expression;
import org.apache.log4j.Logger;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import static com.puppetlabs.puppetdb.javaclient.query.Query.eq;
import static com.puppetlabs.puppetdb.javaclient.query.Query.or;

/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSource implements ResourceModelSource {

    public static final String PUPPETDB_HOST = "PUPPETDB_HOST";
    public static final String PUPPETDB_PORT = "PUPPETDB_PORT";
    public static final String SSL_DIR = "SSL_DIR";
    public static final String CA_CERT_PEM = "CA_CERT_PEM";
    public static final String CERT_PEM = "CERT_PEM";
    public static final String USERNAME = "USERNAME";


    private final PuppetDBClient client;
    private final String username;

    public PuppetDBResourceModelSource(Properties configuration) {

        BasicAPIPreferences prefs = new BasicAPIPreferences();
        prefs.setServiceHostname(configuration.getProperty(PUPPETDB_HOST));
        prefs.setServicePort(Integer.valueOf(configuration.getProperty(PUPPETDB_PORT)));

        prefs.setAllowAllHosts(false);

        File sslDir = new File(configuration.getProperty(SSL_DIR));

        File caCertPem = new File(sslDir, "certs/" + configuration.getProperty(CA_CERT_PEM));

        if (caCertPem.canRead()) {
            prefs.setCaCertPEM(caCertPem);
        }

        String agentCertPem = configuration.getProperty(CERT_PEM);
        File certPem = new File(sslDir, "certs/" + agentCertPem);

        prefs.setCertPEM(certPem);

        File privateKeyPEM = new File(sslDir, "private_keys/" + agentCertPem);
        prefs.setPrivateKeyPEM(privateKeyPEM);

        this.client = PuppetDBClientFactory.newClient(prefs);

        this.username = configuration.getProperty(USERNAME);
    }

    @Override
    public INodeSet getNodes() throws ResourceModelSourceException {
        try {
            return parsePuppetDBNodes(queryPuppetNodes(), queryPuppetFacts());
        } catch (IOException e) {
            throw new ResourceModelSourceException("Error requesting PuppetDB!", e);
        }
    }

    private INodeSet parsePuppetDBNodes(INodeSet rundeckNodes, List<Fact> facts) throws IOException {

        for (Fact fact : facts) {

            INodeEntry node = rundeckNodes.getNode(fact.getCertname());

            if (node != null) {
                NodeEntryImpl nodeEdit = (NodeEntryImpl) node;

                if (fact.getName().equalsIgnoreCase("osfamily")) {
                    nodeEdit.setOsFamily(fact.getValue());

                } else if (fact.getName().equalsIgnoreCase("operatingsystem")) {
                    nodeEdit.setOsName(fact.getValue());

                } else if (fact.getName().equalsIgnoreCase("operatingsystemrelease")) {
                    nodeEdit.setOsVersion(fact.getValue());

                } else if (fact.getName().equalsIgnoreCase("hardwaremodel")) {
                    nodeEdit.setOsArch(fact.getValue());
                }
            }
        }

        return rundeckNodes;
    }

    private INodeSet queryPuppetNodes() throws IOException {

        List<Node> activeNodes = client.getActiveNodes(null);

        final NodeSetImpl nodes = new NodeSetImpl();

        for (Node activeNode : activeNodes) {
            final NodeEntryImpl nodeEntry = new NodeEntryImpl(activeNode.getName(), activeNode.getName());
            nodeEntry.setUsername(username);
            nodes.putNode(nodeEntry);

        }

        return nodes;
    }

    private List<Fact> queryPuppetFacts() throws IOException {

        ArrayList<Expression<Fact>> factNames = new ArrayList<>();

        factNames.add(eq(Fact.NAME, "hardwaremodel"));
        factNames.add(eq(Fact.NAME, "lsbdistcodename"));
        factNames.add(eq(Fact.NAME, "lsbdistdescription"));
        factNames.add(eq(Fact.NAME, "operatingsystem"));
        factNames.add(eq(Fact.NAME, "operatingsystemrelease"));
        factNames.add(eq(Fact.NAME, "osfamily"));

        return client.getFacts(or(factNames));
    }
}
