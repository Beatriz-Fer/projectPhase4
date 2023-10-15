import { Component, OnInit } from '@angular/core';
import { Router } from '@angular/router';
import { FormGroup, AbstractControl, Validators, FormBuilder } from '@angular/forms';
import { RegisterRequestPayload } from './register-request.payload';
import { AuthService } from '../auth/shared/auth.service';
import { ToastrService } from 'ngx-toastr';
import { debounceTime, distinctUntilChanged } from 'rxjs';
import { EmailResponsePayload } from './email-reponse.payload';


@Component({
  selector: 'app-register',
  templateUrl: './register.component.html',
  styleUrls: ['./register.component.css']
})
export class RegisterComponent implements OnInit {

  registerRequestPayload: RegisterRequestPayload;
  registerForm!: FormGroup;
  registrationAllowed = false;

  constructor(private authService: AuthService, private toastr: ToastrService, private router: Router,
    private formBuilder : FormBuilder) {

    this.registerRequestPayload =  {
      firstName: '',
      lastName:'',
      username:'',
      email:'',
      password:''
    };
  }

  ngOnInit() {
    this.registerForm = this.formBuilder.group({
      firstName: ['', [Validators.required, nameValidator]],
      lastName: ['', [Validators.required, nameValidator]],
      username:['', [Validators.required]],
      email:['', [Validators.required, emailValidator]],
      password: ['', [Validators.required, passwordValidator]]
    });

    this.registerForm.get('email')?.valueChanges
    .pipe(
      debounceTime(500),
      distinctUntilChanged()
      )
    .subscribe(() => {
        this.checkEmailUniqueness();
    });
  }


  // check to see if email already exists
  checkEmailUniqueness(){
    const emailControl = this.registerForm.get('email');

    if(emailControl?.valid){
      const email = emailControl.value;

      const requestDto: RegisterRequestPayload = {
        firstName: this.registerForm?.get('firstName')?.value,
        lastName: this.registerForm?.get('lastName')?.value,
        username: this.registerForm?.get('username')?.value,
        email: email,
        password: this.registerForm?.get('password')?.value
       }
       
        this.authService.checkEmailUniqueness(requestDto).subscribe(
          (response: EmailResponsePayload) => {
            if (response.emailUnique) {
              this.registerForm.get('email')?.setErrors(null);
              console.log("unique - no error");
            } else {
              this.registerForm.get('email')?.setErrors({ notUniqueEmail: true });
              console.log("used - has error");
            }
          },
          error => {
            console.error(error);
          }
        );
      }
    }
  
  

  // register the user
  register() {

    if (this.registerForm.valid){

    this.registerRequestPayload.firstName = this.registerForm?.get('firstName')?.value;
    this.registerRequestPayload.lastName = this.registerForm?.get('lastName')?.value;
    this.registerRequestPayload.username = this.registerForm?.get('username')?.value;
    this.registerRequestPayload.email = this.registerForm?.get('email')?.value;
    this.registerRequestPayload.password = this.registerForm?.get('password')?.value;

    this.authService.register(this.registerRequestPayload)
      .subscribe(data => {
        this.router.navigate(['/login'],
        { queryParams: { registered: 'true' } });
      }, error => {
        console.log(error);
        this.toastr.error('Registration Failed! Please try again. Username already taken');
      });

    }
  }

}


 // Validation function for name length
 function nameValidator(control: AbstractControl): { [key: string]: boolean } | null {
  if (control.value && control.value.length > 20) {
    return { 'nameLength': true };
  }
  return null;
}

// Validation function for email format
function emailValidator(control: AbstractControl): { [key: string]: boolean } | null {
  const emailPattern = /^[a-zA-Z0-9._%+-]+@[a-zA-Z0-9.-]+\.[a-zA-Z]{2,}$/;
  if (control.value && !emailPattern.test(control.value)) {
    return { 'invalidEmail': true };
  }
  return null;
}

// Validation function for password complexity
function passwordValidator(control: AbstractControl): { [key: string]: boolean } | null {
  const passwordPattern = /^(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?])(?=.*[a-z])(?=.*[A-Z]).{8,12}$/;
  if (control.value && (!passwordPattern.test(control.value) || control.value.length < 8 || control.value.length > 12)) {
    return { 'invalidPassword': true };
  }
  return null;
}