package com.sravya.leaderboard.api;

import com.sravya.leaderboard.service.LeaderboardService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Positive;
import org.springframework.http.HttpStatus;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api")
@Validated
public class LeaderboardController {
    private final LeaderboardService leaderboardService;

    public LeaderboardController(LeaderboardService leaderboardService) {
        this.leaderboardService = leaderboardService;
    }

    @PostMapping("/scores")
    @ResponseStatus(HttpStatus.CREATED)
    public LeaderboardEntry submitScore(@Valid @RequestBody ScoreRequest request) {
        return leaderboardService.submitScore(request.playerName().trim(), request.score());
    }

    @GetMapping("/leaderboard/top/{n}")
    public List<LeaderboardEntry> topPlayers(
            @PathVariable @Positive @Max(100) int n) {
        return leaderboardService.topPlayers(n);
    }
}
