
import { Injectable, inject } from '@angular/core';
import { HttpClient, HttpHeaders, HttpErrorResponse } from '@angular/common/http';
import { Observable, catchError, throwError } from 'rxjs';

@Injectable({
  providedIn: 'root'
})
export class BookingService {

  private http = inject(HttpClient);
  private apiUrl = '/api/appointments';

  bookAppointment(payload: any): Observable<any> {

    const token = localStorage.getItem('access_token');

    const headers = new HttpHeaders({
      'Authorization': `Bearer ${token}`,
      'Content-Type': 'application/json'
    });

    return this.http.post<any>(
      this.apiUrl,
      payload,
      { headers }
    ).pipe(
      catchError((error: HttpErrorResponse) => {

        console.error('Booking API Error:', error);

        let errorMessage = 'Something went wrong. Please try again.';

        if (error.status === 0) {
          errorMessage = 'Unable to connect to the server. Please check your connection.';
        } else if (error.status === 401) {
          errorMessage = 'Session expired. Please login again.';
        } else if (error.status === 403) {
          errorMessage = 'You are not authorized to book this appointment.';
        } else if (error.status === 400) {
          errorMessage = error.error?.message || 'Invalid booking details.';
        } else if (error.status >= 500) {
          errorMessage = 'Server error. Please try again later.';
        } else {
          errorMessage = error.error?.message || errorMessage;
        }

        return throwError(() => new Error(errorMessage));

      })
    );
  }
}
