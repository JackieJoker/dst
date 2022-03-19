package dst.ass1.kv.impl;

import dst.ass1.kv.ISessionManager;
import dst.ass1.kv.SessionCreationFailedException;
import dst.ass1.kv.SessionNotFoundException;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.JedisPool;
import redis.clients.jedis.Transaction;

import java.util.List;
import java.util.UUID;

public class SessionManager implements ISessionManager {
    private final JedisPool pool;

    public SessionManager(JedisPool pool) {
        this.pool = pool;
    }

    @Override
    public String createSession(Long userId, int timeToLive) throws SessionCreationFailedException {
        try (Jedis jedis = pool.getResource()) {
            //assume that timeToLive is in seconds, otherwise use pexpire instead of expire
            UUID sessionId = UUID.randomUUID();
            Transaction t = jedis.multi();
            t.hset(sessionId.toString(), "userId", userId.toString());
            t.set("user:" + userId.toString(),sessionId.toString());

            t.expire(sessionId.toString(), timeToLive);
            t.expire("user:" + userId.toString(), timeToLive);
            List<Object> feedback = t.exec();
            if(!(feedback.get(0).equals(1L) && feedback.get(1).equals("OK"))) {
                throw new SessionCreationFailedException();
            }

            return sessionId.toString();
        }
    }

    @Override
    public void setSessionVariable(String sessionId, String key, String value) throws SessionNotFoundException {
        try (Jedis jedis = pool.getResource()) {
            if(!jedis.exists(sessionId))
                throw new SessionNotFoundException();
            jedis.hset(sessionId, key, value);
        }
    }

    @Override
    public String getSessionVariable(String sessionId, String key) throws SessionNotFoundException {
        try (Jedis jedis = pool.getResource()) {
            if(!jedis.exists(sessionId))
                throw new SessionNotFoundException();
            return jedis.hget(sessionId, key);
        }
    }

    @Override
    public Long getUserId(String sessionId) throws SessionNotFoundException {
        try (Jedis jedis = pool.getResource()) {
            String id = jedis.hget(sessionId, "userId");
            if(id == null)
                throw new SessionNotFoundException();
            return Long.valueOf(id);
        }
    }

    @Override
    public int getTimeToLive(String sessionId) throws SessionNotFoundException {
        try (Jedis jedis = pool.getResource()) {
            if(!jedis.exists(sessionId))
                throw new SessionNotFoundException();
            return (int) jedis.ttl(sessionId);
        }
    }

    @Override
    public String requireSession(Long userId, int timeToLive) throws SessionCreationFailedException {
        try (Jedis jedis = pool.getResource()) {
            if(!jedis.exists("user:" + userId.toString()))
                return createSession(userId, timeToLive);
            return jedis.get("user:" + userId.toString());
        }
    }

    @Override
    public void close() {
        pool.close();
    }
}
