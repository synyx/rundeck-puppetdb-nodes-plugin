package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceFactory;

import java.util.Properties;

/**
 * @author Johannes Graf - graf@synyx.de
 */
@Plugin(name="puppetdb", service="ResourceModelSource")
public class PuppetDBResourceModelSourceFactory implements ResourceModelSourceFactory {

    @Override
    public ResourceModelSource createResourceModelSource(Properties configuration) throws ConfigurationException {

        return new PuppetDBResourceModelSource(configuration);
    }

}
