package org.synyx.rundeck.plugin.resources.puppetdb;

import com.dtolabs.rundeck.core.common.INodeSet;
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
import org.ehcache.Cache;
import org.ehcache.CacheManager;
import org.ehcache.CacheManagerBuilder;
import org.ehcache.config.CacheConfigurationBuilder;
import org.ehcache.config.ResourcePoolsBuilder;
import org.ehcache.expiry.Duration;
import org.ehcache.expiry.Expirations;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Properties;
import java.util.Set;
import java.util.concurrent.TimeUnit;

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
    public static final String CACHE = "CACHE";
    public static final String FACTS_DELIMITER = ";";

    public static final String FACTS = "FACTS";
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
            .stringProperty(FACTS, null, false, "Facts mapped to tags", "Additional facts, that will be mapped to your nodes as tags (semicolon separated values)")
            .integerProperty(CACHE, null, false, "Caching of PuppetDB nodes", "Set the time in seconds nodes get removed from cache or blank to disable caching")
            .build();


    @Override
    public ResourceModelSource createResourceModelSource(Properties configuration) throws ConfigurationException {

        LOG.info("Creating new PuppetDB ResourceModelSource");

        Cache<String, INodeSet> cache = createCache(configuration);
        PuppetDBClient puppetDBClient = createPuppetDBClient(configuration);
        Set<String> facts = parseFacts(configuration);

        return new PuppetDBResourceModelSource(puppetDBClient, cache, configuration.getProperty(USERNAME), facts);
    }

    @Override
    public Description getDescription() {
        return DESC;
    }

    private Cache<String, INodeSet> createCache(Properties configuration) {

        Cache<String, INodeSet> cache = null;

        if(configuration.get(CACHE) == null) {
            LOG.info("Caching of PuppetDB nodes is disabled!");
        } else {
            Integer cachingInSeconds = Integer.valueOf(configuration.getProperty(CACHE));
            CacheManager cacheManager = CacheManagerBuilder.newCacheManagerBuilder()
                    .withCache(
                            "puppetdb",
                            CacheConfigurationBuilder
                                    .newCacheConfigurationBuilder()
                                    .withExpiry(Expirations.timeToLiveExpiration(new Duration(cachingInSeconds, TimeUnit.SECONDS)))
                                    .buildConfig(String.class, INodeSet.class)
                    )
                    .build(true);
            cache = cacheManager.getCache("puppetdb", String.class, INodeSet.class);
        }
        return cache;
    }

    private PuppetDBClient createPuppetDBClient(Properties configuration) {

        BasicAPIPreferences prefs = new BasicAPIPreferences();
        prefs.setServiceHostname(configuration.getProperty(PuppetDBResourceModelSourceFactory.PUPPETDB_HOST));
        prefs.setServicePort(Integer.parseInt(configuration.getProperty(PuppetDBResourceModelSourceFactory.PUPPETDB_PORT)));

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

    private Set<String> parseFacts(Properties configuration) {

        String rawFacts = configuration.getProperty(FACTS);

        if(rawFacts == null) {
            return null;
        }

        return new HashSet<>(Arrays.asList(rawFacts.split(FACTS_DELIMITER)));
    }
}
