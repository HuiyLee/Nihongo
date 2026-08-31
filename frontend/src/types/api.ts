/** Mirrors the backend's standard response envelope (requirements section 6). */
export interface ApiResponse<T> {
  status: 'SUCCESS' | 'ERROR';
  message: string;
  data: T;
}

/** Mirrors the backend's pagination envelope (requirements section 26). */
export interface PageResponse<T> {
  content: T[];
  page: number;
  size: number;
  totalElements: number;
  totalPages: number;
}
