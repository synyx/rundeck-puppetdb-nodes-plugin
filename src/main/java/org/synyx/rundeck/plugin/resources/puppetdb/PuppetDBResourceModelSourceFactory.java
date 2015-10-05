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

    private static final String PUPPETDB_HOST = "puppetdb_host";
    private static final String PUPPETDB_PORT = "puppetdb_port";
    private static final String SSL_DIR = "ssl_dir";
    private static final String CA_CERT_PEM = "ca_cert_pem";
    private static final String CERT_PEM = "cert_pem";
    private static final String PRIVATE_KEY_PEM = "private_key_pem";
    private static final String USERNAME = "username";
    private static final String CACHE = "cache";
    private static final String FACTS = "facts";

    private static final String FACTS_DELIMITER = ";";
    private static final String[] REQUIRED_PROPERTIES = {PUPPETDB_HOST, PUPPETDB_PORT, SSL_DIR, CA_CERT_PEM, CERT_PEM, PRIVATE_KEY_PEM, USERNAME};

    private static final Description DESC = DescriptionBuilder.builder()
            .name("puppetdb")
            .title("PuppetDB")
            .description("Provides PuppetDB nodes for your RunDeck server.")
            .stringProperty(PUPPETDB_HOST, null, true, "Host", "Hostname of your PuppetDB server")
            .integerProperty(PUPPETDB_PORT, "8081", false, "Port", "Port of your PuppetDB")
            .stringProperty(CA_CERT_PEM, "/var/lib/puppet/ssl/certs/ca.pem", false, "Ca cert PEM", "Path of the CA cert PEM")
            .stringProperty(CERT_PEM, null, true, "Cert PEM", "Path of the rundeck node cert PEMe.g. /var/lib/puppet/ssl/certs/myrundecknode.pem")
            .stringProperty(PRIVATE_KEY_PEM, null, true, "Private key PEM", "Path of the rundeck node private key PEM e.g. /var/lib/puppet/ssl/private_keys/myrundecknode.pem")
            .stringProperty(USERNAME, "rundeck", false, "Username", "The connecting username")
            .stringProperty(FACTS, null, false, "Facts mapped to tags", "Additional facts, that will be mapped to your nodes as tags (semicolon separated values)")
            .integerProperty(CACHE, null, false, "Caching of PuppetDB nodes", "Set the time-to-live in seconds nodes get held in cache or blank to disable caching")
            .build();


    @Override
    public ResourceModelSource createResourceModelSource(Properties configuration) throws ConfigurationException {

        LOG.info("Creating new PuppetDB ResourceModelSource");

        validateConfiguration(configuration);

        Cache<String, INodeSet> cache = createCache(configuration);
        PuppetDBClient puppetDBClient = createPuppetDBClient(configuration);
        Set<String> facts = parseFacts(configuration);

        return new PuppetDBResourceModelSource(puppetDBClient, cache, configuration.getProperty(USERNAME), facts);
    }

    private void validateConfiguration(Properties configuration) throws ConfigurationException {

        if(LOG.isDebugEnabled()) {
            LOG.debug("Validating PuppetDB ResourceModelSource configuration");
        }

        for(String propertyName : REQUIRED_PROPERTIES){
            if(configuration.getProperty(propertyName) == null) {
                throw new ConfigurationException("Found mandatory property with " + propertyName + "=null");
            }
        }

        if(LOG.isDebugEnabled()) {
            LOG.debug("PuppetDB ResourceModelSource configuration is valid");
        }
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

        prefs.setCaCertPEM(new File(configuration.getProperty(PuppetDBResourceModelSourceFactory.CA_CERT_PEM)));
        prefs.setCertPEM(new File(configuration.getProperty(PuppetDBResourceModelSourceFactory.CERT_PEM)));
        prefs.setPrivateKeyPEM(new File(configuration.getProperty(PuppetDBResourceModelSourceFactory.PRIVATE_KEY_PEM)));

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
