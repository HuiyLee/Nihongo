import { apiClient } from './axiosClient';
import type { ApiResponse, ListParams, PageResponse } from '../types/api';
import type { Bookmark, BookmarkRequest, BookmarkTargetType } from '../types/learning';

export interface BookmarkListParams extends ListParams {
  targetType?: BookmarkTargetType;
}

/** Requirements section 23 / BR-011. Always scoped to the caller by the backend. */
export const bookmarkApi = {
  list(params: BookmarkListParams) {
    return apiClient.get<ApiResponse<PageResponse<Bookmark>>>('/bookmarks', { params });
  },
  create(payload: BookmarkRequest) {
    return apiClient.post<ApiResponse<Bookmark>>('/bookmarks', payload);
  },
  remove(id: number) {
    return apiClient.delete<ApiResponse<null>>(`/bookmarks/${id}`);
  },
  exists(targetType: BookmarkTargetType, targetId: number) {
    return apiClient.get<ApiResponse<boolean>>('/bookmarks/exists', {
      params: { targetType, targetId },
    });
  },
};
