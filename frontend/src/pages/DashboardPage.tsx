import { Typography, Row, Col, Card, Statistic } from 'antd';
import { FontSizeOutlined, FormOutlined, ReadOutlined, BookOutlined } from '@ant-design/icons';
import { useAuth } from '../hooks/useAuth';

const { Title, Paragraph } = Typography;

/**
 * Phase 1 placeholder: shows the logged-in user and static progress cards.
 * Real numbers arrive once /api/progress is implemented in a later phase.
 */
export default function DashboardPage() {
  const { user } = useAuth();

  return (
    <div>
      <Title level={3}>Welcome back, {user?.fullName ?? user?.username}!</Title>
      <Paragraph type="secondary">
        This dashboard will show your JLPT learning progress once lessons, vocabulary and Kanji
        content are added.
      </Paragraph>
      <Row gutter={16}>
        <Col xs={12} md={6}>
          <Card>
            <Statistic title="Vocabulary" value={0} suffix="%" prefix={<FontSizeOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card>
            <Statistic title="Kanji" value={0} suffix="%" prefix={<FormOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card>
            <Statistic title="Grammar" value={0} suffix="%" prefix={<ReadOutlined />} />
          </Card>
        </Col>
        <Col xs={12} md={6}>
          <Card>
            <Statistic title="Lessons" value={0} suffix="%" prefix={<BookOutlined />} />
          </Card>
        </Col>
      </Row>
    </div>
  );
}
