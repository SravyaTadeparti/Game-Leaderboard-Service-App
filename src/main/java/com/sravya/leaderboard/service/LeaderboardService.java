package com.sravya.leaderboard.service;

import com.sravya.leaderboard.api.LeaderboardEntry;
import com.sravya.leaderboard.model.Player;
import com.sravya.leaderboard.repository.PlayerRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.StringRedisTemplate;
import org.springframework.data.redis.core.ZSetOperations;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

@Service
public class LeaderboardService {
    private final PlayerRepository playerRepository;
    private final StringRedisTemplate redisTemplate;
    private final String leaderboardKey;

    public LeaderboardService(PlayerRepository playerRepository,
                              StringRedisTemplate redisTemplate,
                              @Value("${leaderboard.redis.key:leaderboard:scores}") String leaderboardKey) {
        this.playerRepository = playerRepository;
        this.redisTemplate = redisTemplate;
        this.leaderboardKey = leaderboardKey;
    }

    @Transactional
    public LeaderboardEntry submitScore(String playerName, long score) {
        Player player = playerRepository.findByName(playerName)
                .orElseGet(() -> playerRepository.save(new Player(playerName)));
        player.addScore(score);
        playerRepository.save(player);

        Double newScore = redisTemplate.opsForZSet().incrementScore(leaderboardKey, playerName, score);
        if (newScore == null) {
            throw new IllegalStateException("Redis did not return the new leaderboard score");
        }
        return new LeaderboardEntry(0, playerName, newScore.longValue());
    }

    public List<LeaderboardEntry> topPlayers(int limit) {
        Set<ZSetOperations.TypedTuple<String>> scores = redisTemplate.opsForZSet()
                .reverseRangeWithScores(leaderboardKey, 0, limit - 1L);
        if (scores == null || scores.isEmpty()) {
            return List.of();
        }

        List<LeaderboardEntry> entries = new ArrayList<>();
        int rank = 1;
        for (ZSetOperations.TypedTuple<String> tuple : scores) {
            if (tuple.getValue() != null && tuple.getScore() != null) {
                entries.add(new LeaderboardEntry(rank++, tuple.getValue(), tuple.getScore().longValue()));
            }
        }
        return entries;
    }
}
