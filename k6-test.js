import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    startTime: '30s',

    stages: [
        { duration: '30s', target: 50 },
        { duration: '30s', target: 50 },
        { duration: '30s', target: 100 },
        { duration: '30s', target: 100 }
        { duration: '30s', target: 250 }
        { duration: '30s', target: 250 }
        { duration: '30s', target: 500 }
        { duration: '30s', target: 500 }
        { duration: '30s', target: 1000 }
    ],

    noConnectionReuse: true,
    userAgent: 'k6_stress_test/1.0'
};

export default function () {
    let res = http.get('http://app:8080/files');
        check(res, {
        'Status 200': (r) => r.status === 200,
        'Timp răspuns < 500ms': (r) => r.timings.duration < 500
    });

    sleep(30);
}
