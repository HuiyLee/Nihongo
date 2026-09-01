import { Result } from 'antd';

/**
 * Generic placeholder for routes defined by the spec (section 27) but not
 * yet implemented in Phase 1 (Lessons, Vocabulary, Kanji, Grammar, Listening,
 * Reading, Exercises, Exams, Progress, Bookmarks, and all Admin CRUD pages).
 */
export default function ComingSoonPage({ title }: { title: string }) {
  return (
    <Result
      status="info"
      title={title}
      subTitle="This feature is planned for a later development phase."
    />
  );
}
