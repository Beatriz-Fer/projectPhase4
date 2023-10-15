import { Retweet } from "./RetweetModel";
import { CommentPayload } from "./payloads/comment-payload";

export interface TweetModel{
    id: number;
    profilePic: string;
    firstName: string;
    lastName: string;
    username: string;
    tweetText: string;
    commentCounter: number;
    comments: CommentPayload[];
    retweetCounter: number;
    likeCounter: number;
    isLiked: boolean;
    isRetweeted: boolean;
    retweetedBy: Retweet;
    createdDate: Date;
    
}