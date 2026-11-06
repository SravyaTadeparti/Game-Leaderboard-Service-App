package com.sravya.leaderboard.service;

import com.sravya.leaderboard.model.Player;
import com.sravya.leaderboard.repository.PlayerRepository;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.data.redis.core.DefaultTypedTuple;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;

import java.util.LinkedHashSet;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;

class LeaderboardServiceTest {
    private PlayerRepository repository;
    private StringRedisTemplate redis;
    private ZSetOperations<String, String> zset;
    private LeaderboardService service;

    @BeforeEach
    void setUp() {
        repository = mock(PlayerRepository.class);
        redis = mock(StringRedisTemplate.class);
        zset = mock(ZSetOperations.class);
        when(redis.opsForZSet()).thenReturn(zset);
        service = new LeaderboardService(repository, redis, "leaderboard:scores");
    }

    @Test
    void addsScoreForExistingPlayer() {
        Player player = new Player("Alice");
        when(repository.findByName("Alice")).thenReturn(Optional.of(player));
        when(zset.incrementScore("leaderboard:scores", "Alice", 100)).thenReturn(100.0);

        var result = service.submitScore("Alice", 100);

        assertThat(result.playerName()).isEqualTo("Alice");
        assertThat(result.score()).isEqualTo(100);
        verify(repository).save(player);
    }

    @Test
    void returnsPlayersRankedByRedisScore() {
        LinkedHashSet<ZSetOperations.TypedTuple<String>> tuples = new LinkedHashSet<>();
        tuples.add(new DefaultTypedTuple<>("Alice", 250.0));
        tuples.add(new DefaultTypedTuple<>("Bob", 200.0));
        when(zset.reverseRangeWithScores("leaderboard:scores", 0, 1))
                .thenReturn(tuples);

        var result = service.topPlayers(2);

        assertThat(result).hasSize(2);
        assertThat(result.get(0).playerName()).isEqualTo("Alice");
        assertThat(result.get(0).rank()).isEqualTo(1);
    }
}
