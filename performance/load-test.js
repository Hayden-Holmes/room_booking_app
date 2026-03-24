import http from 'k6/http';
import { check } from 'k6';

export const options = {
  scenarios: {
    // Scenario 1: single user performance check
    single_user_response_time: {
      executor: 'constant-vus',
      vus: 1,
      duration: '30s',
      exec: 'singleUserTest',
    },

    // Scenario 2: load test with 1000 users
    load_test_1000_users: {
      executor: 'constant-vus',
      vus: 1000,
      duration: '1m',
      exec: 'loadTest',
      startTime: '30s', // starts after first test finishes
    },
  },

  thresholds: {
    // Only enforce response time on the single user test
    'http_req_duration{scenario:single_user_response_time}': ['p(95)<1000'],
    'http_req_failed': ['rate<0.01'],
  },
};

// Scenario 1 function
export function singleUserTest() {
  const res = http.get('http://localhost:8080/search');

  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 1 sec': (r) => r.timings.duration < 1000,
  });
}

// Scenario 2 function
export function loadTest() {
  http.get('http://localhost:8080/search');
}