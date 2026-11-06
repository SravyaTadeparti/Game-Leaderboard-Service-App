package com.sravya.leaderboard.model;

import jakarta.persistence.*;

@Entity
@Table(name = "players", uniqueConstraints = @UniqueConstraint(columnNames = "name"))
public class Player {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    @Column(nullable = false)
    private long totalScore;

    protected Player() {}

    public Player(String name) {
        this.name = name;
    }

    public void addScore(long score) {
        totalScore += score;
    }

    public Long getId() { return id; }
    public String getName() { return name; }
    public long getTotalScore() { return totalScore; }
}
