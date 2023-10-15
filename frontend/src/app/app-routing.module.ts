import { NgModule } from '@angular/core';
import { RouterModule, Routes } from '@angular/router';
import { LoginComponent } from './login/login.component';
import { RegisterComponent } from './register/register.component';
import { FeedComponent } from './feed/feed.component';
import { ProfileComponent } from './profile/profile.component';
import { FollowerProfileComponent } from './follower-profile/follower-profile.component';

const routes: Routes = [
  { path: 'register', component: RegisterComponent },
  { path: 'login', component: LoginComponent },
  { path: 'feed', component: FeedComponent},
  { path: 'profile', component: ProfileComponent },
  { path: 'follower-profile/:id', component: FollowerProfileComponent }
];

@NgModule({
  imports: [RouterModule.forRoot(routes)],
  exports: [RouterModule]
})
export class AppRoutingModule { }