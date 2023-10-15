import { Injectable } from '@angular/core';
import { HttpClient, HttpParams } from '@angular/common/http';
import { Observable } from 'rxjs';
import { TweetModel } from './TweetModel';
import { CreateTweetPayload } from './payloads/create-tweet.payload';
import { CommentPayload } from './payloads/comment-payload';

@Injectable({
  providedIn: 'root'
})
export class TweetService {

  constructor(private http: HttpClient) {
  }

  // get tweets for feed page, seperated by pages
  getPaginatedFeed(page: number, pageSize: number): Observable<TweetModel[]> {
    const params = new HttpParams()
      .set('page', page.toString())
      .set('pageSize', pageSize.toString());
    return this.http.get<TweetModel[]>(`http://localhost:8080/api/feed`, { params });
  }

  // tweet creation
  createTweet(createTweetPayload: CreateTweetPayload) {
    return this.http.post('http://localhost:8080/api/tweet', createTweetPayload);
  }

  // liking a tweet
  like(id: number){
    return this.http.post("http://localhost:8080/api/tweet/" + id + "/like", {id: id})
  }
  isLiked(): Observable<TweetModel[]> {
    return this.http.get<TweetModel[]>("http://localhost:8080/api/tweet/is-liked");
  }

  // retweeting a tweet
  retweet(id: number) {
    return this.http.post("http://localhost:8080/api/tweet/" + id + "/share", {id: id});
  }
  isRetweeted(): Observable<TweetModel[]> {
    return this.http.get<TweetModel[]>("http://localhost:8080/api/tweet/is-retweeted");
  }

  // creating a comment
  comment(id: number, commentPayload: CommentPayload) {
    return this.http.post("http://localhost:8080/api/tweet" + id + "/comment", commentPayload);
  }

  // retrieving comment list
  commentList(id: number) {
    return this.http.get<CommentPayload[]>("http://localhost:8080/api/tweet" + id + "/comment-list");
  }

}