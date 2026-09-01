import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Card, Divider, List, Space, Switch, Tag, Typography, message } from 'antd';
import { ArrowLeftOutlined, CheckCircleOutlined, FormOutlined } from '@ant-design/icons';
import { readingApi } from '../../api/readingApi';
import { exerciseApi } from '../../api/exerciseApi';
import { BookmarkButton } from '../../components/learning/BookmarkButton';
import { LoadingState } from '../../components/LoadingState';
import { ErrorState } from '../../components/ErrorState';
import { getErrorMessage } from '../../utils/errors';
import { EXERCISE_DIFFICULTY_COLOR } from '../../types/exercise';
import type { Reading } from '../../types/content';
import type { Exercise } from '../../types/exercise';

const { Title, Paragraph, Text } = Typography;

/**
 * Requirements section 16. Furigana is plain <ruby>/<rt> markup already
 * embedded in `content` by the admin - showing/hiding it is purely a CSS
 * toggle here, no parsing needed. The translation only exists in the
 * response once the backend has decided the passage is unlocked (either
 * `completed` was already true, or admin) - this page never guesses.
 */
export default function ReadingDetailPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const readingId = Number(id);

  const [reading, setReading] = useState<Reading | null>(null);
  const [exercises, setExercises] = useState<Exercise[]>([]);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [completing, setCompleting] = useState(false);
  const [showFurigana, setShowFurigana] = useState(true);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [readingResponse, exercisesResponse] = await Promise.all([
        readingApi.getPublic(readingId),
        exerciseApi.listPublic({ readingId, size: 50 }),
      ]);
      setReading(readingResponse.data.data);
      setExercises(exercisesResponse.data.data.content);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load reading passage'));
    } finally {
      setLoading(false);
    }
  }, [readingId]);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void load();
  }, [load]);

  const handleComplete = async () => {
    setCompleting(true);
    try {
      await readingApi.complete(readingId);
      message.success('Marked as complete - translation unlocked');
      await load();
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to mark as complete'));
    } finally {
      setCompleting(false);
    }
  };

  if (loading) return <LoadingState />;
  if (error) return <ErrorState message={error} onRetry={load} />;
  if (!reading) return null;

  return (
    <div style={{ maxWidth: 720, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16, width: '100%', justifyContent: 'space-between' }} wrap>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/reading')}>
          Back
        </Button>
        <Space>
          <Text>Furigana</Text>
          <Switch checked={showFurigana} onChange={setShowFurigana} />
        </Space>
      </Space>

      <Card>
        <Space style={{ marginBottom: 8 }}>
          <Tag>{reading.levelCode}</Tag>
          <Tag color={EXERCISE_DIFFICULTY_COLOR[reading.difficulty]}>{reading.difficulty}</Tag>
          {reading.completed && (
            <Tag icon={<CheckCircleOutlined />} color="green">
              Completed
            </Tag>
          )}
        </Space>
        <Title level={3}>{reading.title}</Title>
        <div
          className={showFurigana ? undefined : 'reading-hide-furigana'}
          style={{ fontSize: 18, lineHeight: 2 }}
          // The backend is the only writer of reading content (admin-only CRUD) - this is
          // trusted markup (<ruby>/<rt>), not user-supplied HTML.
          dangerouslySetInnerHTML={{ __html: reading.content }}
        />
        <style>{'.reading-hide-furigana rt { display: none; }'}</style>

        {reading.translation ? (
          <>
            <Divider />
            <Title level={5}>Translation</Title>
            <Paragraph>{reading.translation}</Paragraph>
          </>
        ) : (
          <>
            <Divider />
            <Paragraph type="secondary">
              Finish reading, then mark this passage complete to unlock its translation.
            </Paragraph>
          </>
        )}

        <Space style={{ marginTop: 8 }} wrap>
          {!reading.completed && (
            <Button
              type="primary"
              icon={<CheckCircleOutlined />}
              loading={completing}
              onClick={handleComplete}
            >
              Mark as complete
            </Button>
          )}
          <BookmarkButton targetType="READING" targetId={readingId} />
        </Space>
      </Card>

      {exercises.length > 0 && (
        <Card style={{ marginTop: 16 }} title="Comprehension questions">
          <List
            dataSource={exercises}
            renderItem={(exercise) => (
              <List.Item
                actions={[
                  <Button
                    key="attempt"
                    size="small"
                    icon={<FormOutlined />}
                    onClick={() => navigate(`/exercises/${exercise.id}`)}
                  >
                    Attempt
                  </Button>,
                ]}
              >
                <List.Item.Meta title={exercise.question} />
              </List.Item>
            )}
          />
        </Card>
      )}
    </div>
  );
}
