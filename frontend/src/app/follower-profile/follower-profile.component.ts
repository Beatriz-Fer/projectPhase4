import { Component, OnInit, OnDestroy } from '@angular/core';
import { ActivatedRoute } from '@angular/router';
import { UserService } from '../user.service';
import { User } from '../UserModel';
import { TweetModel } from '../TweetModel';

@Component({
  selector: 'app-follower-profile',
  templateUrl: './follower-profile.component.html',
  styleUrls: ['./follower-profile.component.css']
})
export class FollowerProfileComponent implements OnInit, OnDestroy {

  id!: number;
  user!: User;

  showUserTweets: boolean = true;
  showUserLikedTweets: boolean = false;
  showUserRetweets: boolean = false;

  activeLabel: string = 'tweets';
  followingCount = 500;

  userTweets$: Array<TweetModel>;
  userRetweets$: Array<TweetModel>;
  userLikedTweets$: Array<TweetModel>;

constructor(private route: ActivatedRoute, private userService: UserService) {
  this.userTweets$ = new Array();
  this.userRetweets$ = new Array();
  this.userLikedTweets$ = new Array();
}

ngOnInit() {
 this.route.params.subscribe(params => {
  this.id = params['id'];
  this.userService.getUser(this.id).subscribe(user => {
    this.user = user;
  })
 })

 // retrieving user tweets
  this.userService.getUserTweets(this.id).subscribe((tweets) => {
    this.userTweets$ = tweets;
  });

  // retrieving user retweets
  this.userService.getUserRetweets(this.id).subscribe((tweets) => {
    this.userRetweets$ = tweets;
  });

  // retrieving user liked tweets
  this.userService.getUserLikedTweets(this.id).subscribe((tweets) => {
    this.userLikedTweets$ = tweets;
  });

  // Following
  const storedFollowedState = localStorage.getItem('followedState');
  const storedFollowingCount = localStorage.getItem('followingCount');

  if (storedFollowedState) {
    this.user.followed = JSON.parse(storedFollowedState);
  }

  if (storedFollowingCount) {
    this.followingCount = Number(storedFollowingCount);
  }

}


ngOnDestroy(): void {
  // Save followed state and following count to browser storage
  localStorage.setItem('followedState', JSON.stringify(this.user.followed));
  localStorage.setItem('followingCount', this.followingCount.toString());
}

// follow button
toggleFollow(user: User): void {
  user.followed = !user.followed;
  this.followingCount += user.followed ? 1: -1;
}

isFollowed(user: User): boolean {
  return user.followed;
}

// change bottom section of page view
changeContent(label: string) {
  this.activeLabel = label;
}

showTweets() {
  this.showUserTweets = true;
  this.showUserLikedTweets = false;
  this.showUserRetweets = false;
}

showLikedTweets() {
  this.showUserTweets = false;
  this.showUserLikedTweets = true;
  this.showUserRetweets = false;
}

showRetweets() {
  this.showUserTweets = false;
  this.showUserLikedTweets = false;
  this.showUserRetweets = true;
}

}
