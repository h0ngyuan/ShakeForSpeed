export interface Activity {
  id: string;
  name: string;
  creator: string;
  createTime: string;
  winners: {
    rank: number;
    name: string;
    reward: string;
  }[];
  reward: string;
}