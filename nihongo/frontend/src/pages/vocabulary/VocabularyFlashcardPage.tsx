import { Typography } from 'antd';
import { FlashcardView } from '../../components/learning/FlashcardView';
import { vocabularyApi } from '../../api/vocabularyApi';
import { vocabularyLearningApi } from '../../api/learningApi';
import type { Vocabulary } from '../../types/content';

const { Title, Text, Paragraph } = Typography;

export default function VocabularyFlashcardPage() {
  return (
    <FlashcardView<Vocabulary>
      backPath="/vocabulary"
      targetType="VOCABULARY"
      fetchItem={(id) => vocabularyApi.getPublic(id)}
      fetchProgress={(id) => vocabularyLearningApi.getProgress(id)}
      mark={(id, outcome) => vocabularyLearningApi.mark(id, outcome)}
      renderFront={(item) => (
        <div>
          <Title level={2} style={{ margin: 0 }}>
            {item.kanji || item.word}
          </Title>
          {item.hiragana && <Text type="secondary">{item.hiragana}</Text>}
        </div>
      )}
      renderBack={(item) => (
        <div>
          <Title level={3} style={{ margin: 0 }}>
            {item.meaning}
          </Title>
          {item.romaji && <Text>{item.romaji}</Text>}
          {item.partOfSpeech && (
            <div>
              <Text type="secondary">{item.partOfSpeech}</Text>
            </div>
          )}
          {item.example && (
            <Paragraph style={{ marginTop: 12 }}>
              {item.example}
              {item.exampleMeaning && (
                <div>
                  <Text type="secondary">{item.exampleMeaning}</Text>
                </div>
              )}
            </Paragraph>
          )}
        </div>
      )}
    />
  );
}
