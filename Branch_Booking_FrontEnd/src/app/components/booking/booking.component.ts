import { Component, inject, OnInit } from "@angular/core";
import {
  FormBuilder,
  FormGroup,
  Validators,
  ReactiveFormsModule,
} from "@angular/forms";
import { HttpClient } from "@angular/common/http";
import { catchError, of } from "rxjs";
import { NgbDatepickerModule, NgbDateStruct } from "@ng-bootstrap/ng-bootstrap";
import { BookingService } from "../../services/booking.service";

interface TimeSlot {
  time: string;
  available: boolean;
}

@Component({
  selector: "app-booking",
  standalone: true,
  imports: [ReactiveFormsModule, NgbDatepickerModule],
  templateUrl: "./booking.component.html",
  styleUrl: "./booking.component.css",
})
export class BookingComponent implements OnInit {

  private fb = inject(FormBuilder);
  private http = inject(HttpClient);
  private bookingService = inject(BookingService);
  
  selectedDay = 18;
  selectedTime = "10:30 AM";
  confirmation: any = null;
  private today = new Date();

  minDate: NgbDateStruct = {
    year: this.today.getFullYear(),
    month: this.today.getMonth() + 1,
    day: this.today.getDate(),
  };

  // Disable Saturdays and Sundays (same as your old greyed-out days)
  isDisabled = (date: NgbDateStruct): boolean => {
    const day = new Date(date.year, date.month - 1, date.day).getDay();
    return day === 0 || day === 6;
  };
  bookingSuccessDetails: any;
  userName: any;

  private formatDate(d: NgbDateStruct): string {
    return new Date(d.year, d.month - 1, d.day).toLocaleDateString("en-US", {
      weekday: "long",
      year: "numeric",
      month: "long",
      day: "numeric",
    });
  }

  timeSlots: TimeSlot[] = [
    { time: "09:00 AM", available: true },
    { time: "09:30 AM", available: true },
    { time: "10:00 AM", available: true },
    { time: "10:30 AM", available: true },
    { time: "11:00 AM", available: true },
    { time: "11:30 AM", available: true },
    { time: "02:00 PM", available: true },
    { time: "02:30 PM", available: true },
    { time: "03:00 PM", available: true },
  ];
 

  ngOnInit(): void {
    // Fetch logged-in user ID 
    this.userName = localStorage.getItem('username') || 'Unknown User';
    // Fetch available time slots
    // this.loadAvailableTimeSlots();
}

  bookingForm: FormGroup = this.fb.group({
    region: ["Gauteng", Validators.required],
    branch: ["Midrand Branch", Validators.required],
    date: [this.minDate, Validators.required],
    time: ["10:30 AM", Validators.required],
    fullName: ["", Validators.required],
    email: ["", [Validators.required, Validators.email]],
    phone: ["", Validators.required],
    purpose: ["Account Opening", Validators.required],
  });

  selectDay(day: { num: number; disabled: boolean }) {
    if (!day.disabled) {
      this.selectedDay = day.num;
      this.bookingForm.patchValue({
        date: `November ${day.num}, 2026`,
      });
    }
  }

  selectTime(time: string) {
    this.selectedTime = time;
    this.bookingForm.patchValue({ time });
  }




submitBooking() {

  if (this.bookingForm.valid) {

    const value = this.bookingForm.value; 
    
    const payload = {
      ...value,
      date: this.formatDate(value.date)
    };
     const appointmentDateTime =  `${payload.date} ${payload.time}`;

    const formattedDate = new Date(appointmentDateTime)
      .toISOString()
      .substring(0, 19);

    payload.appointmentType = value.purpose; 
    payload.userId =  1; 
    payload.createdBy = this.userName;
    payload.appointmentDateTime = formattedDate;
     console.log("Booking Payload:", payload);

    this.bookingSuccessDetails = payload; // Store the payload for confirmation display
    this.bookingService.bookAppointment(payload)
      .subscribe({

        next: (res) => {
          this.confirmation = this.bookingSuccessDetails;
          this.confirmation.id = res.id; // Assuming the backend returns a confirmationId
        },

        error: (error) => {
          alert(error.message);
        }

      });
  }
}
  resetForm() {
    this.confirmation = null;
    this.bookingForm.patchValue({
      fullName: "",
      email: "",
      phone: "",
    });
  }

  printConfirmation() {
    window.print();
  }
}
function oninit() {
  throw new Error("Function not implemented.");
}

