import type { ReactNode } from 'react';
import { Navigate, useLocation } from 'react-router-dom';
import { Result } from 'antd';
import { useAuth } from '../hooks/useAuth';

/** Same client-side caveat as ProtectedRoute: the backend enforces ROLE_ADMIN independently. */
export function AdminRoute({ children }: { children: ReactNode }) {
  const { user, isAuthenticated } = useAuth();
  const location = useLocation();

  if (!isAuthenticated) {
    return <Navigate to="/login" state={{ from: location }} replace />;
  }

  if (user?.role !== 'ROLE_ADMIN') {
    return (
      <Result
        status="403"
        title="403"
        subTitle="Sorry, you are not authorized to access this page."
      />
    );
  }

  return <>{children}</>;
}
