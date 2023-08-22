package klang.parser

val IS_OS_DARWIN = System.getProperty("os.name").contains("mac", ignoreCase = true)
val INTEGRATION_ENABLED = System.getenv("integration.test").equals("enabled", ignoreCase = true)
