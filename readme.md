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

## Compare

Please see the logs in the `taurus` folder.

| Log Name            | Sample Count |
|---------------------|--------------|
| LruCache.log        | 627176       |
| SimpleWebServer.log | 593443       |
| ~~before.log~~      | ~~27027~~    |
| ~~after.log~~       | ~~37088~~    |
| ~~nio2.log~~        | ~~217592~~   |

