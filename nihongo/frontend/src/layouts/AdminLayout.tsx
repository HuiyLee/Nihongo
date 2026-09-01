import { useState, type ReactNode } from 'react';
import { Layout, Menu, Avatar, Dropdown, Typography, Tag } from 'antd';
import {
  DashboardOutlined,
  UserOutlined,
  ReadOutlined,
  BookOutlined,
  FontSizeOutlined,
  FormOutlined,
  TrophyOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';

const { Header, Sider, Content } = Layout;
const { Text } = Typography;

const NAV_ITEMS = [
  { key: '/admin', icon: <DashboardOutlined />, label: 'Dashboard' },
  { key: '/admin/users', icon: <UserOutlined />, label: 'Users' },
  { key: '/admin/levels', icon: <TrophyOutlined />, label: 'Levels' },
  { key: '/admin/lessons', icon: <BookOutlined />, label: 'Lessons' },
  { key: '/admin/vocabulary', icon: <FontSizeOutlined />, label: 'Vocabulary' },
  { key: '/admin/kanji', icon: <FormOutlined />, label: 'Kanji' },
  { key: '/admin/grammar', icon: <ReadOutlined />, label: 'Grammar' },
  { key: '/admin/exercises', icon: <FormOutlined />, label: 'Exercises' },
  { key: '/admin/exams', icon: <TrophyOutlined />, label: 'Exams' },
  { key: '/admin/readings', icon: <ReadOutlined />, label: 'Reading' },
];

export function AdminLayout({ children }: { children: ReactNode }) {
  const [collapsed, setCollapsed] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();

  const sortedByLength = [...NAV_ITEMS].sort((a, b) => b.key.length - a.key.length);
  const selectedKey =
    sortedByLength.find((item) => location.pathname.startsWith(item.key))?.key ?? '/admin';

  const handleLogout = async () => {
    await logout();
    navigate('/login', { replace: true });
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider collapsible collapsed={collapsed} onCollapse={setCollapsed} theme="dark">
        <div
          style={{
            height: 48,
            margin: 12,
            color: '#fff',
            fontWeight: 700,
            textAlign: 'center',
            whiteSpace: 'nowrap',
            overflow: 'hidden',
          }}
        >
          {collapsed ? '管' : 'Admin Panel'}
        </div>
        <Menu
          theme="dark"
          mode="inline"
          selectedKeys={[selectedKey]}
          items={NAV_ITEMS}
          onClick={({ key }) => navigate(key)}
        />
      </Sider>
      <Layout>
        <Header
          style={{
            background: '#fff',
            padding: '0 16px',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            borderBottom: '1px solid #f0f0f0',
          }}
        >
          <span
            onClick={() => setCollapsed(!collapsed)}
            style={{ cursor: 'pointer', fontSize: 18 }}
          >
            {collapsed ? <MenuUnfoldOutlined /> : <MenuFoldOutlined />}
          </span>
          <Dropdown
            menu={{
              items: [
                {
                  key: 'user-area',
                  icon: <UserOutlined />,
                  label: <Link to="/dashboard">Back to learning area</Link>,
                },
                { type: 'divider' },
                { key: 'logout', icon: <LogoutOutlined />, label: 'Logout', onClick: handleLogout },
              ],
            }}
          >
            <span style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
              <Tag color="gold">ADMIN</Tag>
              <Avatar icon={<UserOutlined />} />
              <Text>{user?.fullName ?? user?.username}</Text>
            </span>
          </Dropdown>
        </Header>
        <Content style={{ margin: 16 }}>
          <div style={{ background: '#fff', padding: 24, borderRadius: 8, minHeight: '80vh' }}>
            {children}
          </div>
        </Content>
      </Layout>
    </Layout>
  );
}
