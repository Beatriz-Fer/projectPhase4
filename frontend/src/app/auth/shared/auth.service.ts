import { Injectable, Output, EventEmitter} from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { RegisterRequestPayload } from 'src/app/register/register-request.payload';
import { Observable } from 'rxjs';
import { LocalStorageService } from 'ngx-webstorage';
import { LoginRequestPayload } from 'src/app/login/login-request.payload';
import { LoginResponse } from 'src/app/login/login-response.payload';
import { map, tap } from 'rxjs/operators';
import { EmailResponsePayload } from 'src/app/register/email-reponse.payload';

@Injectable({
  providedIn: 'root'
})
export class AuthService {

  @Output() loggedIn: EventEmitter<boolean> = new EventEmitter();
  @Output() username: EventEmitter<string> = new EventEmitter();

  refreshTokenPayload = {
    refreshToken: this.getRefreshToken(),
    username: this.getUserName()
  }

  constructor(private httpClient: HttpClient,
    private localStorage: LocalStorageService) { }

    // perform registration
  register(registerRequestPayload : RegisterRequestPayload): Observable<any>{
    return this.httpClient.post('http://localhost:8080/api/user/register', registerRequestPayload, { responseType: 'text'});
  }

  // perform login and authentication
  login(loginRequestPayload: LoginRequestPayload): Observable<boolean> {
    return this.httpClient.post<LoginResponse>('http://localhost:8080/api/user/login',
      loginRequestPayload).pipe(map(data => {
        this.localStorage.store('authenticationToken', data.authenticationToken);
        this.localStorage.store('id', data.id);
        this.localStorage.store('username', data.username);
        this.localStorage.store('refreshToken', data.refreshToken);
        this.localStorage.store('expiresAt', data.expiresAt);

        this.loggedIn.emit(true);
        this.username.emit(data.username);
        return true;
      }));
  }

  // this gets the JWT Token
  getJwtToken() {
    return this.localStorage.retrieve('authenticationToken');
  }

  // token is refreshed
  refreshToken() {
    return this.httpClient.post<LoginResponse>('http://localhost:8080/api/user/refresh-token',
      this.refreshTokenPayload)
      .pipe(tap(response => {
        this.localStorage.clear('authenticationToken');
        this.localStorage.clear('expiresAt');
        this.localStorage.store('authenticationToken',
          response.authenticationToken);
        this.localStorage.store('expiresAt', response.expiresAt);
      }));
  }

  // enables user to logout 
  logout() {
    const self = this;
    return this.httpClient.post("http://localhost:8080/api/user/logout", {username: this.getUserName(), refreshToken: this.localStorage.retrieve("refreshToken")}).subscribe({complete(){
    self.localStorage.clear('authenticationToken');
    self.localStorage.clear('username');
    self.localStorage.clear('refreshToken');
    self.localStorage.clear('expiresAt');
  }});
  }
  // gets the username
  getUserName() {
    return this.localStorage.retrieve('username');
  }
  // retriveed the refresh token
  getRefreshToken() {
    return this.localStorage.retrieve('refreshToken');
  }
  // checks to see if user is logged in
  isLoggedIn(): boolean {
    return this.getJwtToken() != null;
  }
  // gets the user id
  getUserId() {
    return this.localStorage.retrieve('id');
  }
  // checks to see if email has already been used or not
  checkEmailUniqueness(requestDto: RegisterRequestPayload):Observable<any>{
    return this.httpClient.post<EmailResponsePayload>('http://localhost:8080/api/user/check-email-unique', requestDto);
  }
  
}
