import http from 'k6/http';
import { check, sleep } from 'k6';

export let options = {
    vus: 10,
    duration: '3s',
};

export default function () {
    let res = http.get('http://localhost:8081/files');
    check(res, { 'status was 200': (r) => r.status == 200 });
    sleep(1);
}
