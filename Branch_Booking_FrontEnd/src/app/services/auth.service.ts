import { Injectable, inject, signal } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Router } from '@angular/router';
import { Observable, of, tap, catchError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class AuthService {
  private http = inject(HttpClient);
  private router = inject(Router);
  private apiUrl = '/api/auth';

  isAuthenticated = signal<boolean>(!!localStorage.getItem('access_token'));
  currentUser = signal<string>(localStorage.getItem('username') || 'Customer');

  login(email: string, password: string): Observable<any> {
    return this.http.post<any>(`${this.apiUrl}/login`, { email, password }).pipe(
      tap((res) => {
        this.setSession(res.token, res.user?.email || email);
      }),
      catchError(() => {        
        throw new Error('Invalid credentials.');
      })
    );
  }

  private setSession(token: string, username: string) {
    localStorage.setItem('access_token', token);
    localStorage.setItem('username', username);
    if (username && token) {
      this.isAuthenticated.set(true);
      this.currentUser.set(username);
    }
  }


  logout() {
    localStorage.removeItem('access_token');
    localStorage.removeItem('username');
    this.isAuthenticated.set(false);
    this.router.navigate(['/login']);
  }

  getToken(): string | null {
    return localStorage.getItem('access_token');
  }
}
