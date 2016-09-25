# ProfilerAgent for Java

## Requirements
- JDK 8 or later

## How To Use

Build agent jar file:

```
./build.sh
```

Run with profiler:

```
java -javaagent:agent.jar="<target-method-regex>[,<target-method-regex>]" -cp sample HelloWorld
```

For example, target-method-regex is:

- ^abc/def/ghi/method$
- ^.*/method$

