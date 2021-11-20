package by.nyurush.portal.service.impl;

import by.nyurush.portal.service.RedisService;
import lombok.RequiredArgsConstructor;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class RedisServiceImpl implements RedisService {
    private final RedisTemplate<String, String> redisTemplate;

    @Override
    public void addCode(String key, String value) {
        redisTemplate.opsForValue().set(key, value, 24, TimeUnit.HOURS);
    }

    @Override
    public boolean isValidCode(String key, String value) {
        return value.equals(redisTemplate.opsForValue().get(key));
    }

    @Override
    public String getValue(String key) {
        return redisTemplate.opsForValue().get(key);
    }

    @Override
    public boolean isCodeExist(String code) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(code));
    }

}
