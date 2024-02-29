package com.usg.chat.adapter.out.persistence;

import com.usg.chat.application.port.out.MemberPersistencePort;
import com.usg.chat.domain.Member;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;

import java.util.HashMap;
import java.util.Map;
import java.util.Set;

@Slf4j
@Component
@RequiredArgsConstructor
public class MemberPersistenceAdapter implements MemberPersistencePort {
    private final RedisTemplate<String, Object> redisTemplate;
    private final String RedisKeyPrefix = "member";

    @Override
    public void saveMember(Member member) {
        String key = RedisKeyPrefix + ":" + member.getEmail();
        Map<String, String> memberInfo = new HashMap<>();
        memberInfo.put("id", String.valueOf(member.getMemberId()));
        memberInfo.put("nickname", member.getNickname());
        redisTemplate.opsForHash().putAll(key, memberInfo);
    }

    @Override
    public String getNicknameByEmail(String email) {
        String key = RedisKeyPrefix + ":" + email;
        Object value = redisTemplate.opsForHash().get(key, "nickname");
        return value != null ? (String) value : null;
    }

    @Override
    public Long getIdByEmail(String email) {
        String key = RedisKeyPrefix + ":" + email;
        Object value = redisTemplate.opsForHash().get(key, "id");
        return value != null ? Long.parseLong((String) value) : null;
    }

    @Override
    public String getEmailById(Long id) {
        Set<String> keys = redisTemplate.keys(RedisKeyPrefix + ":*");
        for (String key : keys) {
            Object value = redisTemplate.opsForHash().get(key, "id");
            if (value != null && Long.parseLong((String) value) == id) {
                return key.substring((RedisKeyPrefix + ":").length());
            }
        }
        return null;
    }
}
