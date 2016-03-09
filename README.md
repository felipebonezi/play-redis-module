# Play Redis Module


[![Latest release](https://img.shields.io/badge/latest_release-16.03-orange.svg)](https://github.com/0xbaadf00d/play-redis-module/releases)
[![GitHub license](https://jitpack.io/v/0xbaadf00d/play-redis-module.svg)](https://jitpack.io/#0xbaadf00d/play-redis-module)
[![Build](https://img.shields.io/travis-ci/0xbaadf00d/play-redis-module.svg?branch=master&style=flat)](https://travis-ci.org/0xbaadf00d/play-redis-module)
[![GitHub license](https://img.shields.io/badge/license-MIT-blue.svg)](https://raw.githubusercontent.com/0xbaadf00d/play-redis-module/master/LICENSE)

Redis module for Play Framework 2
*****

## Add play-redis-module to your project

#### build.sbt

     resolvers += "jitpack" at "https://jitpack.io"

     libraryDependencies += "com.github.0xbaadf00d" % "play-redis-module" % "release~YY.MM"

#### application.conf

    play.modules.enabled += "com.zero_x_baadf00d.play.module.redis.RedisModuleBinder"



## Usage

#### Example 1

```java
    public class MyController extends Controller {

        private final RedisModule redisModule;

        @Inject
        public MyController(final RedisModule redisModule) {
            this.redisModule = redisModule;
        }

        public Result index() {
            final String token = this.redisModule.getOrElse("key", () -> {
                return "new-token";
                }, 60);
            return ok(token);
        }
    }
```


#### Example 2

```java
    final RedisModule redisModule = Play.application().injector().instanceOf(RedisModule.class);
    try (final Jedis jedis = redisModule.getConnection()) {
        jedis.set("key", "Hello World!")
        jedis.expire("key", 60);
    }
```



## License
This project is released under terms of the [MIT license](https://raw.githubusercontent.com/0xbaadf00d/play-redis-module/master/LICENSE).
