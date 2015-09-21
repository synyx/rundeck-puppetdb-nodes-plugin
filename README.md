Rundeck PuppetDB Nodes Plugin
=============================

This is a Resource Model Source plugin for [RunDeck][] 2.5+ that provides
PuppetDB nodes for the RunDeck server.

[RunDeck]: http://rundeck.org

## Build

You need to install the [PuppetDB Java Client](https://github.com/puppetlabs/puppetdb-javaclient) into your
local Maven Repository ... because it's not distributed via Maven Central

```
clone https://github.com/puppetlabs/puppetdb-javaclient.git
cd puppetdb-javaclient
mvn clean install -DskipTests
```

After that just run `gradle build`.
