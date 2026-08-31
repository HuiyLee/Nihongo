import { apiClient } from './axiosClient';
import type { ApiResponse, ListParams, PageResponse } from '../types/api';
import type {
  AdminExam,
  Exam,
  ExamAttempt,
  ExamRequest,
  ExamResult,
  SubmitExamRequest,
} from '../types/exam';

/**
 * Hand-written rather than createContentApi: the admin and public shapes
 * genuinely differ (AdminExam nests AdminExamQuestion -> AdminExercise with
 * isCorrect; Exam is a flat, question-free summary), and there's the
 * start/submit/result attempt flow besides plain CRUD.
 */
export const examApi = {
  listAdmin(params: ListParams) {
    return apiClient.get<ApiResponse<PageResponse<AdminExam>>>('/admin/exams', { params });
  },
  getAdmin(id: number) {
    return apiClient.get<ApiResponse<AdminExam>>(`/admin/exams/${id}`);
  },
  create(payload: ExamRequest) {
    return apiClient.post<ApiResponse<AdminExam>>('/admin/exams', payload);
  },
  update(id: number, payload: ExamRequest) {
    return apiClient.put<ApiResponse<AdminExam>>(`/admin/exams/${id}`, payload);
  },
  remove(id: number) {
    return apiClient.delete<ApiResponse<null>>(`/admin/exams/${id}`);
  },
  listPublic(params: ListParams) {
    return apiClient.get<ApiResponse<PageResponse<Exam>>>('/exams', { params });
  },
  getPublic(id: number) {
    return apiClient.get<ApiResponse<Exam>>(`/exams/${id}`);
  },
  start(id: number) {
    return apiClient.post<ApiResponse<ExamAttempt>>(`/exams/${id}/start`);
  },
  submit(id: number, payload: SubmitExamRequest) {
    return apiClient.post<ApiResponse<ExamResult>>(`/exams/${id}/submit`, payload);
  },
  getResult(id: number) {
    return apiClient.get<ApiResponse<ExamResult>>(`/exams/${id}/result`);
  },
};
