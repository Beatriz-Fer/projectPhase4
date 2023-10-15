import { Component, OnInit } from '@angular/core';
import { FormGroup, FormControl, Validators, AbstractControl } from '@angular/forms';
import { Router, ActivatedRoute } from '@angular/router';
import { AuthService } from '../auth/shared/auth.service';
import { LoginRequestPayload } from './login-request.payload';
import { ToastrService } from 'ngx-toastr';


@Component({
  selector: 'app-login',
  templateUrl: './login.component.html',
  styleUrls: ['./login.component.css']
})
export class LoginComponent implements OnInit{

  loginForm!: FormGroup;
  loginRequestPayload: LoginRequestPayload;
  isError!: boolean;
  registrationSuccess!: boolean;
  registerSuccessMessage!: string;
  
  constructor(private authService: AuthService, private toastr: ToastrService,
    private router: Router, private route: ActivatedRoute) {

    this.loginRequestPayload = {
      username: '',
      password: ''
    }
  }

  ngOnInit(): void {
    this.loginForm = new FormGroup({
      username: new FormControl('', Validators.required),
      password: new FormControl('', [Validators.required, passwordValidator])
    });

    // checks to see if user has usccessful registration
    this.route.queryParams
      .subscribe(params => {
        if ((params['registered'] !== undefined) && (params['registered'] === 'true')) {
          this.toastr.success('Registered Successfully');
          this.registrationSuccess = true;
        }
      });
  }


  // user login - checks username and password
  login() {
    const self = this;
    this.loginRequestPayload.username = this.loginForm?.get('username')?.value;
    this.loginRequestPayload.password = this.loginForm?.get('password')?.value;

    this.authService.login(this.loginRequestPayload).subscribe({
      next(response){
        console.log(response);
        // login success message can be seen
        self.toastr.success('Login Successful');
        self.router.navigate(["/feed"], {queryParams: {signedIn: "true"}});

      },
      complete(){},
      error(error){
        self.toastr.error("Wrong Username or Password");
        console.log(error)
      }
    });
  }

}

// Validation function for password complexity
function passwordValidator(control: AbstractControl): { [key: string]: boolean } | null {
  const passwordPattern = /^(?=.*[!@#$%^&*()_+\-=[\]{};':"\\|,.<>/?])(?=.*[a-z])(?=.*[A-Z]).{8,12}$/;
  if (control.value && (!passwordPattern.test(control.value) || control.value.length < 8 || control.value.length > 12)) {
    return { 'invalidPassword': true };
  }
  return null;
}