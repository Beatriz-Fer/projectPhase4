export interface Tweet {
  profilePic: string;
  author: string;
  email: string;
  content: string;
  timestamp: Date;
  likes: number;
  isLiked?: boolean;
  comments?: string[];
  commentDate?: Date;
  commentText?: string; 
  isCommented?: boolean; 
  commentCount: number;
  originalAuthor?: string;
  originalDate?: Date;
  retweets: number; 
}