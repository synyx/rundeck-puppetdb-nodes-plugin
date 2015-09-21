package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.plugins.configuration.Describable;
import com.dtolabs.rundeck.core.plugins.configuration.Description;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceFactory;
import com.dtolabs.rundeck.plugins.util.DescriptionBuilder;

import java.util.Properties;

/**
 * @author Johannes Graf - graf@synyx.de
 */
@Plugin(name = "puppetdb", service = "ResourceModelSource")
public class PuppetDBResourceModelSourceFactory implements ResourceModelSourceFactory, Describable {

    private static final Description DESC = DescriptionBuilder.builder()
            .name("puppetdb")
            .title("PuppetDB")
            .description("Provides PuppetDB nodes for your RunDeck server.")
            .build();


    @Override
    public ResourceModelSource createResourceModelSource(Properties configuration) throws ConfigurationException {

        return new PuppetDBResourceModelSource(configuration);
    }

    @Override
    public Description getDescription() {
        return DESC;
    }
}
