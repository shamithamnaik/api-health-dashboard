import { Component } from '@angular/core';
import { FormsModule } from '@angular/forms';
import { CommonModule } from '@angular/common';
import { Router } from '@angular/router';
import { AuthService } from '../auth.service';

@Component({
  selector: 'app-login',
  standalone: true,
  imports: [CommonModule, FormsModule],
  template: `
    <div class="box">
      <h2>API Health Dashboard</h2>
      <input placeholder="Username" [(ngModel)]="username" (keyup.enter)="login()">
      <input placeholder="Password" type="password" [(ngModel)]="password" (keyup.enter)="login()">
      <button (click)="login()">Login</button>
      <p class="err" *ngIf="error">{{ error }}</p>
      <small>Demo users: admin / admin123 &nbsp;|&nbsp; operator / operator123 &nbsp;|&nbsp; viewer / viewer123</small>
    </div>`,
  styles: [`
    .box { max-width: 340px; margin: 14vh auto; background: #fff; padding: 28px; border-radius: 8px;
           display: flex; flex-direction: column; gap: 12px; box-shadow: 0 2px 8px #0002; }
    h2 { margin: 0 0 6px; }
    .err { color: #dc2626; margin: 0; }
    small { color: #666; }
  `]
})
export class LoginComponent {
  username = '';
  password = '';
  error = '';

  constructor(private auth: AuthService, private router: Router) {}

  login(): void {
    this.auth.login(this.username, this.password).subscribe({
      next: () => this.router.navigate(['/']),
      error: () => (this.error = 'Invalid username or password')
    });
  }
}
