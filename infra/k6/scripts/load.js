import http from 'k6/http';
import { check, sleep } from 'k6';

const BASE_URL = 'http://host.docker.internal:8080';
const TEST_USER = { email: 'k6-load@test.com', nickname: 'k6load' };

export const options = {
  stages: [
    { duration: '1m', target: 50 },
    { duration: '3m', target: 50 },
    { duration: '1m', target: 0 },
  ],
  thresholds: {
    http_req_failed: ['rate<0.01'],
    http_req_duration: ['p(95)<500', 'p(99)<1000'],
  },
};

export function setup() {
  http.post(`${BASE_URL}/api/v2/dev/sign-up`, JSON.stringify(TEST_USER), {
    headers: { 'Content-Type': 'application/json' },
  });

  const res = http.post(`${BASE_URL}/api/v2/dev/login`, JSON.stringify({ email: TEST_USER.email }), {
    headers: { 'Content-Type': 'application/json' },
  });

  const token = res.json('response.accessToken');
  if (!token) throw new Error('로그인 실패: accessToken 없음');
  return { token };
}

export default function (data) {
  const headers = {
    'Content-Type': 'application/json',
    Authorization: data.token,
  };
  const today = new Date().toISOString().split('T')[0];

  check(http.get(`${BASE_URL}/api/v2/users/me`, { headers }), {
    'users/me: status 200': (r) => r.status === 200,
  });

  check(http.get(`${BASE_URL}/api/v2/planners/daily?date=${today}`, { headers }), {
    'planners/daily: status 200': (r) => r.status === 200,
  });

  check(http.get(`${BASE_URL}/api/v2/calendars/monthly?month=${today.slice(0, 7)}`, { headers }), {
    'calendars/monthly: status 200': (r) => r.status === 200,
  });

  check(http.get(`${BASE_URL}/api/v2/calendars/daily?date=${today}`, { headers }), {
    'calendars/daily: status 200': (r) => r.status === 200,
  });

  sleep(1);
}
