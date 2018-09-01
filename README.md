# Kinformer

This is a simple demonstration of [Ktor](https://github.com/ktorio/ktor), [Kotlinx HTML DSL](https://github.com/Kotlin/kotlinx.html) and [Kotlinx Coroutines](https://github.com/Kotlin/kotlinx.coroutines).

The app is very simple, it fetches crypto currencies ticker data from the [Bitstamp API](https://www.bitstamp.net/api/) and display them on a simple webpage.

## Run

```shell
./gradlew run
```

This will start a webserver on http://localhost:8080/

## Check style

Check style:
```shell
./gradlew ktlint
```

Fix style errors:
```shell
./gradlew ktlintFormat
```

## License

MIT