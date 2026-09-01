import { apiClient } from './axiosClient';
import type { ApiResponse } from '../types/api';
import type {
  ConversationRequest,
  ConversationResponse,
  GrammarExplanationRequest,
  GrammarExplanationResponse,
  WritingCorrectionRequest,
  WritingCorrectionResponse,
} from '../types/ai';

/** Requirements section 38, Phase 7 (optional AI features). */
export const aiApi = {
  explainGrammar(payload: GrammarExplanationRequest) {
    return apiClient.post<ApiResponse<GrammarExplanationResponse>>(
      '/ai/grammar-explanation',
      payload
    );
  },
  correctWriting(payload: WritingCorrectionRequest) {
    return apiClient.post<ApiResponse<WritingCorrectionResponse>>(
      '/ai/writing-correction',
      payload
    );
  },
  converse(payload: ConversationRequest) {
    return apiClient.post<ApiResponse<ConversationResponse>>('/ai/conversation', payload);
  },
};
