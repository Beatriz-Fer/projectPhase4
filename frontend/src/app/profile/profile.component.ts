import { Component, OnInit, OnDestroy } from '@angular/core';
import { FormGroup, FormControl } from '@angular/forms';
import { User } from '../UserModel';
import { AuthService } from '../auth/shared/auth.service';
import { UserService } from '../user.service';
import { HttpClient } from '@angular/common/http';
import { TweetModel } from '../TweetModel';
import { ToastrService } from 'ngx-toastr';
import { LocalStorageService } from 'ngx-webstorage';


@Component({
  selector: 'app-profile',
  templateUrl: './profile.component.html',
  styleUrls: ['./profile.component.css']
})
export class ProfileComponent implements OnInit, OnDestroy{

  user!: User;
  id!: number;

  userTweets$: Array<TweetModel>;
  userRetweets$: Array<TweetModel>;
  userLikedTweets$: Array<TweetModel>;

  allUsers$: Array<User>;

  updateForm!: FormGroup;

  selectedFile: File | null = null;

  showUserTweets: boolean = true;
  showUserLikedTweets: boolean = false;
  showUserRetweets: boolean = false;

  photoString: string;
  activeLabel: string = 'tweets';
  followingCount = 500;
  showPopup: boolean = false;

  constructor(private userService: UserService, private authService: AuthService, 
    private http: HttpClient, private toastr: ToastrService, 
    private localStorage: LocalStorageService){

    this.userTweets$ = new Array();
    this.userRetweets$ = new Array();
    this.userLikedTweets$ = new Array();

    this.allUsers$ = new Array();

    this.id = this.authService.getUserId();
    this.photoString = ""; 
  }
  
  ngOnInit() { 
    // get user infromation
    this.http.get<User>('http://localhost:8080/api/user/' + this.id).subscribe(
      (userData) => {
        this.user = userData;
        this.localStorage.store('userPhoto', userData.profilePic);
      },
      (error) => {
        console.error('Error fetching user data', error);
      }
    )

    // get specified user tweets
    this.userService.getUserTweets(this.id).subscribe((tweets) => {
      this.userTweets$ = tweets;
    });

    // get specified user retweets
    this.userService.getUserRetweets(this.id).subscribe((tweets) => {
      this.userRetweets$ = tweets;
    });

    // get specified user liked tweets
    this.userService.getUserLikedTweets(this.id).subscribe((tweets) => {
      this.userLikedTweets$ = tweets;
    });

    // get all other users apart from logged in one - to show on who to follow list
    this.userService.getAllUsers().subscribe((userList) => {
      this.allUsers$ = userList.filter(selectedUser => selectedUser.username !== this.user.username);
    });

    this.updateForm = new FormGroup({
      firstName: new FormControl(''),
      lastName: new FormControl(''),
      bio: new FormControl('')
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
  window.location.reload();
}


// Edit pop up box
openPopup() {
  this.showPopup = true;
}

// select a profile image from files
onFileSelected(event: any) {
  const file: File = event.target.files[0];
  if (file) {
    const reader = new FileReader();
    reader.onload = (e: any) => {
      this.user.profilePic = e.target.result;
    };
    reader.readAsDataURL(file);
  }
}

closePopup() {
  this.showPopup = false;
}


//save new editted changes to user profile
saveChanges() {

  this.user.firstName = this.updateForm?.get('firstName')?.value;
  this.user.lastName = this.updateForm?.get('lastName')?.value;
  this.user.bio = this.updateForm?.get('bio')?.value;
  window.location.reload();

  const self = this;
    this.userService.updateUser(this.id, this.user).subscribe(
      (reponse) => {
        console.log('Profile updated successfully', reponse);
        self.toastr.success('Login Successful');
      },
      (error) => {
        self.toastr.error("Unable to apply changes");
        console.error('Error updating profile', error);
      }
    );
  this.showPopup = false;
}


// follow button to increment or decrement following number
  toggleFollow(user: User): void {
  user.followed = !user.followed;
  this.followingCount += user.followed ? 1: -1;
}

isFollowed(user: User): boolean {
  return user.followed;
}


// change content at bottom of page
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
  
