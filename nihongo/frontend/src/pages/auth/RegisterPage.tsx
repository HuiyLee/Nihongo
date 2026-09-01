import { useState } from 'react';
import { Form, Input, Button, Alert } from 'antd';
import { UserOutlined, LockOutlined, MailOutlined, IdcardOutlined } from '@ant-design/icons';
import { Link, useNavigate } from 'react-router-dom';
import { AuthLayout } from '../../layouts/AuthLayout';
import { useAuth } from '../../hooks/useAuth';
import { getErrorMessage } from '../../utils/errors';
import type { RegisterRequest } from '../../types/auth';

interface RegisterFormValues extends RegisterRequest {
  confirmPassword: string;
}

export default function RegisterPage() {
  const { register } = useAuth();
  const navigate = useNavigate();
  const [loading, setLoading] = useState(false);
  const [error, setError] = useState<string | null>(null);
  const [success, setSuccess] = useState(false);

  const onFinish = async (values: RegisterFormValues) => {
    setLoading(true);
    setError(null);
    try {
      const payload: RegisterRequest = {
        username: values.username,
        email: values.email,
        password: values.password,
        fullName: values.fullName,
      };
      await register(payload);
      setSuccess(true);
      setTimeout(() => navigate('/login', { replace: true }), 1200);
    } catch (err) {
      setError(getErrorMessage(err, 'Registration failed'));
    } finally {
      setLoading(false);
    }
  };

  return (
    <AuthLayout title="Create your account">
      {error && <Alert type="error" message={error} showIcon style={{ marginBottom: 16 }} />}
      {success && (
        <Alert
          type="success"
          message="Account created! Redirecting to sign in..."
          showIcon
          style={{ marginBottom: 16 }}
        />
      )}
      <Form layout="vertical" onFinish={onFinish} disabled={loading || success}>
        <Form.Item
          name="fullName"
          label="Full name"
          rules={[{ required: true, message: 'Please enter your full name' }]}
        >
          <Input prefix={<IdcardOutlined />} placeholder="Full name" />
        </Form.Item>
        <Form.Item
          name="username"
          label="Username"
          rules={[
            { required: true, message: 'Please enter a username' },
            { min: 3, message: 'Username must be at least 3 characters' },
            {
              pattern: /^[a-zA-Z0-9_.]+$/,
              message: 'Only letters, digits, dot and underscore are allowed',
            },
          ]}
        >
          <Input prefix={<UserOutlined />} placeholder="username" autoComplete="username" />
        </Form.Item>
        <Form.Item
          name="email"
          label="Email"
          rules={[
            { required: true, message: 'Please enter your email' },
            { type: 'email', message: 'Please enter a valid email' },
          ]}
        >
          <Input prefix={<MailOutlined />} placeholder="you@example.com" autoComplete="email" />
        </Form.Item>
        <Form.Item
          name="password"
          label="Password"
          hasFeedback
          rules={[
            { required: true, message: 'Please enter a password' },
            { min: 8, message: 'Password must be at least 8 characters' },
            {
              pattern: /^(?=.*[a-z])(?=.*[A-Z])(?=.*\d).+$/,
              message: 'Must include an uppercase letter, a lowercase letter and a digit',
            },
          ]}
        >
          <Input.Password
            prefix={<LockOutlined />}
            placeholder="password"
            autoComplete="new-password"
          />
        </Form.Item>
        <Form.Item
          name="confirmPassword"
          label="Confirm password"
          dependencies={['password']}
          hasFeedback
          rules={[
            { required: true, message: 'Please confirm your password' },
            ({ getFieldValue }) => ({
              validator(_, value) {
                if (!value || getFieldValue('password') === value) {
                  return Promise.resolve();
                }
                return Promise.reject(new Error('Passwords do not match'));
              },
            }),
          ]}
        >
          <Input.Password placeholder="confirm password" autoComplete="new-password" />
        </Form.Item>
        <Form.Item>
          <Button type="primary" htmlType="submit" block loading={loading}>
            Create account
          </Button>
        </Form.Item>
        <div style={{ textAlign: 'center' }}>
          Already have an account? <Link to="/login">Sign in</Link>
        </div>
      </Form>
    </AuthLayout>
  );
}
