

## Current State

Due to various pains regarding multi-release JARs (lack of support in Maven and Jacoco), and no well-defined 
way to run tests against different JDKs in Maven (i.e. toolchains per plugin execution) - the build is  
dependent on JDK paths.  And while the modules and tests run against Java 8, 9 and 10 - the build itself must 
be launched with Java 8.


## Building 

This project uses [maven-toolchains-plugin][maven-toolchains-plugin], so you'll need to [setup toolchains][maven-toolchains-plugin-setup].  
Examples for various OS/architectures can be found [here][maven-central-earcam-toolchain] 

With toolchains configured, run `mvn clean install`.

When modifying the code beware/be-aware the build will fail if Maven POMs, license headers, Java source or Javascript source aren't formatted
according to conventions (Apache Maven's standards for POMs, my own undocumented formatting for source).  To auto-format the lot, simply run 
`mvn -P '!strict,tidy'`.

To run PiTest use `mvn -P analyse clean install`

To run against SonarQube use `mvn -P analyse,report,sonar`

## SCA Metrics

Due to issues with Jacoco vs mutli-release JARs, coverage reports are currently only available 
via [SonarCloud](https://sonarcloud.io/component_measures?id=io.earcam%3Aio.earcam.instrumental&metric=coverage). 



## Roadmap

TODO


[maven-toolchains-plugin]: http://maven.apache.org/plugins/maven-toolchains-plugin/
[maven-toolchains-plugin-setup]: https://maven.apache.org/guides/mini/guide-using-toolchains.html
[maven-central-earcam-toolchain]: http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22io.earcam.maven.toolchain%22