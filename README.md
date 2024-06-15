# Thrift Support (fork)

Plugin to support Thrift language in IntelliJ IDEs

IDEA marketplace: https://plugins.jetbrains.com/plugin/19912-thrift-support-fork-

## Why fork?

Continuation of [original plugin](https://github.com/fkorotkov/intellij-thrift) with main focus on IDEA compatibility
updates. Feel free to open PR for any new feature or update of functionality.

## How to build

[JDK 21 or later](https://adoptium.net/) is required to build from source.

```bash
./gradlew :thrift:buildPlugin
```

## How to run locally with new changes

```bash
./gradlew :thrift:runIde
```

## Development

 - General [docs](https://plugins.jetbrains.com/docs/intellij/welcome.html) about plugin development
 - Thrift grammar generated from the [BNF grammar](./thrift/src/main/grammar/Thrift.bnf) using
the [Grammar-Kit](https://github.com/JetBrains/Grammar-Kit) IDEA plugin. 
 - [IDEA platform plugin referenve](https://plugins.jetbrains.com/docs/intellij/tools-intellij-platform-gradle-plugin.html)

### Testing 

```bash
./gradlew :thrift:test
```
