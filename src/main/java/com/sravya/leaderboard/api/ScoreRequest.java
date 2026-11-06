package com.sravya.leaderboard.api;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Positive;

public record ScoreRequest(
        @NotBlank String playerName,
        @Positive long score
) {}
