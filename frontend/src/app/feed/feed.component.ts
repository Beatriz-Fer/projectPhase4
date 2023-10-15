import { Component, OnInit } from '@angular/core';
import { FormGroup, Validators, FormControl, AbstractControl } from '@angular/forms';
import { TweetService } from '../tweet.service';
import { Router } from '@angular/router';
import { HttpClient } from '@angular/common/http'
import { TweetModel } from '../TweetModel';
import { User } from '../UserModel';
import { AuthService } from '../auth/shared/auth.service';
import { CreateTweetPayload } from '../payloads/create-tweet.payload';


@Component({
  selector: 'app-feed',
  templateUrl: './feed.component.html',
  styleUrls: ['./feed.component.css']
})
export class FeedComponent implements OnInit {

  user!: User;
  id!: number;

  activeTweet: any = null;
  searchQuery: string = '';

  tweets$: Array<TweetModel>;

  isLoggedIn!: boolean;
  username!: string;

  createTweetForm!: FormGroup;
  createTweetPayload: CreateTweetPayload;

  imageSurce: any;
  errorMessage: string | null = null;

  currentPage!: number ;
  itemsPerPage!: number ;
  totalPages: number = 0; 

  constructor(private tweetService: TweetService, private router: Router, private http: HttpClient, private authService: AuthService) {  

    this.createTweetForm = new FormGroup({
      tweetText: new FormControl('', [Validators.required, tweetTextValidator])
    });

    this.tweets$ = new Array();

    this.createTweetPayload = {
      tweetType: '',
      text: ''
    }

    this.id = this.authService.getUserId();
  }


  ngOnInit(): void {
    this.authService.loggedIn.subscribe((data: boolean) => this.isLoggedIn = data);
    this.authService.username.subscribe((data: string) => this.username = data);
    this.username = this.authService.getUserName();

    this.currentPage = 1;
    this.itemsPerPage = 5; // total number of tweets displayed in feed page
    this.getTweets(this.currentPage, this.itemsPerPage);

    this.http.get<User>('http://localhost:8080/api/user/' + this.id).subscribe(
      (userData) => {
        this.user = userData;
      },
      (error) => {
        console.error('Error fetching user data', error);
      }
    )

  }


  // logging out - user is brought to login page
  logout() {
  this.authService.logout();
  this.isLoggedIn = false;
  this.router.navigate(['/login']);
  }


  // tweets appear in feed page in pages of 5 tweets each
  private getTweets(page: number, pageSize: number) {
    const self = this;
    this.tweetService.getPaginatedFeed(page, pageSize).subscribe({
      next(data) {
        if (data && Array.isArray(data)) {
          self.tweets$ = data;
          self.totalPages = (data.length);
        } else {
          // Case where data is not in expected format
          console.error('Invalid data format:', data);
        }
      },
      error(err) {
        // Handling errors if necessary
        console.error('Error fetching tweets:', err);
      }
    });
  }


  // next page of the feed
  nextPage() {
    if (this.currentPage <= this.totalPages) {
      this.currentPage++;
      this.getTweets(this.currentPage, this.itemsPerPage);
    }
  }
  

  // previous page of the feed
  previousPage() {
    if (this.currentPage > 1) {
      this.currentPage--;
      this.getTweets(this.currentPage, this.itemsPerPage);
    }
  }


  //search tweets in the feed page
  searchTweets() {
    // Perform the search based on the searchQuery
    const filteredTweets = this.tweets$.filter((tweet) => {
      const lowerCaseQuery = this.searchQuery.toLowerCase();
      const lowerCaseContent = tweet.tweetText.toLowerCase();
      const lowerCaseAuthor = tweet.username.toLowerCase();
      return lowerCaseContent.includes(lowerCaseQuery) || lowerCaseAuthor.includes(lowerCaseQuery);
    });

    // Update the tweets with the filtered results
    this.tweets$ = filteredTweets;

    // Reset the search if search query is empty
    if (this.searchQuery === '') {
      this.resetSearch();
    }
  }

  // reset the search
  resetSearch() {
    this.searchQuery = '';
    this.getTweets(this.currentPage, this.itemsPerPage);
  }


  // create a new tweet for feed page
  createTweet() {

  this.createTweetPayload.tweetType = 'TWEET_TYPE';
  this.createTweetPayload.text = this.createTweetForm.get('tweetText')!.value;

  const self = this;
  this.tweetService.createTweet(this.createTweetPayload).subscribe({
    next(response) {
      console.log(response)
      self.createTweetForm.reset();
      self.router.navigateByUrl("/feed");
    },
    complete() { window.location.reload(); },
    error(error) {
      if (error.status === 400){
        // making sure the same tweet is not being created again
        self.errorMessage = "You have already tweeted this text."
      } else {
          console.log(error);
      }
    }
  })

  }


  // when the liked button is clicked
  toggleLike(tweet: TweetModel) {
    this.tweetService.like(tweet.id).subscribe({
      next(response) {
        console.log(response)
        if (tweet.retweetedBy){
          tweet.retweetedBy.isLiked = !tweet.retweetedBy.isLiked;
          tweet.retweetedBy.likeCounter += tweet.retweetedBy.isLiked ? 1 : -1;
        } else{
           tweet.isLiked = !tweet.isLiked;
           tweet.likeCounter += tweet.isLiked ? 1 : -1;
        } 
      },
      complete() { },
      error(error) {
        console.log(error)
      }
    }) 
  }


  // comment pop up box
  toggleTextBox(tweet: any) {
      if (this.activeTweet === tweet) {
        this.activeTweet = null;
      } else {
        this.activeTweet = tweet;
      }
    }

  closePopup() {
    this.activeTweet = null;
  }

    

  //submitting a comment for specific tweet
  submitText(tweet: TweetModel) {
    const inputtedComment = this.activeTweet.commentText;

    this.http.post(`http://localhost:8080/api/tweet/${tweet.id}/comment`, inputtedComment).subscribe(
      (response) => {
        const updatedTweet: TweetModel = response as TweetModel;
        tweet.comments = updatedTweet.comments; // Update the comments array in the original tweet
        // Clear the comment text field
        this.activeTweet.commentText = '';
        this.refreshComments(tweet);
      },
      (error) => {
        console.error('Error saving comment:', error);
      }
    );
    window.location.reload();
  }
    

  // refreshing the comments and retrieving them
  refreshComments(tweet: TweetModel){
    this.tweetService.commentList(tweet.id).subscribe(
      (commentList) => {
        tweet.comments = commentList;
      },
      (error) => {
        console.error('Error retrieving comments', error);
      }
    )
  }


  // creating a retweet/ repost of a normal tweet
  retweet(tweet: TweetModel) {
   this.tweetService.retweet(tweet.id).subscribe({
      next(response) {
        console.log(response)
        tweet.isRetweeted = true;
      },
      complete() { },
      error(error) {
        console.log(error)
      }
    }) 
    window.location.reload();
  }

}

// Validation function for tweet creation max length 100 
function tweetTextValidator(control: AbstractControl): { [key: string]: boolean } | null {
  const value = control.value;
  const maxLength = 100;
  return value.length <= maxLength ? null : { characterLimitExceeded: true};
}