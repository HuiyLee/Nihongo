import { apiClient } from './axiosClient';
import type { ApiResponse, ListParams, PageResponse } from '../types/api';
import type {
  AdminExercise,
  Exercise,
  ExerciseRequest,
  ExerciseType,
  SubmitExerciseRequest,
  SubmitExerciseResponse,
} from '../types/exercise';

export interface ExerciseListParams extends ListParams {
  type?: ExerciseType;
}

/**
 * Hand-written rather than createContentApi, because the admin and public
 * shapes genuinely differ here (AdminExercise carries isCorrect on every
 * answer, Exercise never does) and there's a submit endpoint besides.
 */
export const exerciseApi = {
  listAdmin(params: ExerciseListParams) {
    return apiClient.get<ApiResponse<PageResponse<AdminExercise>>>('/admin/exercises', { params });
  },
  getAdmin(id: number) {
    return apiClient.get<ApiResponse<AdminExercise>>(`/admin/exercises/${id}`);
  },
  create(payload: ExerciseRequest) {
    return apiClient.post<ApiResponse<AdminExercise>>('/admin/exercises', payload);
  },
  update(id: number, payload: ExerciseRequest) {
    return apiClient.put<ApiResponse<AdminExercise>>(`/admin/exercises/${id}`, payload);
  },
  remove(id: number) {
    return apiClient.delete<ApiResponse<null>>(`/admin/exercises/${id}`);
  },
  listPublic(params: ExerciseListParams) {
    return apiClient.get<ApiResponse<PageResponse<Exercise>>>('/exercises', { params });
  },
  getPublic(id: number) {
    return apiClient.get<ApiResponse<Exercise>>(`/exercises/${id}`);
  },
  submit(id: number, payload: SubmitExerciseRequest) {
    return apiClient.post<ApiResponse<SubmitExerciseResponse>>(`/exercises/${id}/submit`, payload);
  },
};
