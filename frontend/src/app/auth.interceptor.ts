import { HttpInterceptorFn } from '@angular/common/http';
import { inject } from '@angular/core';
import { Router } from '@angular/router';
import { catchError, throwError } from 'rxjs';
import { AuthService } from './auth.service';

/** Adds the Basic auth header to every call; on 401 sends the user back to the login page. */
export const authInterceptor: HttpInterceptorFn = (req, next) => {
  const auth = inject(AuthService);
  const router = inject(Router);
  const token = auth.token;
  const authed = token && !req.headers.has('Authorization')
    ? req.clone({ setHeaders: { Authorization: `Basic ${token}` } })
    : req;

  return next(authed).pipe(catchError(err => {
    if (err.status === 401 && !req.url.endsWith('/auth/me')) {
      auth.logout();
      router.navigate(['/login']);
    }
    return throwError(() => err);
  }));
};
