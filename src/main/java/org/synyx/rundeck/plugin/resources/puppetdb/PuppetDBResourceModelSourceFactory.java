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
            .stringProperty(PuppetDBResourceModelSource.PUPPETDB_HOST, null, true, "PuppetDB Host", "The hostname of your PuppetDB server")
            .integerProperty(PuppetDBResourceModelSource.PUPPETDB_PORT, "8081", false, "PuppetDB Port", "Port of the PuppetDB or blank for default")
            .stringProperty(PuppetDBResourceModelSource.SSL_DIR, "/var/lib/puppet/ssl", false, "SSL Directory", "The SSL directory of your puppet node or blank for default")
            .stringProperty(PuppetDBResourceModelSource.CA_CERT_PEM, "ca.pem", false, "Ca cert pem", "The filename of the Ca cert pem file or blank for default")
            .stringProperty(PuppetDBResourceModelSource.CERT_PEM, null, true, "Cert pem", "The cert pem filename of your rundeck node")
            .stringProperty(PuppetDBResourceModelSource.USERNAME, "rundeck", false, "Username", "The connecting username or blank for default")
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
