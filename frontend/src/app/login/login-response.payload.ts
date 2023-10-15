export interface LoginResponse {
    authenticationToken: string;
    refreshToken: string;
    id: number;
    username:string;
    expiresAt: Date;
}