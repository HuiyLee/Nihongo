import { useCallback, useEffect, useState } from 'react';
import type { ReactNode } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Card, Space, Tag, Typography, message } from 'antd';
import { ArrowLeftOutlined, CheckCircleOutlined, CloseCircleOutlined } from '@ant-design/icons';
import type { AxiosResponse } from 'axios';
import { BookmarkButton } from './BookmarkButton';
import { LoadingState } from '../LoadingState';
import { ErrorState } from '../ErrorState';
import { getErrorMessage } from '../../utils/errors';
import { LEARNING_STATUS_COLOR, LEARNING_STATUS_LABEL } from '../../types/learning';
import type { BookmarkTargetType, LearningProgress, MarkOutcome } from '../../types/learning';
import type { ApiResponse } from '../../types/api';

const { Text, Paragraph } = Typography;

export interface FlashcardViewProps<T> {
  backPath: string;
  targetType: BookmarkTargetType;
  fetchItem: (id: number) => Promise<AxiosResponse<ApiResponse<T>>>;
  fetchProgress: (id: number) => Promise<AxiosResponse<ApiResponse<LearningProgress>>>;
  mark: (id: number, outcome: MarkOutcome) => Promise<AxiosResponse<ApiResponse<LearningProgress>>>;
  renderFront: (item: T) => ReactNode;
  renderBack: (item: T) => ReactNode;
}

/**
 * Generic flip-card detail view shared by the Vocabulary, Kanji, and
 * Grammar learning pages (section 10): shows the item, lets the learner
 * mark it known/unknown, and toggle a bookmark - all scoped to the
 * authenticated caller by the backend.
 */
export function FlashcardView<T>({
  backPath,
  targetType,
  fetchItem,
  fetchProgress,
  mark,
  renderFront,
  renderBack,
}: FlashcardViewProps<T>) {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const itemId = Number(id);

  const [item, setItem] = useState<T | null>(null);
  const [progress, setProgress] = useState<LearningProgress | null>(null);
  const [flipped, setFlipped] = useState(false);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [marking, setMarking] = useState(false);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const [itemResponse, progressResponse] = await Promise.all([
        fetchItem(itemId),
        fetchProgress(itemId),
      ]);
      setItem(itemResponse.data.data);
      setProgress(progressResponse.data.data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load item'));
    } finally {
      setLoading(false);
    }
  }, [fetchItem, fetchProgress, itemId]);

  useEffect(() => {
    // Resetting the flip state for a newly-loaded item, plus the real
    // async data fetch below - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setFlipped(false);
    void load();
  }, [load]);

  const handleMark = async (outcome: MarkOutcome) => {
    setMarking(true);
    try {
      const response = await mark(itemId, outcome);
      setProgress(response.data.data);
      message.success(outcome === 'KNOWN' ? 'Marked as known' : 'Marked as still learning');
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to update progress'));
    } finally {
      setMarking(false);
    }
  };

  if (loading) return <LoadingState />;
  if (error) return <ErrorState message={error} onRetry={load} />;
  if (!item) return null;

  return (
    <div style={{ maxWidth: 560, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate(backPath)}>
          Back
        </Button>
        {progress && (
          <Tag color={LEARNING_STATUS_COLOR[progress.status]}>
            {LEARNING_STATUS_LABEL[progress.status]}
          </Tag>
        )}
      </Space>

      <Card
        hoverable
        onClick={() => setFlipped((f) => !f)}
        style={{ minHeight: 240, textAlign: 'center', cursor: 'pointer' }}
        styles={{
          body: { display: 'flex', alignItems: 'center', justifyContent: 'center', minHeight: 216 },
        }}
      >
        <div>{flipped ? renderBack(item) : renderFront(item)}</div>
      </Card>
      <Text type="secondary" style={{ display: 'block', textAlign: 'center', marginTop: 8 }}>
        Click the card to flip
      </Text>

      <Space style={{ marginTop: 24, width: '100%', justifyContent: 'center' }} wrap>
        <Button
          icon={<CloseCircleOutlined />}
          danger
          loading={marking}
          onClick={() => handleMark('UNKNOWN')}
        >
          Still learning
        </Button>
        <Button
          icon={<CheckCircleOutlined />}
          type="primary"
          loading={marking}
          onClick={() => handleMark('KNOWN')}
        >
          I know this
        </Button>
        <BookmarkButton targetType={targetType} targetId={itemId} />
      </Space>

      {progress && (
        <Paragraph type="secondary" style={{ textAlign: 'center', marginTop: 16 }}>
          Correct: {progress.correctCount} · Wrong: {progress.wrongCount}
        </Paragraph>
      )}
    </div>
  );
}
