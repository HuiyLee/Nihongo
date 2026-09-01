import { useCallback, useEffect, useState } from 'react';
import { useSearchParams } from 'react-router-dom';
import { Alert, Button, Card, Input, Space, Tag, Typography, message } from 'antd';
import { BulbOutlined } from '@ant-design/icons';
import { aiApi } from '../../api/aiApi';
import { grammarApi } from '../../api/grammarApi';
import { LoadingState } from '../../components/LoadingState';
import { getErrorMessage } from '../../utils/errors';
import type { Grammar } from '../../types/content';
import type { GrammarExplanationResponse } from '../../types/ai';

const { Title, Paragraph, Text } = Typography;
const { TextArea } = Input;

/**
 * Requirements section 38, Phase 7 (optional) - AI grammar explanation.
 * Reached either standalone (free-text question) or from a Grammar detail
 * page via ?grammarId=, which grounds the explanation in that grammar point
 * and lets the AI expand on it with extra examples.
 */
export default function AiGrammarExplainerPage() {
  const [searchParams] = useSearchParams();
  const grammarIdParam = searchParams.get('grammarId');
  const grammarId = grammarIdParam ? Number(grammarIdParam) : undefined;

  const [grammar, setGrammar] = useState<Grammar | null>(null);
  const [loadingGrammar, setLoadingGrammar] = useState(false);
  const [question, setQuestion] = useState('');
  const [explaining, setExplaining] = useState(false);
  const [result, setResult] = useState<GrammarExplanationResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const loadGrammar = useCallback(async () => {
    if (!grammarId) return;
    setLoadingGrammar(true);
    try {
      const response = await grammarApi.getPublic(grammarId);
      setGrammar(response.data.data);
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to load the grammar point'));
    } finally {
      setLoadingGrammar(false);
    }
  }, [grammarId]);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setResult(null);
    void loadGrammar();
  }, [loadGrammar]);

  const handleExplain = async () => {
    setExplaining(true);
    setError(null);
    try {
      const response = await aiApi.explainGrammar({ grammarId, question: question || undefined });
      setResult(response.data.data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to get an explanation from the AI'));
    } finally {
      setExplaining(false);
    }
  };

  if (loadingGrammar) return <LoadingState />;

  const canSubmit = Boolean(grammarId) || question.trim().length > 0;

  return (
    <div style={{ maxWidth: 720, margin: '0 auto' }}>
      <Title level={3}>AI grammar explanation</Title>
      <Paragraph type="secondary">
        Ask about any Japanese grammar point and get an AI-generated explanation with examples.
      </Paragraph>

      {grammar && (
        <Card size="small" style={{ marginBottom: 16 }}>
          <Space>
            <Tag color="blue">{grammar.pattern}</Tag>
            <Text>{grammar.meaning}</Text>
          </Space>
        </Card>
      )}

      <Card>
        <TextArea
          rows={3}
          placeholder={
            grammar
              ? 'Optionally add a specific question about this grammar point...'
              : 'e.g. What is the difference between 〜ばかり and 〜だけ?'
          }
          value={question}
          onChange={(e) => setQuestion(e.target.value)}
          maxLength={1000}
          showCount
        />
        <Button
          type="primary"
          icon={<BulbOutlined />}
          style={{ marginTop: 12 }}
          loading={explaining}
          disabled={!canSubmit}
          onClick={handleExplain}
        >
          Explain with AI
        </Button>
      </Card>

      {error && <Alert type="error" message={error} showIcon style={{ marginTop: 16 }} />}

      {result && (
        <Card
          title={result.pattern ? `Explanation - ${result.pattern}` : 'Explanation'}
          style={{ marginTop: 16 }}
        >
          <Paragraph style={{ whiteSpace: 'pre-wrap' }}>{result.explanation}</Paragraph>
        </Card>
      )}
    </div>
  );
}
