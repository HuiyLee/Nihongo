import type { ReactNode } from 'react';
import { Card, Typography } from 'antd';

const { Title } = Typography;

/** Centered card shell used by /login and /register. */
export function AuthLayout({ title, children }: { title: string; children: ReactNode }) {
  return (
    <div
      style={{
        minHeight: '100vh',
        display: 'flex',
        alignItems: 'center',
        justifyContent: 'center',
        background: 'linear-gradient(135deg, #f5f7fa 0%, #e4ecf7 100%)',
        padding: '1rem',
      }}
    >
      <Card style={{ width: 400, maxWidth: '100%' }}>
        <Title level={3} style={{ textAlign: 'center', marginBottom: 24 }}>
          {title}
        </Title>
        {children}
      </Card>
    </div>
  );
}
