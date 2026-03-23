import http from 'k6/http';
import { check } from 'k6';

export const options = {
  vus: 1000,
  duration: '1m',
  thresholds: {
    http_req_duration: ['p(95)<1000'],
    http_req_failed: ['rate<0.01'],
  },
};

export default function () {
  const res = http.get('http://localhost:8080/search');

  check(res, {
    'status is 200': (r) => r.status === 200,
    'response time < 1 sec': (r) => r.timings.duration < 1000,
  });
}