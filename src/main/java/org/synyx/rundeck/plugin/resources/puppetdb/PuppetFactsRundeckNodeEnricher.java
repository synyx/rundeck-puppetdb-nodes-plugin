package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.puppetlabs.puppetdb.javaclient.model.Fact;

import java.util.List;

/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetFactsRundeckNodeEnricher {

    public INodeSet enrich(INodeSet rundeckNodes, List<Fact> puppetFacts) {

        for (Fact fact : puppetFacts) {

            INodeEntry node = rundeckNodes.getNode(fact.getCertname());

            if (node != null) {

                NodeEntryImpl nodeEdit = (NodeEntryImpl) node;

                switch (fact.getName().toLowerCase()) {
                    case "osfamily":
                        nodeEdit.setOsFamily(fact.getValue());
                        break;

                    case "operatingsystem":
                        nodeEdit.setOsName(fact.getValue());
                        break;

                    case "operatingsystemrelease":
                        nodeEdit.setOsVersion(fact.getValue());
                        break;

                    case "hardwaremodel":
                        nodeEdit.setOsArch(fact.getValue());
                        break;

                    default:
                        nodeEdit.getTags().add(fact.getValue());
                }

            }
        }

        return rundeckNodes;
    }
}
