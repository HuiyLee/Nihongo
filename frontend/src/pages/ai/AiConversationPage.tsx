import { useCallback, useEffect, useRef, useState } from 'react';
import { Alert, Avatar, Button, Card, Input, Select, Space, Typography, message } from 'antd';
import { RobotOutlined, SendOutlined, UserOutlined } from '@ant-design/icons';
import { aiApi } from '../../api/aiApi';
import { levelApi } from '../../api/levelApi';
import { getErrorMessage } from '../../utils/errors';
import type { Level } from '../../types/content';
import type { ConversationMessage } from '../../types/ai';

const { Title, Paragraph, Text } = Typography;

/**
 * Requirements section 38, Phase 7 (optional) - AI conversation practice.
 * Deliberately stateless on the backend: the conversation lives only in this
 * page's React state and is resent in full with every turn, matching
 * ConversationRequest on the backend (no new entity/migration for Phase 7).
 */
export default function AiConversationPage() {
  const [levels, setLevels] = useState<Level[]>([]);
  const [level, setLevel] = useState('N5');
  const [messages, setMessages] = useState<ConversationMessage[]>([]);
  const [draft, setDraft] = useState('');
  const [sending, setSending] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const bottomRef = useRef<HTMLDivElement>(null);

  const loadLevels = useCallback(async () => {
    try {
      const response = await levelApi.listPublic();
      setLevels(response.data.data);
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to load JLPT levels'));
    }
  }, []);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void loadLevels();
  }, [loadLevels]);

  useEffect(() => {
    bottomRef.current?.scrollIntoView({ behavior: 'smooth' });
  }, [messages]);

  const handleSend = async () => {
    const content = draft.trim();
    if (!content) return;

    const nextMessages: ConversationMessage[] = [...messages, { role: 'user', content }];
    setMessages(nextMessages);
    setDraft('');
    setSending(true);
    setError(null);
    try {
      const response = await aiApi.converse({ level, messages: nextMessages });
      setMessages([...nextMessages, { role: 'assistant', content: response.data.data.reply }]);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to reach the AI conversation partner'));
    } finally {
      setSending(false);
    }
  };

  return (
    <div style={{ maxWidth: 720, margin: '0 auto' }}>
      <Space style={{ width: '100%', justifyContent: 'space-between' }} wrap>
        <div>
          <Title level={3} style={{ marginBottom: 0 }}>
            AI conversation practice
          </Title>
          <Paragraph type="secondary" style={{ marginBottom: 0 }}>
            Practice a Japanese conversation with an AI partner pitched at your level.
          </Paragraph>
        </div>
        <Select
          value={level}
          onChange={setLevel}
          style={{ width: 120 }}
          options={levels.map((l) => ({ label: l.code, value: l.code }))}
        />
      </Space>

      <Card style={{ marginTop: 16 }}>
        <div style={{ minHeight: 320, maxHeight: 480, overflowY: 'auto' }}>
          {messages.length === 0 && (
            <Text type="secondary">Say something in Japanese (or ask how to start) below.</Text>
          )}
          <Space direction="vertical" style={{ width: '100%' }} size={12}>
            {messages.map((m, i) => (
              <Space
                key={i}
                align="start"
                style={{
                  width: '100%',
                  flexDirection: m.role === 'user' ? 'row-reverse' : 'row',
                }}
              >
                <Avatar icon={m.role === 'user' ? <UserOutlined /> : <RobotOutlined />} />
                <div
                  style={{
                    background: m.role === 'user' ? '#e6f4ff' : '#f5f5f5',
                    padding: '8px 12px',
                    borderRadius: 8,
                    maxWidth: 480,
                    whiteSpace: 'pre-wrap',
                  }}
                >
                  {m.content}
                </div>
              </Space>
            ))}
          </Space>
          <div ref={bottomRef} />
        </div>

        {error && <Alert type="error" message={error} showIcon style={{ marginTop: 12 }} />}

        <Space.Compact style={{ width: '100%', marginTop: 12 }}>
          <Input
            placeholder="こんにちは!"
            value={draft}
            onChange={(e) => setDraft(e.target.value)}
            onPressEnter={handleSend}
            maxLength={2000}
            disabled={sending}
          />
          <Button
            type="primary"
            icon={<SendOutlined />}
            loading={sending}
            disabled={!draft.trim()}
            onClick={handleSend}
          >
            Send
          </Button>
        </Space.Compact>
      </Card>
    </div>
  );
}
