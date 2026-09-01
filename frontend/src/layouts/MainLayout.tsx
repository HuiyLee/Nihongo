import { useState, type ReactNode } from 'react';
import { Layout, Menu, Avatar, Dropdown, Typography, Grid } from 'antd';
import {
  DashboardOutlined,
  BookOutlined,
  ReadOutlined,
  SoundOutlined,
  FontSizeOutlined,
  FormOutlined,
  FileTextOutlined,
  TrophyOutlined,
  StarOutlined,
  UserOutlined,
  LogoutOutlined,
  MenuFoldOutlined,
  MenuUnfoldOutlined,
} from '@ant-design/icons';
import { Link, useLocation, useNavigate } from 'react-router-dom';
import { useAuth } from '../hooks/useAuth';
import { NotificationBell } from '../components/NotificationBell';

const { Header, Sider, Content } = Layout;
const { Text } = Typography;

const NAV_ITEMS = [
  { key: '/dashboard', icon: <DashboardOutlined />, label: 'Dashboard' },
  { key: '/lessons', icon: <BookOutlined />, label: 'Lessons' },
  { key: '/vocabulary', icon: <FontSizeOutlined />, label: 'Vocabulary' },
  { key: '/kanji', icon: <FormOutlined />, label: 'Kanji' },
  { key: '/grammar', icon: <ReadOutlined />, label: 'Grammar' },
  { key: '/listening', icon: <SoundOutlined />, label: 'Listening' },
  { key: '/reading', icon: <FileTextOutlined />, label: 'Reading' },
  { key: '/exercises', icon: <FormOutlined />, label: 'Exercises' },
  { key: '/exams', icon: <TrophyOutlined />, label: 'JLPT Exams' },
  { key: '/progress', icon: <DashboardOutlined />, label: 'Progress' },
  { key: '/bookmarks', icon: <StarOutlined />, label: 'Bookmarks' },
];

export function MainLayout({ children }: { children: ReactNode }) {
  const [collapsed, setCollapsed] = useState(false);
  const { user, logout } = useAuth();
  const navigate = useNavigate();
  const location = useLocation();
  const screens = Grid.useBreakpoint();

  const selectedKey =
    NAV_ITEMS.find((item) => location.pathname.startsWith(item.key))?.key ?? '/dashboard';

  const handleLogout = async () => {
    await logout();
    navigate('/login', { replace: true });
  };

  return (
    <Layout style={{ minHeight: '100vh' }}>
      <Sider
        collapsible
        collapsed={collapsed}
        onCollapse={setCollapsed}
        breakpoint="lg"
        collapsedWidth={screens.xs ? 0 : 80}
      >
        <div
          style={{
            height: 48,
            margin: 12,
            color: '#fff',
            fontWeight: 700,
            fontSize: collapsed ? 16 : 18,
            textAlign: 'center',
            whiteSpace: 'nowrap',
            overflow: 'hidden',
          }}
        >
          {collapsed ? '日' : '日本語 Nihongo'}
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
          <div style={{ display: 'flex', alignItems: 'center', gap: 8 }}>
            <NotificationBell />
            <Dropdown
              menu={{
                items: [
                  {
                    key: 'profile',
                    icon: <UserOutlined />,
                    label: <Link to="/profile">Profile</Link>,
                  },
                  { type: 'divider' },
                  {
                    key: 'logout',
                    icon: <LogoutOutlined />,
                    label: 'Logout',
                    onClick: handleLogout,
                  },
                ],
              }}
            >
              <span style={{ cursor: 'pointer', display: 'flex', alignItems: 'center', gap: 8 }}>
                <Avatar icon={<UserOutlined />} />
                <Text>{user?.fullName ?? user?.username}</Text>
              </span>
            </Dropdown>
          </div>
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
