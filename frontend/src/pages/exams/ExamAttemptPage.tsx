import { useCallback, useEffect, useRef, useState } from 'react';
import { useNavigate, useParams } from 'react-router-dom';
import {
  Button,
  Card,
  Checkbox,
  Radio,
  Space,
  Tag,
  Typography,
  Alert,
  Progress,
  Divider,
  message,
} from 'antd';
import { ArrowLeftOutlined, ClockCircleOutlined, OrderedListOutlined } from '@ant-design/icons';
import { examApi } from '../../api/examApi';
import { LoadingState } from '../../components/LoadingState';
import { ErrorState } from '../../components/ErrorState';
import { getErrorMessage } from '../../utils/errors';
import { SELECTABLE_EXERCISE_TYPES } from '../../types/exercise';
import type { Exam, ExamAttempt } from '../../types/exam';

const { Title, Paragraph, Text } = Typography;

/**
 * Requirements section 17-18. Detail + Start live on this same page; once
 * started it switches to the question list with a countdown. The countdown
 * is purely a UX convenience - the backend independently computes the
 * deadline from the DB-stored startedAt and rejects a late submit
 * regardless of what this timer shows (BR-009), so this page auto-submits
 * at zero but the server would reject it even if this client didn't.
 */
export default function ExamAttemptPage() {
  const { id } = useParams<{ id: string }>();
  const navigate = useNavigate();
  const examId = Number(id);

  const [exam, setExam] = useState<Exam | null>(null);
  const [attempt, setAttempt] = useState<ExamAttempt | null>(null);
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [starting, setStarting] = useState(false);
  const [answers, setAnswers] = useState<Record<number, number[]>>({});
  const [remainingSeconds, setRemainingSeconds] = useState<number | null>(null);
  const [submitting, setSubmitting] = useState(false);
  const [lastSavedAt, setLastSavedAt] = useState<Date | null>(null);
  const submittedRef = useRef(false);

  const loadExam = useCallback(async () => {
    setLoading(true);
    setError(null);
    try {
      const response = await examApi.getPublic(examId);
      setExam(response.data.data);
    } catch (err) {
      setError(getErrorMessage(err, 'Failed to load exam'));
    } finally {
      setLoading(false);
    }
  }, [examId]);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void loadExam();
  }, [loadExam]);

  const handleSubmit = useCallback(async () => {
    if (!attempt || submittedRef.current) {
      return;
    }
    submittedRef.current = true;
    setSubmitting(true);
    try {
      await examApi.submit(examId, {
        answers: attempt.questions.map((q) => ({
          examQuestionId: q.id,
          answerIds: answers[q.id] ?? [],
        })),
      });
      navigate(`/exams/${examId}/result`);
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to submit exam'));
      submittedRef.current = false;
      setSubmitting(false);
    }
  }, [attempt, answers, examId, navigate]);

  // Countdown tick - the setInterval callback fires asynchronously, so its
  // setState calls are not the synchronous-in-effect-body pattern the
  // react-hooks/set-state-in-effect rule targets.
  useEffect(() => {
    if (!attempt) {
      return;
    }
    const deadline = new Date(attempt.startedAt).getTime() + attempt.durationMinutes * 60_000;

    const tick = () => {
      const secondsLeft = Math.max(0, Math.round((deadline - Date.now()) / 1000));
      setRemainingSeconds(secondsLeft);
      if (secondsLeft <= 0) {
        void handleSubmit();
      }
    };

    tick();
    const interval = window.setInterval(tick, 1000);
    return () => window.clearInterval(interval);
  }, [attempt, handleSubmit]);

  // Auto save (requirements section 38 Phase 5) - debounced so every click
  // doesn't fire its own request, but a refresh mid-attempt loses at most a
  // couple of seconds of progress rather than everything. The debounce
  // timer's callback fires asynchronously, same reasoning as the countdown
  // tick above.
  useEffect(() => {
    if (!attempt || submittedRef.current) {
      return;
    }
    const timeout = window.setTimeout(() => {
      examApi
        .saveProgress(examId, {
          answers: attempt.questions.map((q) => ({
            examQuestionId: q.id,
            answerIds: answers[q.id] ?? [],
          })),
        })
        .then(() => setLastSavedAt(new Date()))
        .catch(() => {
          // Best-effort - the next debounced save (or the final submit) will
          // retry; a transient failure here shouldn't interrupt the exam.
        });
    }, 1500);
    return () => window.clearTimeout(timeout);
  }, [answers, attempt, examId]);

  const handleStart = async () => {
    setStarting(true);
    try {
      const response = await examApi.start(examId);
      const data = response.data.data;
      setAttempt(data);
      const restored: Record<number, number[]> = {};
      for (const saved of data.savedAnswers) {
        restored[saved.examQuestionId] = saved.answerIds;
      }
      setAnswers(restored);
      if (data.savedAnswers.length > 0) {
        setLastSavedAt(new Date());
      }
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to start exam'));
    } finally {
      setStarting(false);
    }
  };

  if (loading) return <LoadingState />;
  if (error) return <ErrorState message={error} onRetry={loadExam} />;
  if (!exam) return null;

  if (!attempt) {
    return (
      <div style={{ maxWidth: 640, margin: '0 auto' }}>
        <Button
          icon={<ArrowLeftOutlined />}
          onClick={() => navigate('/exams')}
          style={{ marginBottom: 16 }}
        >
          Back
        </Button>
        <Card>
          <Tag>{exam.levelCode}</Tag>
          <Title level={3}>{exam.title}</Title>
          {exam.description && <Paragraph>{exam.description}</Paragraph>}
          <Space size="large" style={{ marginBottom: 24 }}>
            <Text>
              <ClockCircleOutlined /> {exam.durationMinutes} minutes
            </Text>
            <Text>
              <OrderedListOutlined /> {exam.totalQuestions} questions
            </Text>
          </Space>
          <Alert
            type="info"
            showIcon
            style={{ marginBottom: 16 }}
            message="Once started, the timer cannot be paused"
            description="Answer all questions before time runs out - the exam is auto-submitted with whatever you've selected once the clock hits zero."
          />
          <Button type="primary" size="large" block loading={starting} onClick={handleStart}>
            Start Exam
          </Button>
        </Card>
      </div>
    );
  }

  const minutes =
    remainingSeconds !== null ? Math.floor(remainingSeconds / 60) : attempt.durationMinutes;
  const seconds = remainingSeconds !== null ? remainingSeconds % 60 : 0;
  const totalSeconds = attempt.durationMinutes * 60;
  const percentLeft =
    remainingSeconds !== null ? Math.round((remainingSeconds / totalSeconds) * 100) : 100;

  return (
    <div style={{ maxWidth: 720, margin: '0 auto' }}>
      <Card
        style={{
          position: 'sticky',
          top: 0,
          zIndex: 1,
          marginBottom: 16,
        }}
      >
        <Space style={{ width: '100%', justifyContent: 'space-between' }}>
          <Title level={4} style={{ margin: 0 }}>
            {attempt.examTitle}
          </Title>
          <Tag
            color={percentLeft <= 10 ? 'red' : 'blue'}
            style={{ fontSize: 16, padding: '4px 12px' }}
          >
            <ClockCircleOutlined /> {String(minutes).padStart(2, '0')}:
            {String(seconds).padStart(2, '0')}
          </Tag>
        </Space>
        <Progress
          percent={percentLeft}
          showInfo={false}
          status={percentLeft <= 10 ? 'exception' : 'active'}
        />
        {lastSavedAt && (
          <Text type="secondary" style={{ fontSize: 12 }}>
            Progress auto-saved at {lastSavedAt.toLocaleTimeString()}
          </Text>
        )}
      </Card>

      {attempt.questions.map((q, index) => {
        const isMultiAnswer = q.exercise.type === 'MULTIPLE_ANSWER';
        const isSupported = SELECTABLE_EXERCISE_TYPES.includes(q.exercise.type);
        const selected = answers[q.id] ?? [];

        return (
          <Card key={q.id} style={{ marginBottom: 16 }}>
            <Text type="secondary">
              Question {index + 1} of {attempt.questions.length}
            </Text>
            <Title level={5} style={{ marginTop: 8 }}>
              {q.exercise.question}
            </Title>

            {!isSupported ? (
              <Alert type="info" showIcon message="This question type isn't attemptable yet" />
            ) : isMultiAnswer ? (
              <Checkbox.Group
                style={{ display: 'flex', flexDirection: 'column', gap: 8 }}
                value={selected}
                onChange={(values) =>
                  setAnswers((prev) => ({ ...prev, [q.id]: values as number[] }))
                }
                options={q.exercise.answers.map((a) => ({ label: a.answerText, value: a.id }))}
              />
            ) : (
              <Radio.Group
                style={{ display: 'flex', flexDirection: 'column', gap: 8 }}
                value={selected[0]}
                onChange={(e) =>
                  setAnswers((prev) => ({ ...prev, [q.id]: [e.target.value as number] }))
                }
              >
                {q.exercise.answers.map((a) => (
                  <Radio key={a.id} value={a.id}>
                    {a.answerText}
                  </Radio>
                ))}
              </Radio.Group>
            )}
          </Card>
        );
      })}

      <Divider />
      <Button
        type="primary"
        size="large"
        block
        loading={submitting}
        onClick={() => void handleSubmit()}
      >
        Submit Exam
      </Button>
    </div>
  );
}
