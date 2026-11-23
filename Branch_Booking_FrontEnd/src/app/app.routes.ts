import { Routes } from '@angular/router';
import { LoginComponent } from './components/login/login.component';
import { BookingComponent } from './components/booking/booking.component';
import { authGuard } from './guards/auth.guard';

export const routes: Routes = [
  { path: 'login', component: LoginComponent },
  { path: 'booking', component: BookingComponent, canActivate: [authGuard] },
  { path: '', redirectTo: 'booking', pathMatch: 'full' },
  { path: '**', redirectTo: 'booking' }
];
