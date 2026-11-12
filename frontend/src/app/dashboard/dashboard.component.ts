import { Component, OnDestroy, OnInit } from '@angular/core';
import { CommonModule } from '@angular/common';
import { FormsModule } from '@angular/forms';
import { Router } from '@angular/router';
import { Subscription, timer } from 'rxjs';
import { ApiService } from '../api.service';
import { AuthService } from '../auth.service';
import { ApiStatus, CheckLog } from '../models';

const REFRESH_MS = 5000;   // UI auto-refresh interval

@Component({
  selector: 'app-dashboard',
  standalone: true,
  imports: [CommonModule, FormsModule],
  templateUrl: './dashboard.component.html',
  styleUrl: './dashboard.component.css'
})
export class DashboardComponent implements OnInit, OnDestroy {
  apis: ApiStatus[] = [];
  envs: string[] = [];
  types: string[] = [];

  env = '';
  status = '';
  type = '';

  selected: ApiStatus | null = null;
  logs: CheckLog[] = [];
  lastRefresh: Date | null = null;
  error = '';

  private timerSub?: Subscription;

  constructor(public auth: AuthService, private api: ApiService, private router: Router) {}

  ngOnInit(): void {
    this.timerSub = timer(0, REFRESH_MS).subscribe(() => this.load());
  }

  ngOnDestroy(): void { this.timerSub?.unsubscribe(); }

  load(): void {
    this.api.filters().subscribe(f => { this.envs = f.envs; this.types = f.types; });
    this.api.list(this.env, this.status, this.type).subscribe({
      next: list => { this.apis = list; this.lastRefresh = new Date(); this.error = ''; },
      error: () => (this.error = 'Cannot reach backend')
    });
    if (this.selected) this.loadLogs();
  }

  count(status: string): number { return this.apis.filter(a => a.status === status).length; }

  open(a: ApiStatus): void {
    if (!this.auth.canViewLogs()) return;
    this.selected = a;
    this.loadLogs();
  }

  close(): void { this.selected = null; this.logs = []; }

  private loadLogs(): void {
    if (this.selected) this.api.logs(this.selected.id).subscribe(l => (this.logs = l));
  }

  checkNow(): void {
    if (this.selected) this.api.check(this.selected.id).subscribe(() => setTimeout(() => this.load(), 1500));
  }

  checkAll(): void {
    this.api.checkAll().subscribe(() => setTimeout(() => this.load(), 2500));
  }

  logout(): void { this.auth.logout(); this.router.navigate(['/login']); }
}
