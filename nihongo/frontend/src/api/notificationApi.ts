import { apiClient } from './axiosClient';
import type { ApiResponse, ListParams, PageResponse } from '../types/api';
import type { Notification } from '../types/notification';

/** Requirements section 24. Always scoped to the caller. */
export const notificationApi = {
  list(params: ListParams) {
    return apiClient.get<ApiResponse<PageResponse<Notification>>>('/notifications', { params });
  },
  unreadCount() {
    return apiClient.get<ApiResponse<{ count: number }>>('/notifications/unread-count');
  },
  markRead(id: number) {
    return apiClient.post<ApiResponse<Notification>>(`/notifications/${id}/read`);
  },
  markAllRead() {
    return apiClient.post<ApiResponse<null>>('/notifications/read-all');
  },
};
