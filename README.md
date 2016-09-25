# ProfilerAgent for Java

## Requirements
- JDK 8 or later

## How To Use

Build agent jar file:

```
$ ./build.sh
```

Run with profiler:

```
$ java -javaagent:agent.jar="<target-method-regex>[,<target-method-regex>]" -cp sample HelloWorld
```

For example, target-method-regex is:

- ^abc/def/ghi/method$
- ^.*/method$


```
$ java -showversion -javaagent:agent.jar=".*xxx$,.*zzz$" -cp sample HelloWorld
openjdk version "1.8.0_91"
OpenJDK Runtime Environment (build 1.8.0_91-8u91-b14-3ubuntu1~16.04.1-b14)
OpenJDK 64-Bit Server VM (build 25.91-b14, mixed mode)

main[id=1, piority=5]
  at HelloWorld.xxx(HelloWorld.java)
  at HelloWorld.main(HelloWorld.java:12)
xxx
yyy
main[id=1, piority=5]
  at jp.hashiwa.tmp.B.xxx(B.java)
  at HelloWorld.main(HelloWorld.java:15)
jp.hashiwa.tmp.B:xxx()
jp.hashiwa.tmp.B:yyy()
```
