# Simple Web Server
![Coverage](.github/badges/jacoco.svg)

![Branches](.github/badges/branches.svg)

A simplex HTTP 1.0 Server implemented in Java for educational
purposes based on W3C specifications (http://www.w3.org/Protocols/):

* [W3](https://www.w3.org/Protocols/HTTP/AsImplemented.html) Hypertext Transfer Protocol -- HTTP/0.9
* [RFC 1945](http://www.ietf.org/rfc/rfc1945.txt) Hypertext Transfer Protocol -- HTTP/1.0
* [RFC 2616](http://www.ietf.org/rfc/rfc2616.txt) Hypertext Transfer Protocol -- HTTP/1.1
* [RFC 2617](http://www.ietf.org/rfc/rfc2617.txt) HTTP Authentication: Basic and Digest Access Authentication
* [RFC 6265](http://tools.ietf.org/html/rfc6265) HTTP State Management Mechanism (Cookies)

## Build
```
./gradlew jar 
```

## Run
```
java -cp build/libs/simple-web-server-1.0.jar liteweb.Server
```

## Performance test
```
bzt performance.yml
```

## Test Results
``` log
baron:simple-web-server/ (stage-1.0.0/Temp/Temporary_simple_test✗) $ bzt performance.yml                                                  [18:01:15]
18:01:19 INFO: Taurus CLI Tool v1.16.23
18:01:19 INFO: Starting with configs: ['/home/baron/.bzt-rc', 'performance.yml']
18:01:19 INFO: Configuring...
18:01:19 INFO: Proxy settings not set
18:01:19 INFO: Artifacts dir: /home/baron/Bill/simple-web-server/2023-06-04_18-01-19.104919
18:01:19 INFO: Preparing...
18:01:24 WARNING: Module 'console' can be only used once, will merge all new instances into single
18:01:24 INFO: Starting...
18:01:24 INFO: Waiting for results...
18:01:25 INFO: Waiting for finish...
18:01:25 WARNING: Failed to check for updates, server returned 5xx.
18:02:40 WARNING: Please wait for graceful shutdown...
18:02:40 INFO: Shutting down...
18:02:40 INFO: Post-processing...
18:02:40 INFO: Test duration: 0:01:16
18:02:40 INFO: Samples count: 55700, 0.00% failures
18:02:40 INFO: Average times: total 0.070, latency 0.070, connect 0.000
18:02:40 INFO: Percentiles:
+---------------+---------------+
| Percentile, % | Resp. Time, s |
+---------------+---------------+
|           0.0 |         0.003 |
|          50.0 |         0.072 |
|          90.0 |         0.081 |
|          95.0 |         0.084 |
|          99.0 |         0.094 |
|          99.9 |         0.247 |
|         100.0 |          0.29 |
+---------------+---------------+
18:02:40 INFO: Request label stats:
+---------------------------------------------------------+--------+---------+--------+-------+
| label                                                   | status |    succ | avg_rt | error |
+---------------------------------------------------------+--------+---------+--------+-------+
| http://127.0.0.1:8080                                   |   OK   | 100.00% |  0.070 |       |
| http://127.0.0.1:8080/performance.yml                   |   OK   | 100.00% |  0.070 |       |
| http://127.0.0.1:8080/readme.md                         |   OK   | 100.00% |  0.070 |       |
| http://127.0.0.1:8080/src/main/java/liteweb/Server.java |   OK   | 100.00% |  0.070 |       |
+---------------------------------------------------------+--------+---------+--------+-------+
18:02:40 INFO: Test duration: 0:01:16
18:02:40 INFO: Samples count: 55700, 0.00% failures
18:02:40 INFO: Average times: total 0.070, latency 0.070, connect 0.000
18:02:40 INFO: Percentiles:
+---------------+---------------+
| Percentile, % | Resp. Time, s |
+---------------+---------------+
|           0.0 |         0.003 |
|          50.0 |         0.072 |
|          90.0 |         0.081 |
|          95.0 |         0.084 |
|          99.0 |         0.094 |
|          99.9 |         0.247 |
|         100.0 |          0.29 |
+---------------+---------------+
18:02:40 INFO: Request label stats:
+---------------------------------------------------------+--------+---------+--------+-------+
| label                                                   | status |    succ | avg_rt | error |
+---------------------------------------------------------+--------+---------+--------+-------+
| http://127.0.0.1:8080                                   |   OK   | 100.00% |  0.070 |       |
| http://127.0.0.1:8080/performance.yml                   |   OK   | 100.00% |  0.070 |       |
| http://127.0.0.1:8080/readme.md                         |   OK   | 100.00% |  0.070 |       |
| http://127.0.0.1:8080/src/main/java/liteweb/Server.java |   OK   | 100.00% |  0.070 |       |
+---------------------------------------------------------+--------+---------+--------+-------+
18:02:40 INFO: Artifacts dir: /home/baron/Bill/simple-web-server/2023-06-04_18-01-19.104919
18:02:40 INFO: Done performing with code: 0

```


