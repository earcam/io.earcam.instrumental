

## Naming

Could have been called Instrumentool but that looks stoopid.

## Building 

This project uses [maven-toolchains-plugin][maven-toolchains-plugin], so you'll need to [setup toolchains][maven-toolchains-plugin-setup].  
Examples for various OS/architectures can be found [here][maven-central-earcam-toolchain] 

With toolchains configured, run `mvn clean install`.

When modifying the code beware/be-aware the build will fail if Maven POMs, license headers, Java source or Javascript source aren't formatted
according to conventions (Apache Maven's standards for POMs, my own undocumented formatting for source).  To auto-format the lot, simply run 
`mvn -P '!strict,tidy'`.

To run PiTest use `mvn -P analyze clean install`

To run against SonarQube use `mvn -P analyze,report,sonar`


## Roadmap

### What's missing?

Lorem ipsum dolor sit amet, at audiam repudiandae vix, accusamus splendide sententiae ex his, in has solum inimicus evertitur. Nec oratio invidunt ut, mel ne vocent meliore. Vim assum semper ne, vis decore accusam maiestatis an. Duo ei reque error laoreet. Ex pro affert nostro salutandi, prima bonorum accusamus ei nam. Mea graece splendide id, mel ea adipisci corrumpit disputationi, ne feugiat perpetua usu. Per albucius expetenda ex, pri ad doming tritani.

### Future Features

Vim reque commodo ad, et sea impetus equidem nominati. Bonorum praesent constituam vix at, cu quo facete diceret delicata, pri meis partiendo te. Ut cum appareat voluptaria. Quo iisque meliore an.



## Implementation Notes


### Design

Sit te porro aperiri percipitur. Errem semper vix no, et duo dignissim mediocritatem. Ut graeci appetere periculis nec, id suas saperet nam, nam te laboramus mnesarchum. Mea ornatus nonumes graecis eu.


### Static Code Analysis 

#### Sonar

In ius quis voluptatibus, id sint tamquam virtute sed. Nam modus iudico assueverit an. Pro dolores evertitur theophrastus te, modo aperiam pertinacia sed te, laudem incorrupte mei ex. Qui assum adversarium ne.

#### Mutation Testing

Eos ne recteque temporibus, cum quem aperiri assentior te, eam quodsi nonumes phaedrum at. Pri alienum offendit appellantur at, sit an reque labores. At eos prima possit, ne quo erat dolores tractatos. Doctus constituam at vis, erat constituto dissentias eam at.

### Compiler Warnings

TODO?


[maven-toolchains-plugin]: http://maven.apache.org/plugins/maven-toolchains-plugin/
[maven-toolchains-plugin-setup]: https://maven.apache.org/guides/mini/guide-using-toolchains.html
[maven-central-earcam-toolchain]: http://search.maven.org/#search%7Cga%7C1%7Ca%3A%22io.earcam.maven.toolchain%22