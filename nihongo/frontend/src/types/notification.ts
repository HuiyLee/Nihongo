/** Requirements section 24. */
export type NotificationType =
  'NEW_LESSON' | 'NEW_EXAM' | 'VOCABULARY_REVIEW' | 'STREAK' | 'SYSTEM';

export interface Notification {
  id: number;
  title: string;
  content?: string;
  type: NotificationType;
  read: boolean;
  createdAt: string;
}
