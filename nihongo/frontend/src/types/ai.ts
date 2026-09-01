/** Requirements section 38, Phase 7 (optional AI features). */

export interface GrammarExplanationRequest {
  grammarId?: number;
  question?: string;
}

export interface GrammarExplanationResponse {
  pattern: string | null;
  explanation: string;
}

export interface WritingCorrectionRequest {
  text: string;
}

export interface WritingCorrectionResponse {
  original: string;
  corrected: string;
  feedback: string;
}

export interface ConversationMessage {
  role: 'user' | 'assistant';
  content: string;
}

export interface ConversationRequest {
  level?: string;
  messages: ConversationMessage[];
}

export interface ConversationResponse {
  reply: string;
}
