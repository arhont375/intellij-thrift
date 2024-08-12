# Thrift Support (fork)

Plugin to support Thrift language in IntelliJ IDEs

IDEA marketplace: https://plugins.jetbrains.com/plugin/19912-thrift-support-fork-

## Why fork?

Continuation of [original plugin](https://github.com/fkorotkov/intellij-thrift) with main focus on IDEA compatibility
updates. Feel free to open PR for any new feature or update of functionality.

## Contribution

### How to build

[JDK 21 or later](https://adoptium.net/) is required to build from source.

```bash
./gradlew :thrift:buildPlugin
```

### How to test locally with new changes

```bash
./gradlew :thrift:runIde
```

### Development

 - General [docs](https://plugins.jetbrains.com/docs/intellij/welcome.html) about plugin development
 - Thrift grammar generated from the [BNF grammar](./thrift/src/main/grammar/Thrift.bnf) using
the [Grammar-Kit](https://github.com/JetBrains/Grammar-Kit) IDEA plugin. 
 - [IDEA platform plugin reference](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html)

### Testing 

```bash
./gradlew :thrift:test
```

### Update for the next IDEA version

Generally the flow of updates is following:
1. Update [IDEA platform plugin](https://github.com/JetBrains/intellij-platform-gradle-plugin/releases) to the latest 
    version and resolve related deprecations, see `ideaPlatformPluginVersion` 
    in the [`gradle.properties`](gradle.properties);
1. Reference [IDEA vesrions](https://plugins.jetbrains.com/docs/intellij/build-number-ranges.html#platformVersions) 
    to find latest `Branch Number` to be used as the `ideaSinceVersion` and related `IntelliJ Platform Version`
    to be used as the `ideaVersion` in the [`gradle.properties`](gradle.properties);
1. Try to run tests and build the plugin for local test. If all fine it's good to submit PR.

### Other improvements and features

Feel free to open PR for any desired feature.
