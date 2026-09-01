import { useState } from 'react';
import { Alert, Button, Card, Input, Typography } from 'antd';
import { EditOutlined } from '@ant-design/icons';
import { aiApi } from '../../api/aiApi';
import { getErrorMessage } from '../../utils/errors';
import type { WritingCorrectionResponse } from '../../types/ai';

const { Title, Paragraph } = Typography;
const { TextArea } = Input;

/** Requirements section 38, Phase 7 (optional) - AI writing correction. */
export default function AiWritingCorrectionPage() {
  const [text, setText] = useState('');
  const [checking, setChecking] = useState(false);
  const [result, setResult] = useState<WritingCorrectionResponse | null>(null);
  const [error, setError] = useState<string | null>(null);

  const handleCheck = async () => {
    setChecking(true);
    setError(null);
    try {
      const response = await aiApi.correctWriting({ text });
      setResult(response.data.data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to check this text'));
    } finally {
      setChecking(false);
    }
  };

  return (
    <div style={{ maxWidth: 720, margin: '0 auto' }}>
      <Title level={3}>AI writing correction</Title>
      <Paragraph type="secondary">
        Write a Japanese sentence or short paragraph and get AI feedback on grammar and naturalness.
      </Paragraph>

      <Card>
        <TextArea
          rows={5}
          placeholder="わたし がくせいです。"
          value={text}
          onChange={(e) => setText(e.target.value)}
          maxLength={2000}
          showCount
        />
        <Button
          type="primary"
          icon={<EditOutlined />}
          style={{ marginTop: 12 }}
          loading={checking}
          disabled={!text.trim()}
          onClick={handleCheck}
        >
          Check with AI
        </Button>
      </Card>

      {error && <Alert type="error" message={error} showIcon style={{ marginTop: 16 }} />}

      {result && (
        <Card style={{ marginTop: 16 }}>
          <Title level={5}>Corrected</Title>
          <Paragraph style={{ whiteSpace: 'pre-wrap', fontSize: 16 }}>{result.corrected}</Paragraph>
          <Title level={5}>Feedback</Title>
          <Paragraph style={{ whiteSpace: 'pre-wrap' }}>{result.feedback}</Paragraph>
        </Card>
      )}
    </div>
  );
}
