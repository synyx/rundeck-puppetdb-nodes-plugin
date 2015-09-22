package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeEntry;
import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.common.NodeEntryImpl;
import com.dtolabs.rundeck.core.common.NodeSetImpl;
import com.puppetlabs.puppetdb.javaclient.model.Fact;
import org.junit.Test;

import java.util.ArrayList;
import java.util.List;

import static org.hamcrest.core.Is.is;
import static org.junit.Assert.assertThat;
import static org.junit.Assert.assertTrue;

/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetFactsRundeckNodeEnricherTest {

    public static final String OS_FAMILY = "Debian";
    public static final String DIST_CODE_NAME = "trusty";
    public static final String DIST_DESCRIPTION = "Ubuntu 14.04.1 LTS";
    public static final String NODE_ONE = "rundeck.local";
    public static final String OS_RELEASE = "14.04";
    public static final String OS = "Ubuntu";
    public static final String HARDWARE_MODEL = "x86_64";

    @Test
    public void testMap() throws Exception {

        PuppetFactsRundeckNodeEnricher sut = new PuppetFactsRundeckNodeEnricher();

        NodeSetImpl rundeckNodes = new NodeSetImpl();

        NodeEntryImpl nodeOne = new NodeEntryImpl(NODE_ONE, NODE_ONE);
        rundeckNodes.putNode(nodeOne);

        NodeEntryImpl nodeTwo = new NodeEntryImpl("foobar.local", "foobar.local");
        rundeckNodes.putNode(nodeTwo);

        List<Fact> facts = new ArrayList<>();

        Fact hardwaremodel = new Fact();
        hardwaremodel.setName("hardwaremodel");
        hardwaremodel.setValue(HARDWARE_MODEL);
        hardwaremodel.setCertname(NODE_ONE);
        facts.add(hardwaremodel);

        Fact operatingsystem = new Fact();
        operatingsystem.setName("operatingsystem");
        operatingsystem.setValue(OS);
        operatingsystem.setCertname(NODE_ONE);
        facts.add(operatingsystem);

        Fact operatingsystemrelease = new Fact();
        operatingsystemrelease.setName("operatingsystemrelease");
        operatingsystemrelease.setValue(OS_RELEASE);
        operatingsystemrelease.setCertname(NODE_ONE);
        facts.add(operatingsystemrelease);

        Fact osfamily = new Fact();
        osfamily.setName("osfamily");
        osfamily.setValue(OS_FAMILY);
        osfamily.setCertname(NODE_ONE);
        facts.add(osfamily);

        Fact lsbdistcodename = new Fact();
        lsbdistcodename.setName("lsbdistcodename");
        lsbdistcodename.setValue(DIST_CODE_NAME);
        lsbdistcodename.setCertname(NODE_ONE);
        facts.add(lsbdistcodename);

        Fact lsbdistdescription = new Fact();
        lsbdistdescription.setName("lsbdistdescription");
        lsbdistdescription.setValue(DIST_DESCRIPTION);
        lsbdistdescription.setCertname(NODE_ONE);
        facts.add(lsbdistdescription);

        INodeSet mapedRundeckNodes = sut.enrich(rundeckNodes, facts);

        INodeEntry enrichedNodeOne = mapedRundeckNodes.getNode(NODE_ONE);

        assertThat(enrichedNodeOne.getOsArch(), is(HARDWARE_MODEL));
        assertThat(enrichedNodeOne.getOsFamily(), is(OS_FAMILY));
        assertThat(enrichedNodeOne.getOsName(), is(OS));
        assertThat(enrichedNodeOne.getOsVersion(), is(OS_RELEASE));
        assertTrue(enrichedNodeOne.getTags().contains(DIST_CODE_NAME));
        assertTrue(enrichedNodeOne.getTags().contains(DIST_DESCRIPTION));
    }
}