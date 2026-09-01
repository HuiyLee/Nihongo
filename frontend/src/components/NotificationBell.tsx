import { useCallback, useEffect, useState } from 'react';
import { Badge, Button, Dropdown, Empty, List, Typography, message } from 'antd';
import { BellOutlined } from '@ant-design/icons';
import { notificationApi } from '../api/notificationApi';
import { getErrorMessage } from '../utils/errors';
import type { Notification } from '../types/notification';

const { Text } = Typography;

const POLL_INTERVAL_MS = 60_000;

/**
 * Requirements section 24. Header bell showing unread notifications - polls
 * periodically so the badge doesn't go stale during a long session, and
 * also refreshes whenever the dropdown is opened.
 */
export function NotificationBell() {
  const [items, setItems] = useState<Notification[]>([]);
  const [unreadCount, setUnreadCount] = useState(0);
  const [open, setOpen] = useState(false);

  const refresh = useCallback(async () => {
    try {
      const [listResponse, countResponse] = await Promise.all([
        notificationApi.list({ page: 0, size: 10 }),
        notificationApi.unreadCount(),
      ]);
      setItems(listResponse.data.data.content);
      setUnreadCount(countResponse.data.data.count);
    } catch {
      // Non-fatal - the bell just keeps showing its last known state.
    }
  }, []);

  useEffect(() => {
    // Initial load, plus a light poll so the badge doesn't go stale - the
    // fetch itself is async, so this isn't the derived-state anti-pattern
    // the react-hooks rule targets (see CrudManager.tsx).
    // eslint-disable-next-line react-hooks/set-state-in-effect
    void refresh();
    const interval = window.setInterval(() => void refresh(), POLL_INTERVAL_MS);
    return () => window.clearInterval(interval);
  }, [refresh]);

  const handleOpenChange = (next: boolean) => {
    setOpen(next);
    if (next) {
      void refresh();
    }
  };

  const markAllRead = async () => {
    try {
      await notificationApi.markAllRead();
      await refresh();
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to mark notifications as read'));
    }
  };

  const handleItemClick = async (item: Notification) => {
    if (item.read) {
      return;
    }
    try {
      await notificationApi.markRead(item.id);
      await refresh();
    } catch {
      // Non-fatal - the item just stays marked unread until the next refresh.
    }
  };

  return (
    <Dropdown
      trigger={['click']}
      open={open}
      onOpenChange={handleOpenChange}
      popupRender={() => (
        <div
          style={{
            width: 320,
            background: '#fff',
            borderRadius: 8,
            boxShadow: '0 2px 8px rgba(0,0,0,0.15)',
          }}
        >
          <div
            style={{
              display: 'flex',
              justifyContent: 'space-between',
              alignItems: 'center',
              padding: '8px 12px',
            }}
          >
            <Text strong>Notifications</Text>
            <Button type="link" size="small" disabled={unreadCount === 0} onClick={markAllRead}>
              Mark all read
            </Button>
          </div>
          {items.length === 0 ? (
            <Empty description="No notifications" style={{ padding: 16 }} />
          ) : (
            <List
              style={{ maxHeight: 360, overflowY: 'auto' }}
              dataSource={items}
              renderItem={(item) => (
                <List.Item
                  style={{
                    padding: '8px 12px',
                    cursor: 'pointer',
                    background: item.read ? undefined : '#e6f4ff',
                  }}
                  onClick={() => void handleItemClick(item)}
                >
                  <List.Item.Meta
                    title={<Text strong={!item.read}>{item.title}</Text>}
                    description={item.content}
                  />
                </List.Item>
              )}
            />
          )}
        </div>
      )}
    >
      <Badge count={unreadCount} size="small">
        <Button type="text" icon={<BellOutlined style={{ fontSize: 18 }} />} />
      </Badge>
    </Dropdown>
  );
}
