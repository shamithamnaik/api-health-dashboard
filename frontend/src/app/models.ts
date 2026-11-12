export interface User { username: string; role: 'ADMIN' | 'OPERATOR' | 'VIEWER'; }

export interface ApiStatus {
  id: number; name: string; env: string; type: string; url: string;
  status: 'UP' | 'DOWN' | 'UNKNOWN';
  httpStatus: number | null; responseTimeMs: number | null;
  lastCheckedAt: string | null; message: string | null;
}

export interface CheckLog {
  id: number; status: string; httpStatus: number | null;
  responseTimeMs: number; message: string; checkedAt: string;
}

export interface Filters { envs: string[]; types: string[]; }
