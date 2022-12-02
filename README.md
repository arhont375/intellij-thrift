# Thrift Support (fork)

Plugin to support Thrift language in IntelliJ

## Why fork?

This is the fork of [original plugin](https://github.com/fkorotkov/intellij-thrift) developed by @fkorotkov, that 
was forked for publishing and few fixes as I'm using original plugin for development in different places, 
but unfortunately it wasn't receiving compatibility updates recently.

There is no guarantee for current fork to stay long, it's possible that it would be discontinued at the moment
when development for original plugin would become active again.

## How to build

[JDK 17 or later](https://adoptium.net/) is required to build from source.

```bash
./gradlew :thrift:buildPlugin
```

## How to run locally with new changes

```bash
./gradlew :thrift:runIde
```

## Development

 - Thrift grammar generated from the [BNF grammar](./thrift/src/main/grammar/Thrift.bnf) using
the [Grammar-Kit](https://github.com/JetBrains/Grammar-Kit) IDEA plugin.
