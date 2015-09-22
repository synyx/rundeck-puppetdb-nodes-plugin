package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClient;
import com.puppetlabs.puppetdb.javaclient.model.Fact;
import com.puppetlabs.puppetdb.javaclient.model.Node;
import com.puppetlabs.puppetdb.javaclient.query.Expression;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import static com.puppetlabs.puppetdb.javaclient.query.Query.eq;
import static com.puppetlabs.puppetdb.javaclient.query.Query.or;

/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSource implements ResourceModelSource {

    private final PuppetDBClient client;
    private final String username;
    private final Set<String> customFactNamesToQuery;
    private final Set<String> mandatoryFactNames = new HashSet<String>(Arrays.asList("hardwaremodel", "operatingsystem", "operatingsystemrelease", "osfamily"));

    public PuppetDBResourceModelSource(PuppetDBClient puppetDBClient, String username) {
        this(puppetDBClient, username, null);
    }

    public PuppetDBResourceModelSource(PuppetDBClient puppetDBClient, String username, Set<String> customFactNamesToQuery) {
        this.client = puppetDBClient;
        this.username = username;
        this.customFactNamesToQuery = customFactNamesToQuery;
    }

    @Override
    public INodeSet getNodes() throws ResourceModelSourceException {

        try {
            List<Node> activeNodes = client.getActiveNodes(null);
            if (activeNodes.isEmpty()) {
                throw new ResourceModelSourceException("Received ZERO nodes from PuppetDB!");
            }

            INodeSet rundeckNodes = new PuppetNodesToRundeckConverter(this.username).convert(activeNodes);

            List<Fact> facts = client.getFacts(createFactsQuery());
            if (facts.isEmpty()) {
                throw new ResourceModelSourceException("Received ZERO facts from PuppetDB!");
            }

            INodeSet mappedRundeckNodes = new PuppetFactsRundeckNodeEnricher().enrich(rundeckNodes, facts);

            return mappedRundeckNodes;
        } catch (IOException e) {
            throw new ResourceModelSourceException("Error requesting PuppetDB!", e);
        }
    }

    private Expression<Fact> createFactsQuery() throws IOException {

        ArrayList<Expression<Fact>> factsToQuery = new ArrayList<>();

        Set<String> factNames = new HashSet<>();

        factNames.addAll(mandatoryFactNames);

        if (customFactNamesToQuery != null) {
            factNames.addAll(customFactNamesToQuery);
        }

        for (String factName : factNames) {
            factsToQuery.add(eq(Fact.NAME, factName));
        }

        return or(factsToQuery);
    }
}
