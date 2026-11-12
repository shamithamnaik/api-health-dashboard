import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable, tap } from 'rxjs';
import { User } from './models';

@Injectable({ providedIn: 'root' })
export class AuthService {
  user: User | null = JSON.parse(sessionStorage.getItem('user') ?? 'null');

  constructor(private http: HttpClient) {}

  get token(): string | null { return sessionStorage.getItem('token'); }

  /** Validates the credentials against the backend (Spring Security Basic auth). */
  login(username: string, password: string): Observable<User> {
    const token = btoa(`${username}:${password}`);
    return this.http
      .get<User>('/api/auth/me', { headers: { Authorization: `Basic ${token}` } })
      .pipe(tap(u => {
        sessionStorage.setItem('token', token);
        sessionStorage.setItem('user', JSON.stringify(u));
        this.user = u;
      }));
  }

  logout(): void { sessionStorage.clear(); this.user = null; }

  isLoggedIn(): boolean { return !!this.user; }
  isAdmin(): boolean { return this.user?.role === 'ADMIN'; }
  canViewLogs(): boolean { return this.user?.role === 'ADMIN' || this.user?.role === 'OPERATOR'; }
}
