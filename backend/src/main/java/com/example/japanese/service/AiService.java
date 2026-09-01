package com.example.japanese.service;

import com.example.japanese.dto.request.ConversationRequest;
import com.example.japanese.dto.request.GrammarExplanationRequest;
import com.example.japanese.dto.request.WritingCorrectionRequest;
import com.example.japanese.dto.response.ConversationResponse;
import com.example.japanese.dto.response.GrammarExplanationResponse;
import com.example.japanese.dto.response.WritingCorrectionResponse;
import com.example.japanese.entity.Grammar;
import com.example.japanese.exception.InvalidRequestException;
import com.example.japanese.exception.ResourceNotFoundException;
import com.example.japanese.repository.GrammarRepository;
import com.example.japanese.service.ai.AnthropicClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.util.List;
import java.util.stream.Collectors;

/**
 * Requirements section 38, Phase 7 (optional AI features) - grammar
 * explanation, writing correction, and conversation practice, all backed by
 * AnthropicClient. Deliberately scoped to the three features actually
 * requested for this pass; weakness analysis / personalized learning path is
 * left for a later iteration, matching the requirements doc's own "do not
 * implement every feature at once" rule (section 44).
 */
@Service
@RequiredArgsConstructor
public class AiService {

    private static final String CORRECTED_MARKER = "###CORRECTED###";
    private static final String FEEDBACK_MARKER = "###FEEDBACK###";

    private final AnthropicClient anthropicClient;
    private final GrammarRepository grammarRepository;

    public GrammarExplanationResponse explainGrammar(GrammarExplanationRequest request) {
        Grammar grammar = null;
        if (request.getGrammarId() != null) {
            grammar = grammarRepository.findById(request.getGrammarId())
                    .orElseThrow(() -> new ResourceNotFoundException("Grammar not found"));
        }
        if (grammar == null && !StringUtils.hasText(request.getQuestion())) {
            throw new InvalidRequestException("Provide either grammarId or a question");
        }

        String system = "You are a friendly, precise Japanese language teacher helping a learner "
                + "understand Japanese grammar. Explain clearly, give at least two example sentences "
                + "with Vietnamese translations, and keep the explanation focused rather than overly long.";

        StringBuilder prompt = new StringBuilder();
        if (grammar != null) {
            prompt.append("Explain this Japanese grammar point in more depth than the summary below, ")
                    .append("with additional example sentences:\n")
                    .append("Pattern: ").append(grammar.getPattern()).append('\n')
                    .append("Meaning: ").append(grammar.getMeaning()).append('\n');
            if (StringUtils.hasText(grammar.getFormation())) {
                prompt.append("Formation: ").append(grammar.getFormation()).append('\n');
            }
        }
        if (StringUtils.hasText(request.getQuestion())) {
            prompt.append("Learner's question: ").append(request.getQuestion());
        }

        String explanation = anthropicClient.complete(system,
                List.of(new AnthropicClient.Message("user", prompt.toString())));

        return new GrammarExplanationResponse(grammar != null ? grammar.getPattern() : null, explanation);
    }

    public WritingCorrectionResponse correctWriting(WritingCorrectionRequest request) {
        String system = "You are a Japanese writing tutor. The learner will submit Japanese text. "
                + "Respond with EXACTLY two sections in this format and nothing else:\n"
                + CORRECTED_MARKER + "\n<the corrected Japanese text>\n"
                + FEEDBACK_MARKER + "\n<feedback in Vietnamese explaining the mistakes and why, "
                + "or a short compliment if the original was already correct>";

        String raw = anthropicClient.complete(system,
                List.of(new AnthropicClient.Message("user", request.getText())));

        return parseCorrection(request.getText(), raw);
    }

    private WritingCorrectionResponse parseCorrection(String original, String raw) {
        int correctedIdx = raw.indexOf(CORRECTED_MARKER);
        int feedbackIdx = raw.indexOf(FEEDBACK_MARKER);
        if (correctedIdx < 0 || feedbackIdx < 0 || feedbackIdx < correctedIdx) {
            // The model didn't follow the requested format - fall back to
            // surfacing the raw reply as feedback rather than failing outright.
            return new WritingCorrectionResponse(original, original, raw.trim());
        }
        String corrected = raw.substring(correctedIdx + CORRECTED_MARKER.length(), feedbackIdx).trim();
        String feedback = raw.substring(feedbackIdx + FEEDBACK_MARKER.length()).trim();
        return new WritingCorrectionResponse(original, corrected, feedback);
    }

    public ConversationResponse converse(ConversationRequest request) {
        String level = StringUtils.hasText(request.getLevel()) ? request.getLevel() : "N5";
        String system = "You are a friendly Japanese conversation partner for a learner at JLPT level "
                + level + ". Reply mostly in Japanese using vocabulary and grammar appropriate for that "
                + "level, keep replies short (2-4 sentences), and if the learner made a Japanese mistake "
                + "in their last message, gently note the correction in Vietnamese at the end of your reply.";

        List<AnthropicClient.Message> messages = request.getMessages().stream()
                .map(m -> new AnthropicClient.Message(m.getRole(), m.getContent()))
                .collect(Collectors.toList());

        String reply = anthropicClient.complete(system, messages);
        return new ConversationResponse(reply);
    }
}
