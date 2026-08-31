import { useState } from 'react';
import { Form, Input, Button, Alert } from 'antd';
import { UserOutlined, LockOutlined } from '@ant-design/icons';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { AuthLayout } from '../../layouts/AuthLayout';
import { useAuth } from '../../hooks/useAuth';
import { getErrorMessage } from '../../utils/errors';
import type { LoginRequest } from '../../types/auth';

export default function LoginPage() {
  const { login } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);

  const redirectTo =
    (location.state as { from?: { pathname?: string } } | null)?.from?.pathname ?? '/dashboard';

  const onFinish = async (values: LoginRequest) => {
    setLoading(true);
    setError(null);
    try {
      await login(values);
      navigate(redirectTo, { replace: true });
    } catch (err) {
      setError(getErrorMessage(err, 'Invalid username or password'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout title="Sign in to Nihongo">
      {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} />}
      <Form layout="vertical" onFinish={onFinish} disabled={loading}>
        <Form.Item
          name="username"
          label="Username"
          rules={[{ required: true, message: 'Please enter your username' }]}
        >
          <Input prefix={<UserOutlined />} placeholder="username" autoComplete="username" />
        </Form.Item>
        <Form.Item
          name="password"
          label="Password"
          rules={[{ required: true, message: 'Please enter your password' }]}
        >
          <Input.Password
            prefix={<LockOutlined />}
            placeholder="password"
            autoComplete="current-password"
          />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" block loading={loading}>
            Sign in
          </Button>
        </Form.Item>
        <div style={{ textAlign: 'center' }}>
          Don&apos;t have an account? <Link to="/register">Register</Link>
        </div>
      </Form>
    </AuthLayout>
  );
}
