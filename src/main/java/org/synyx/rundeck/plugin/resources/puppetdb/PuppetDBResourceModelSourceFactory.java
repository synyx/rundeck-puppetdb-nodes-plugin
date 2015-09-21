package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceFactory;

import java.util.Properties;

/**
 * @author Johannes Graf - graf@synyx.de
 */
public class PuppetDBResourceModelSourceFactory implements ResourceModelSourceFactory {

    @Override
    public ResourceModelSource createResourceModelSource(Properties configuration) throws ConfigurationException {
        throw new ConfigurationException("Not implemented!");
    }

}
