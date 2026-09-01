import { useEffect, useState } from 'react';
import { Button, message } from 'antd';
import { StarFilled, StarOutlined } from '@ant-design/icons';
import { bookmarkApi } from '../../api/bookmarkApi';
import { getErrorMessage } from '../../utils/errors';
import type { BookmarkTargetType } from '../../types/learning';

/**
 * Self-contained bookmark toggle for a single Vocabulary/Kanji/Grammar item
 * (section 23). Checks whether the item is already bookmarked on mount,
 * then creates/removes the bookmark on click.
 */
export function BookmarkButton({
  targetType,
  targetId,
}: {
  targetType: BookmarkTargetType;
  targetId: number;
}) {
  const [bookmarked, setBookmarked] = useState(false);
  const [checking, setChecking] = useState(true);
  const [toggling, setToggling] = useState(false);

  useEffect(() => {
    let cancelled = false;
    // Resetting to the loading state for a new target - not the
    // derived-state anti-pattern the react-hooks rule targets, since the
    // rest of this effect performs a real async status check.
    // eslint-disable-next-line react-hooks/set-state-in-effect
    setChecking(true);

    async function check() {
      try {
        const response = await bookmarkApi.exists(targetType, targetId);
        if (!cancelled) {
          setBookmarked(response.data.data);
        }
      } catch {
        // Non-fatal - the button just starts in the "not bookmarked" state.
      } finally {
        if (!cancelled) {
          setChecking(false);
        }
      }
    }

    void check();

    return () => {
      cancelled = true;
    };
  }, [targetType, targetId]);

  const toggle = async () => {
    setToggling(true);
    try {
      if (bookmarked) {
        const response = await bookmarkApi.list({ targetType, size: 200 });
        const match = response.data.data.content.find((b) => b.targetId === targetId);
        if (match) {
          await bookmarkApi.remove(match.id);
        }
        setBookmarked(false);
        message.success('Bookmark removed');
      } else {
        await bookmarkApi.create({ targetType, targetId });
        setBookmarked(true);
        message.success('Bookmarked');
      }
    } catch (err) {
      message.error(getErrorMessage(err, 'Failed to update bookmark'));
    } finally {
      setToggling(false);
    }
  };

  return (
    <Button
      icon={bookmarked ? <StarFilled style={{ color: '#faad14' }} /> : <StarOutlined />}
      loading={checking || toggling}
      onClick={toggle}
    >
      {bookmarked ? 'Bookmarked' : 'Bookmark'}
    </Button>
  );
}
