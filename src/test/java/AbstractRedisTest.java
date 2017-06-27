/*
 * The MIT License (MIT)
 *
 * Copyright (c) 2016 Thibault Meyer
 *
 * Permission is hereby granted, free of charge, to any person obtaining a copy
 * of this software and associated documentation files (the "Software"), to deal
 * in the Software without restriction, including without limitation the rights
 * to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is
 * furnished to do so, subject to the following conditions:
 *
 * The above copyright notice and this permission notice shall be included in all
 * copies or substantial portions of the Software.
 *
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR
 * IMPLIED, INCLUDING BUT NOT LIMITED TO THE WARRANTIES OF MERCHANTABILITY,
 * FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO EVENT SHALL THE
 * AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER
 * LIABILITY, WHETHER IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM,
 * OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE USE OR OTHER DEALINGS IN THE
 * SOFTWARE.
 */

import com.zero_x_baadf00d.play.module.redis.PlayRedisImpl;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;
import play.Application;
import play.inject.ApplicationLifecycle;
import play.test.Helpers;
import redis.clients.jedis.exceptions.JedisConnectionException;

import java.util.Collections;
import java.util.HashMap;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;

/**
 * All unit test classes must extend this class.
 *
 * @author Thibault Meyer
 * @version 17.06.26
 * @since 17.03.26
 */
public class AbstractRedisTest {

    /**
     * Handle to the Redis module.
     *
     * @since 17.03.26
     */
    protected PlayRedisImpl playRedis;

    /**
     * Handle to the current application instance.
     *
     * @since 17.03.25
     */
    protected Application application;

    /**
     * The Redis listen port to use.
     *
     * @since 17.03.26
     */
    private Integer redisPort;

    /**
     * Default constructor.
     *
     * @param redisPort The redis port number to use
     * @since 17.03.26
     */
    AbstractRedisTest(final Integer redisPort) {
        this.redisPort = redisPort;
    }

    /**
     * Initialize Redis module.
     *
     * @since 17.03.26
     */
    @Before
    public void initializeRedisModule() {
        if (this.playRedis == null) {
            this.application = Helpers.
                fakeApplication(new HashMap<String, Object>() {{
                    put(
                        "play.modules.disabled",
                        Collections.singletonList(
                            "com.zero_x_baadf00d.play.module.redis.PlayRedisModule"
                        )
                    );
                    put("redis.default.db.default", 0);
                    put("redis.default.host", "127.0.0.1");
                    put("redis.default.port", redisPort);
                }});
            Assert.assertEquals(
                0,
                this.application.config().getLong("redis.default.db.default")
            );
            Assert.assertEquals(
                "127.0.0.1",
                this.application.config().getString("redis.default.host")
            );
            Assert.assertEquals(
                redisPort.longValue(),
                this.application.config().getLong("redis.default.port")
            );
            this.playRedis = new PlayRedisImpl(
                mock(ApplicationLifecycle.class),
                this.application.config()
            );
            Assert.assertNotEquals(null, this.playRedis);
            try {
                this.playRedis.remove(
                    "junit.lock",
                    "junit.item",
                    "junit.item2",
                    "junit.counter"
                );
            } catch (JedisConnectionException ignore) {
            }
        }
    }

    /**
     * Destroy Redis module.
     *
     * @since 17.03.26
     */
    @After
    public void destroyRedis() {
        if (this.playRedis != null) {
            try {
                Assert.assertNull(
                    this.playRedis.stopHook()
                        .toCompletableFuture()
                        .get()
                );
            } catch (InterruptedException | ExecutionException e) {
                Assert.fail();
            }
        }
    }
}