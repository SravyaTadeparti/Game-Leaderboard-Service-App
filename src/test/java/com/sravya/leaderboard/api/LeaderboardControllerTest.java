package com.sravya.leaderboard.api;

import com.sravya.leaderboard.service.LeaderboardService;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import java.util.List;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

class LeaderboardControllerTest {
    private MockMvc mockMvc;
    private LeaderboardService service;

    @BeforeEach
    void setUp() {
        service = mock(LeaderboardService.class);
        mockMvc = MockMvcBuilders.standaloneSetup(new LeaderboardController(service)).build();
    }

    @Test
    void submitsScore() throws Exception {
        when(service.submitScore("Alice", 100)).thenReturn(new LeaderboardEntry(0, "Alice", 100));

        mockMvc.perform(post("/api/scores")
                        .contentType("application/json")
                        .content("{\"playerName\":\"Alice\",\"score\":100}"))
                .andExpect(status().isCreated())
                .andExpect(jsonPath("$.playerName").value("Alice"))
                .andExpect(jsonPath("$.score").value(100));
    }

    @Test
    void rejectsNegativeScore() throws Exception {
        mockMvc.perform(post("/api/scores")
                        .contentType("application/json")
                        .content("{\"playerName\":\"Alice\",\"score\":-1}"))
                .andExpect(status().isBadRequest());
    }

    @Test
    void fetchesTopPlayers() throws Exception {
        when(service.topPlayers(2)).thenReturn(List.of(
                new LeaderboardEntry(1, "Alice", 250),
                new LeaderboardEntry(2, "Bob", 200)));

        mockMvc.perform(get("/api/leaderboard/top/2"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$[0].playerName").value("Alice"))
                .andExpect(jsonPath("$[1].playerName").value("Bob"));
    }
}
