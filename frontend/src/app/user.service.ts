import { Injectable } from '@angular/core';
import { HttpClient } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TweetModel } from './TweetModel';
import { User } from './UserModel';


@Injectable({
  providedIn: 'root'
})
export class UserService {

  constructor(private http: HttpClient) {
  }

  // getting the user tweets
  getUserTweets(id: number): Observable<TweetModel[]> {
    return this.http.get<TweetModel[]>("http://localhost:8080/api/user/userTweets/" + id);
  }

  // getting the user retweets
  getUserRetweets(id: number): Observable<TweetModel[]> {
    return this.http.get<TweetModel[]>("http://localhost:8080/api/user/userRetweets/" + id);
  }

  // getting the user liked tweets
  getUserLikedTweets(id: number): Observable<TweetModel[]> {
    return this.http.get<TweetModel[]>("http://localhost:8080/api/user/userLikedTweets/" + id);
  }

  // updating the user information
  updateUser(id: number, user: User) {
    return this.http.put("http://localhost:8080/api/user/" + id, user);
  }

  // retrieving user information
  getUser(id: number){
    return this.http.get<User>('http://localhost:8080/api/user/' + id);
  }

  // getting all the users
  getAllUsers(): Observable<User[]>{
    return this.http.get<User[]>('http://localhost:8080/api/user/userList');
  }

}