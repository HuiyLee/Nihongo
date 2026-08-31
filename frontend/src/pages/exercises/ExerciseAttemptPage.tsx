import { useCallback, useEffect, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import { Button, Card, Checkbox, Radio, Space, Tag, Typography, Alert, message } from 'antd';
import { ArrowLeftOutlined, SoundOutlined } from '@ant-design/icons';
import { exerciseApi } from '../../api/exerciseApi';
import { LoadingState } from '../../components/LoadingState';
import { ErrorState } from '../../components/ErrorState';
import { getErrorMessage } from '../../utils/errors';
import { EXERCISE_DIFFICULTY_COLOR, SELECTABLE_EXERCISE_TYPES } from '../../types/exercise';
import type { Exercise, SubmitExerciseResponse } from '../../types/exercise';

const { Title, Paragraph, Text } = Typography;

export default function ExerciseAttemptPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const exerciseId = Number(id);

  const [exercise, setExercise] = useState<Exercise | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [selected, setSelected] = useState<number[]>([]);
  const [submitting, setSubmitting] = useState(false);
  const [result, setResult] = useState<SubmitExerciseResponse | null>(null);

  const load = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await exerciseApi.getPublic(exerciseId);
      setExercise(response.data.data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load exercise'));
    } finally {
      setLoading(false);
    }
  }, [exerciseId]);

  useEffect(() => {
    // Resetting selection/result state for a newly-loaded exercise, plus
    // the real async fetch below - see CrudManager.tsx for why this is not
    // the derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setSelected([]);
    setResult(null);
    void load();
  }, [load]);

  const handleSubmit = async () => {
    if (selected.length === 0) {
      message.warning('Select an answer first');
      return;
    }
    setSubmitting(true);
    try {
      const response = await exerciseApi.submit(exerciseId, { answerIds: selected });
      setResult(response.data.data);
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to submit exercise'));
    } finally {
      setSubmitting(false);
    }
  };

  const handleTryAgain = () => {
    setSelected([]);
    setResult(null);
  };

  if (loading) return <LoadingState />;
  if (error) return <ErrorState message={error} onRetry={load} />;
  if (!exercise) return null;

  const isMultiAnswer = exercise.type === 'MULTIPLE_ANSWER';
  const isSupported = SELECTABLE_EXERCISE_TYPES.includes(exercise.type);

  return (
    <div style={{ maxWidth: 640, margin: '0 auto' }}>
      <Space style={{ marginBottom: 16 }}>
        <Button icon={<ArrowLeftOutlined />} onClick={() => navigate('/exercises')}>
          Back
        </Button>
        <Tag>{exercise.type.replaceAll('_', ' ')}</Tag>
        <Tag color={EXERCISE_DIFFICULTY_COLOR[exercise.difficulty]}>{exercise.difficulty}</Tag>
      </Space>

      <Card>
        <Title level={4}>{exercise.question}</Title>
        {exercise.imageUrl && (
          <img
            src={exercise.imageUrl}
            alt="Exercise"
            style={{ maxWidth: '100%', marginBottom: 16 }}
          />
        )}
        {exercise.audioUrl && (
          <Paragraph>
            <SoundOutlined />{' '}
            <audio controls src={exercise.audioUrl} style={{ verticalAlign: 'middle' }} />
          </Paragraph>
        )}

        {!isSupported ? (
          <Alert
            type="info"
            showIcon
            message="This exercise type isn't attemptable yet"
            description="Matching and Listening exercises will get their own attempt UI in a later phase."
          />
        ) : isMultiAnswer ? (
          <Checkbox.Group
            style={{ display: 'flex', flexDirection: 'column', gap: 8 }}
            value={selected}
            disabled={!!result}
            onChange={(values) => setSelected(values as number[])}
            options={exercise.answers.map((a) => ({ label: a.answerText, value: a.id }))}
          />
        ) : (
          <Radio.Group
            style={{ display: 'flex', flexDirection: 'column', gap: 8 }}
            value={selected[0]}
            disabled={!!result}
            onChange={(e) => setSelected([e.target.value as number])}
          >
            {exercise.answers.map((a) => (
              <Radio key={a.id} value={a.id}>
                {a.answerText}
              </Radio>
            ))}
          </Radio.Group>
        )}
      </Card>

      {isSupported && !result && (
        <Button
          type="primary"
          block
          style={{ marginTop: 16 }}
          loading={submitting}
          onClick={handleSubmit}
        >
          Submit
        </Button>
      )}

      {result && (
        <div style={{ marginTop: 16 }}>
          <Alert
            type={result.correct ? 'success' : 'error'}
            showIcon
            message={result.correct ? 'Correct!' : 'Not quite'}
            description={
              result.explanation ? (
                <Text>{result.explanation}</Text>
              ) : (
                'No explanation was provided for this exercise.'
              )
            }
          />
          <Button style={{ marginTop: 12 }} onClick={handleTryAgain}>
            Try again
          </Button>
        </div>
      )}
    </div>
  );
}
