import { Result, Button } from 'antd';

/** Reusable error placeholder for failed API calls. */
export function ErrorState({
  message = 'Something went wrong. Please try again.',
  onRetry,
}: {
  message?: string;
  onRetry?: () => void;
}) {
  return (
    <Result
      status="error"
      title="Error"
      subTitle={message}
      extra={
        onRetry && (
          <Button type="primary" onClick={onRetry}>
            Retry
          </Button>
        )
      }
    />
  );
}
