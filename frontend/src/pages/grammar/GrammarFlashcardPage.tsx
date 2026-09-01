import { Button, Typography } from 'antd';
import { BulbOutlined } from '@ant-design/icons';
import { useParams, Link } from 'react-router-dom';
import { FlashcardView } from '../../components/learning/FlashcardView';
import { grammarApi } from '../../api/grammarApi';
import { grammarLearningApi } from '../../api/learningApi';
import type { Grammar } from '../../types/content';

const { Title, Text, Paragraph } = Typography;

export default function GrammarFlashcardPage() {
  const { id: grammarId } = useParams<{ id: string }>();

  return (
    <>
      <FlashcardView<Grammar>
        backPath="/grammar"
        targetType="GRAMMAR"
        fetchItem={(id) => grammarApi.getPublic(id)}
        fetchProgress={(id) => grammarLearningApi.getProgress(id)}
        mark={(id, outcome) => grammarLearningApi.mark(id, outcome)}
        renderFront={(item) => (
          <Title level={2} style={{ margin: 0 }}>
            {item.pattern}
          </Title>
        )}
        renderBack={(item) => (
          <div>
            <Title level={3} style={{ margin: 0 }}>
              {item.meaning}
            </Title>
            {item.formation && (
              <Paragraph style={{ marginTop: 12 }}>
                <Text strong>Formation: </Text>
                {item.formation}
              </Paragraph>
            )}
            {item.explanation && <Paragraph>{item.explanation}</Paragraph>}
            {item.example && (
              <Paragraph>
                {item.example}
                {item.exampleMeaning && (
                  <div>
                    <Text type="secondary">{item.exampleMeaning}</Text>
                  </div>
                )}
              </Paragraph>
            )}
            {item.notes && (
              <Text type="secondary" style={{ display: 'block', marginTop: 8 }}>
                {item.notes}
              </Text>
            )}
          </div>
        )}
      />
      <div style={{ maxWidth: 560, margin: '16px auto 0', textAlign: 'center' }}>
        <Link to={`/ai/grammar?grammarId=${grammarId}`}>
          <Button icon={<BulbOutlined />}>Explain with AI</Button>
        </Link>
      </div>
    </>
  );
}
