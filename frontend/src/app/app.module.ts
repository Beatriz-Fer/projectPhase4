import { NgModule } from '@angular/core';
import { BrowserModule } from '@angular/platform-browser';
import { RouterModule, Routes } from '@angular/router';
import { FormsModule } from "@angular/forms";
import { ReactiveFormsModule } from '@angular/forms';

import { AppComponent } from './app.component';

import { LoginComponent } from './login/login.component';
import { FeedComponent } from './feed/feed.component';
import { RegisterComponent } from './register/register.component';
import { ProfileComponent } from './profile/profile.component';
import { FollowerProfileComponent } from './follower-profile/follower-profile.component';

import { HttpClientModule, HTTP_INTERCEPTORS } from "@angular/common/http";
import { NgxWebstorageModule } from 'ngx-webstorage';
import { BrowserAnimationsModule } from '@angular/platform-browser/animations';
import { ToastrModule } from 'ngx-toastr';
import { TokenInterceptor } from './token-interceptor';
import { AuthGuard } from './auth/shared/auth.guard';
import { DatePipe } from '@angular/common';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'feed', component: FeedComponent },
  { path: 'profile', component: ProfileComponent },
  { path: 'follower-profile', component: FollowerProfileComponent },
  { path: '', redirectTo: '/register',  pathMatch: 'full'},
  { path: '**', redirectTo: '/register' } // Handle invalid routes
];

@NgModule({
  declarations: [
    AppComponent,
    LoginComponent,
    RegisterComponent,
    FeedComponent,
    ProfileComponent,
    FollowerProfileComponent
  ],
  imports: [
    BrowserModule,
    RouterModule.forRoot(routes),
    ReactiveFormsModule,
    FormsModule,
    HttpClientModule,
    NgxWebstorageModule.forRoot(),
    BrowserAnimationsModule,
    ToastrModule.forRoot()
  ],
  providers: [
    DatePipe,
    AuthGuard,
    {
      provide: HTTP_INTERCEPTORS,
      useClass: TokenInterceptor,
      multi: true
    }
  ],
  bootstrap: [AppComponent]
})
export class AppModule { }
