import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { ApiStatus, CheckLog, Filters } from './models';

@Injectable({ providedIn: 'root' })
export class ApiService {
  constructor(private http: HttpClient) {}

  list(env: string, status: string, type: string): Observable<ApiStatus[]> {
    let params = new HttpParams();
    if (env) params = params.set('env', env);
    if (status) params = params.set('status', status);
    if (type) params = params.set('type', type);
    return this.http.get<ApiStatus[]>('/api/apis', { params });
  }

  filters(): Observable<Filters> { return this.http.get<Filters>('/api/apis/filters'); }
  logs(id: number): Observable<CheckLog[]> { return this.http.get<CheckLog[]>(`/api/apis/${id}/logs?limit=50`); }
  check(id: number): Observable<void> { return this.http.post<void>(`/api/apis/${id}/check`, {}); }
  checkAll(): Observable<void> { return this.http.post<void>('/api/apis/check-all', {}); }
}
