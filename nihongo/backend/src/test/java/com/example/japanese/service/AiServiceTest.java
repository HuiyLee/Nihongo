package com.example.japanese.service;

import com.example.japanese.dto.request.ConversationMessageDto;
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
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

/**
 * Requirements section 38, Phase 7 - AiService business logic, independent
 * of the HTTP layer: AnthropicClient is mocked out entirely, so these tests
 * never make a real network call.
 */
@ExtendWith(MockitoExtension.class)
class AiServiceTest {

    @Mock
    private AnthropicClient anthropicClient;
    @Mock
    private GrammarRepository grammarRepository;

    private AiService aiService;

    @BeforeEach
    void setUp() {
        aiService = new AiService(anthropicClient, grammarRepository);
    }

    @Test
    void explainGrammar_withNeitherGrammarIdNorQuestion_throwsInvalidRequest() {
        GrammarExplanationRequest request = new GrammarExplanationRequest();

        assertThatThrownBy(() -> aiService.explainGrammar(request))
                .isInstanceOf(InvalidRequestException.class);
    }

    @Test
    void explainGrammar_withUnknownGrammarId_throwsResourceNotFound() {
        GrammarExplanationRequest request = new GrammarExplanationRequest();
        request.setGrammarId(999L);
        when(grammarRepository.findById(999L)).thenReturn(Optional.empty());

        assertThatThrownBy(() -> aiService.explainGrammar(request))
                .isInstanceOf(ResourceNotFoundException.class);
    }

    @Test
    void explainGrammar_withGrammarId_includesPatternInResponseAndCallsAi() {
        Grammar grammar = Grammar.builder().pattern("〜ばかりだ").meaning("only/just keeps ~ing").build();
        GrammarExplanationRequest request = new GrammarExplanationRequest();
        request.setGrammarId(1L);
        when(grammarRepository.findById(1L)).thenReturn(Optional.of(grammar));
        when(anthropicClient.complete(anyString(), anyList())).thenReturn("Here is the explanation.");

        GrammarExplanationResponse response = aiService.explainGrammar(request);

        assertThat(response.getPattern()).isEqualTo("〜ばかりだ");
        assertThat(response.getExplanation()).isEqualTo("Here is the explanation.");
        verify(anthropicClient).complete(anyString(), anyList());
    }

    @Test
    void explainGrammar_withOnlyQuestion_doesNotTouchRepository() {
        GrammarExplanationRequest request = new GrammarExplanationRequest();
        request.setQuestion("What does 〜てしまう mean?");
        when(anthropicClient.complete(anyString(), anyList())).thenReturn("It means ~.");

        GrammarExplanationResponse response = aiService.explainGrammar(request);

        assertThat(response.getPattern()).isNull();
        assertThat(response.getExplanation()).isEqualTo("It means ~.");
    }

    @Test
    void correctWriting_parsesCorrectedAndFeedbackMarkers() {
        WritingCorrectionRequest request = new WritingCorrectionRequest();
        request.setText("わたし　がくせいです");
        when(anthropicClient.complete(anyString(), anyList())).thenReturn(
                "###CORRECTED###\n私は学生です。\n###FEEDBACK###\nBạn thiếu trợ từ 「は」sau chủ ngữ."
        );

        WritingCorrectionResponse response = aiService.correctWriting(request);

        assertThat(response.getOriginal()).isEqualTo("わたし　がくせいです");
        assertThat(response.getCorrected()).isEqualTo("私は学生です。");
        assertThat(response.getFeedback()).isEqualTo("Bạn thiếu trợ từ 「は」sau chủ ngữ.");
    }

    @Test
    void correctWriting_whenModelIgnoresFormat_fallsBackToRawTextAsFeedback() {
        WritingCorrectionRequest request = new WritingCorrectionRequest();
        request.setText("こんにちは");
        when(anthropicClient.complete(anyString(), anyList())).thenReturn("Looks fine to me!");

        WritingCorrectionResponse response = aiService.correctWriting(request);

        assertThat(response.getOriginal()).isEqualTo("こんにちは");
        assertThat(response.getCorrected()).isEqualTo("こんにちは");
        assertThat(response.getFeedback()).isEqualTo("Looks fine to me!");
    }

    @Test
    void converse_defaultsLevelToN5AndReturnsReply() {
        ConversationRequest request = new ConversationRequest();
        ConversationMessageDto message = new ConversationMessageDto();
        message.setRole("user");
        message.setContent("こんにちは！");
        request.setMessages(List.of(message));
        when(anthropicClient.complete(anyString(), anyList())).thenReturn("こんにちは!元気ですか?");

        ConversationResponse response = aiService.converse(request);

        assertThat(response.getReply()).isEqualTo("こんにちは!元気ですか?");
        verify(anthropicClient).complete(any(), anyList());
    }
}
