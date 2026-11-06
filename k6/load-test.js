import http from 'k6/http';
import { check, sleep } from 'k6';

export const options = {
  stages: [
    { duration: '15s', target: 20 },
    { duration: '30s', target: 20 },
    { duration: '15s', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500'],
  },
};

export default function () {
  const player = `k6-player-${Math.floor(Math.random() * 100)}`;
  const submit = http.post('http://localhost:8080/api/scores',
    JSON.stringify({ playerName: player, score: Math.floor(Math.random() * 1000) + 1 }),
    { headers: { 'Content-Type': 'application/json' } });
  check(submit, { 'score submitted': (r) => r.status === 201 });

  const top = http.get('http://localhost:8080/api/leaderboard/top/10');
  check(top, { 'leaderboard fetched': (r) => r.status === 200 });
  sleep(0.2);
}
