package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeSet;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceException;

import java.util.Properties;

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

        throw new ResourceModelSourceException("Not implemented!");
    }
}
