def appName = "kalah-game"

appender("dev_stdout", ConsoleAppender) {
    target = "System.out"
    encoder(PatternLayoutEncoder) {
        pattern = "%d{yyyy-MM-dd HH:mm:ss} [%thread] %-5level %logger{50} - %msg%n"
    }
}

root(INFO, ["dev_stdout"])