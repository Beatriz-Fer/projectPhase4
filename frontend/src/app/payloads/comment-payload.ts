import { User } from "../UserModel";
import { Tweet } from "../tweet";

export interface CommentPayload {
    user: User ;
    tweet: Tweet;
    firstName: string;
    lastName: string;
    username: string;
    text: string;
    createdDate: Date;
}