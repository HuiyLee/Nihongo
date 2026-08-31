import { apiClient } from './axiosClient';
import type { ApiResponse } from '../types/api';
import type { Level, LevelRequest } from '../types/content';

export const levelApi = {
  listPublic() {
    return apiClient.get<ApiResponse<Level[]>>('/levels');
  },
  listAdmin() {
    return apiClient.get<ApiResponse<Level[]>>('/admin/levels');
  },
  create(payload: LevelRequest) {
    return apiClient.post<ApiResponse<Level>>('/admin/levels', payload);
  },
  update(id: number, payload: LevelRequest) {
    return apiClient.put<ApiResponse<Level>>(`/admin/levels/${id}`, payload);
  },
  remove(id: number) {
    return apiClient.delete<ApiResponse<null>>(`/admin/levels/${id}`);
  },
};
