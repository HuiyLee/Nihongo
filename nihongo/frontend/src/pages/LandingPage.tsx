import { Button, Typography, Space } from 'antd';
import { Link } from 'react-router-dom';

const { Title, Paragraph } = Typography;

export default function LandingPage() {
  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        flexDirection: 'column',
        alignItems: 'center',
        justifyContent: 'center',
        textAlign: 'center',
        padding: '2rem',
        background: 'linear-gradient(135deg, #f5f7fa 0%, #e4ecf7 100%)',
      }}
    >
      <Title>日本語を学ぼう</Title>
      <Title level={3} type="secondary" style={{ marginTop: -8 }}>
        Learn Japanese, from N5 to N1
      </Title>
      <Paragraph style={{ maxWidth: 480 }}>
        Lessons, vocabulary, Kanji, grammar, listening, reading, exercises and JLPT mock exams - all
        in one place.
      </Paragraph>
      <Space>
        <Link to="/register">
          <Button type="primary" size="large">
            Get started
          </Button>
        </Link>
        <Link to="/login">
          <Button size="large">Sign in</Button>
        </Link>
      </Space>
    </div>
  );
}
