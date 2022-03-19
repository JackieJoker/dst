package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.ISessionManagerFactory;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.JedisPoolConfig;

import java.util.Properties;

public class SessionManagerFactory implements ISessionManagerFactory {

    @Override
    public ISessionManager createSessionManager(Properties properties) {
        // read "redis.host" and "redis.port" from the properties
        JedisPool pool = new JedisPool(new JedisPoolConfig(), properties.getProperty("redis.host"), Integer.parseInt(properties.getProperty("redis.port")));

        return new SessionManager(pool);
    }
}
