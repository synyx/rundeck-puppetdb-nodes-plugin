package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.plugins.Plugin;
import com.dtolabs.rundeck.core.plugins.configuration.ConfigurationException;
import com.dtolabs.rundeck.core.plugins.configuration.Describable;
import com.dtolabs.rundeck.core.plugins.configuration.Description;
import com.dtolabs.rundeck.core.resources.ResourceModelSource;
import com.dtolabs.rundeck.core.resources.ResourceModelSourceFactory;
import com.dtolabs.rundeck.plugins.util.DescriptionBuilder;
import com.puppetlabs.puppetdb.javaclient.BasicAPIPreferences;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClient;
import com.puppetlabs.puppetdb.javaclient.PuppetDBClientFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;

/**
 * @author Johannes Graf - graf@synyx.de
 */
@Plugin(name = "puppetdb", service = "ResourceModelSource")
public class PuppetDBResourceModelSourceFactory implements ResourceModelSourceFactory, Describable {

    private static final Logger LOG = LoggerFactory.getLogger(PuppetDBResourceModelSource.class);

    public static final String PUPPETDB_HOST = "PUPPETDB_HOST";
    public static final String PUPPETDB_PORT = "PUPPETDB_PORT";
    public static final String SSL_DIR = "SSL_DIR";
    public static final String CA_CERT_PEM = "CA_CERT_PEM";
    public static final String CERT_PEM = "CERT_PEM";
    public static final String USERNAME = "USERNAME";

    private static final Description DESC = DescriptionBuilder.builder()
            .name("puppetdb")
            .title("PuppetDB")
            .description("Provides PuppetDB nodes for your RunDeck server.")
            .stringProperty(PUPPETDB_HOST, null, true, "PuppetDB Host", "The hostname of your PuppetDB server")
            .integerProperty(PUPPETDB_PORT, "8081", false, "PuppetDB Port", "Port of the PuppetDB or blank for default")
            .stringProperty(SSL_DIR, "/var/lib/puppet/ssl", false, "SSL Directory", "The SSL directory of your puppet node or blank for default")
            .stringProperty(CA_CERT_PEM, "ca.pem", false, "Ca cert pem", "The filename of the Ca cert pem file or blank for default")
            .stringProperty(CERT_PEM, null, true, "Cert pem", "The cert pem filename of your rundeck node")
            .stringProperty(USERNAME, "rundeck", false, "Username", "The connecting username or blank for default")
            .build();


    @Override
    public ResourceModelSource createResourceModelSource(Properties configuration) throws ConfigurationException {

        LOG.info("Creating PuppetDB ResourceModelSource");

        final Set<String> facts = new HashSet<String>(Arrays.asList("lsbdistcodename", "lsbdistdescription"));

        PuppetDBClient puppetDBClient = createPuppetDBClient(configuration);

        return new PuppetDBResourceModelSource(puppetDBClient, configuration.getProperty(USERNAME), facts);
    }

    @Override
    public Description getDescription() {
        return DESC;
    }

    private PuppetDBClient createPuppetDBClient(Properties configuration) {

        BasicAPIPreferences prefs = new BasicAPIPreferences();
        prefs.setServiceHostname(configuration.getProperty(PuppetDBResourceModelSourceFactory.PUPPETDB_HOST));
        prefs.setServicePort(Integer.valueOf(configuration.getProperty(PuppetDBResourceModelSourceFactory.PUPPETDB_PORT)));

        prefs.setAllowAllHosts(false);

        File sslDir = new File(configuration.getProperty(PuppetDBResourceModelSourceFactory.SSL_DIR));

        File caCertPem = new File(sslDir, "certs/" + configuration.getProperty(PuppetDBResourceModelSourceFactory.CA_CERT_PEM));

        if (caCertPem.canRead()) {
            prefs.setCaCertPEM(caCertPem);
        }

        String agentCertPem = configuration.getProperty(PuppetDBResourceModelSourceFactory.CERT_PEM);
        File certPem = new File(sslDir, "certs/" + agentCertPem);

        prefs.setCertPEM(certPem);

        File privateKeyPEM = new File(sslDir, "private_keys/" + agentCertPem);
        prefs.setPrivateKeyPEM(privateKeyPEM);

        return PuppetDBClientFactory.newClient(prefs);
    }
}
