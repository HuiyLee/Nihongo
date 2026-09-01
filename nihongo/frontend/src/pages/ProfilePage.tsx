import { Card, Descriptions } from 'antd';
import { useAuth } from '../hooks/useAuth';

export default function ProfilePage() {
  const { user } = useAuth();

  return (
    <Card title="My profile">
      <Descriptions column={1} bordered>
        <Descriptions.Item label="Username">{user?.username}</Descriptions.Item>
        <Descriptions.Item label="Full name">{user?.fullName}</Descriptions.Item>
        <Descriptions.Item label="Email">{user?.email}</Descriptions.Item>
        <Descriptions.Item label="Role">{user?.role}</Descriptions.Item>
      </Descriptions>
    </Card>
  );
}
