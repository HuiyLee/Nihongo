import { useCallback, useEffect, useState } from 'react';
import { Typography, Row, Col, Card, Statistic } from 'antd';
import {
  UserOutlined,
  BookOutlined,
  FontSizeOutlined,
  FormOutlined,
  ReadOutlined,
  TrophyOutlined,
  PercentageOutlined,
} from '@ant-design/icons';
import { adminStatsApi } from '../../api/progressApi';
import type { AdminStats } from '../../types/progress';

const { Title, Paragraph } = Typography;

/** Requirements section 35 - real content/user counts and the exam pass rate. */
export default function AdminDashboardPage() {
  const [stats, setStats] = useState<AdminStats | null>(null);

  const load = useCallback(async () => {
    try {
      const response = await adminStatsApi.get();
      setStats(response.data.data);
    } catch {
      // Non-fatal - the cards below just fall back to 0.
    }
  }, []);

  useEffect(() => {
    // Async data fetch - see CrudManager.tsx for why this is not the
    // derived-state anti-pattern the react-hooks rule targets.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void load();
  }, [load]);

  return (
    <div>
      <Title level={3}>Admin overview</Title>
      <Paragraph type="secondary">
        Content, user, and exam statistics across the platform.
      </Paragraph>
      <Row gutter={[16, 16]}>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Users" value={stats?.totalUsers ?? 0} prefix={<UserOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Lessons" value={stats?.totalLessons ?? 0} prefix={<BookOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic
              title="Vocabulary"
              value={stats?.totalVocabulary ?? 0}
              prefix={<FontSizeOutlined />}
            />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Kanji" value={stats?.totalKanji ?? 0} prefix={<FormOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Grammar" value={stats?.totalGrammar ?? 0} prefix={<ReadOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic
              title="Exercises"
              value={stats?.totalExercises ?? 0}
              prefix={<FormOutlined />}
            />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic title="Exams" value={stats?.totalExams ?? 0} prefix={<TrophyOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic
              title="Study sessions"
              value={stats?.totalStudySessions ?? 0}
              prefix={<BookOutlined />}
            />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic
              title="Exam attempts"
              value={stats?.totalExamAttempts ?? 0}
              prefix={<TrophyOutlined />}
            />
          </Card>
        </Col>
        <Col xs={12} md={8} lg={4}>
          <Card>
            <Statistic
              title="Pass rate"
              value={stats?.passRate ?? 0}
              suffix="%"
              prefix={<PercentageOutlined />}
            />
          </Card>
        </Col>
      </Row>
    </div>
  );
}
