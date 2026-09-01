import { Typography } from 'antd';
import { FlashcardView } from '../../components/learning/FlashcardView';
import { kanjiApi } from '../../api/kanjiApi';
import { kanjiLearningApi } from '../../api/learningApi';
import type { Kanji } from '../../types/content';

const { Title, Text, Paragraph } = Typography;

export default function KanjiFlashcardPage() {
  return (
    <FlashcardView<Kanji>
      backPath="/kanji"
      targetType="KANJI"
      fetchItem={(id) => kanjiApi.getPublic(id)}
      fetchProgress={(id) => kanjiLearningApi.getProgress(id)}
      mark={(id, outcome) => kanjiLearningApi.mark(id, outcome)}
      renderFront={(item) => (
        <Title level={1} style={{ margin: 0, fontSize: 72 }}>
          {item.character}
        </Title>
      )}
      renderBack={(item) => (
        <div>
          <Title level={3} style={{ margin: 0 }}>
            {item.meaning}
          </Title>
          {item.onyomi && <div>On: {item.onyomi}</div>}
          {item.kunyomi && <div>Kun: {item.kunyomi}</div>}
          {item.strokeCount && <Text type="secondary">{item.strokeCount} strokes</Text>}
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
