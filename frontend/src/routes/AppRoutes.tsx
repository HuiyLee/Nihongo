import type { ReactNode } from 'react';
import { Routes, Route } from 'react-router-dom';
import { ProtectedRoute } from './ProtectedRoute';
import { AdminRoute } from './AdminRoute';
import { MainLayout } from '../layouts/MainLayout';
import { AdminLayout } from '../layouts/AdminLayout';

import LandingPage from '../pages/LandingPage';
import LoginPage from '../pages/auth/LoginPage';
import RegisterPage from '../pages/auth/RegisterPage';
import DashboardPage from '../pages/DashboardPage';
import ProfilePage from '../pages/ProfilePage';
import ComingSoonPage from '../pages/ComingSoonPage';
import NotFoundPage from '../pages/NotFoundPage';
import AdminDashboardPage from '../pages/admin/AdminDashboardPage';

/** Wraps a page with the authenticated-user shell (route guard + sidebar layout). */
function userPage(node: ReactNode) {
  return (
    <ProtectedRoute>
      <MainLayout>{node}</MainLayout>
    </ProtectedRoute>
  );
}

/** Wraps a page with the admin-only shell (route guard + admin layout). */
function adminPage(node: ReactNode) {
  return (
    <AdminRoute>
      <AdminLayout>{node}</AdminLayout>
    </AdminRoute>
  );
}

export function AppRoutes() {
  return (
    <Routes>
      {/* Public */}
      <Route path="/" element={<LandingPage />} />
      <Route path="/login" element={<LoginPage />} />
      <Route path="/register" element={<RegisterPage />} />

      {/* User (protected) - section 27 */}
      <Route path="/dashboard" element={userPage(<DashboardPage />)} />
      <Route path="/lessons" element={userPage(<ComingSoonPage title="Lessons" />)} />
      <Route path="/lessons/:id" element={userPage(<ComingSoonPage title="Lesson detail" />)} />
      <Route path="/vocabulary" element={userPage(<ComingSoonPage title="Vocabulary" />)} />
      <Route
        path="/vocabulary/:id"
        element={userPage(<ComingSoonPage title="Vocabulary detail" />)}
      />
      <Route path="/kanji" element={userPage(<ComingSoonPage title="Kanji" />)} />
      <Route path="/kanji/:id" element={userPage(<ComingSoonPage title="Kanji detail" />)} />
      <Route path="/grammar" element={userPage(<ComingSoonPage title="Grammar" />)} />
      <Route path="/grammar/:id" element={userPage(<ComingSoonPage title="Grammar detail" />)} />
      <Route path="/listening" element={userPage(<ComingSoonPage title="Listening" />)} />
      <Route path="/reading" element={userPage(<ComingSoonPage title="Reading" />)} />
      <Route path="/exercises" element={userPage(<ComingSoonPage title="Exercises" />)} />
      <Route path="/exams" element={userPage(<ComingSoonPage title="JLPT Exams" />)} />
      <Route path="/exams/:id" element={userPage(<ComingSoonPage title="Exam" />)} />
      <Route path="/exams/:id/result" element={userPage(<ComingSoonPage title="Exam result" />)} />
      <Route path="/progress" element={userPage(<ComingSoonPage title="Progress" />)} />
      <Route path="/bookmarks" element={userPage(<ComingSoonPage title="Bookmarks" />)} />
      <Route path="/profile" element={userPage(<ProfilePage />)} />

      {/* Admin (protected + ROLE_ADMIN) - section 27 */}
      <Route path="/admin" element={adminPage(<AdminDashboardPage />)} />
      <Route path="/admin/users" element={adminPage(<ComingSoonPage title="User management" />)} />
      <Route
        path="/admin/levels"
        element={adminPage(<ComingSoonPage title="Level management" />)}
      />
      <Route
        path="/admin/lessons"
        element={adminPage(<ComingSoonPage title="Lesson management" />)}
      />
      <Route
        path="/admin/vocabulary"
        element={adminPage(<ComingSoonPage title="Vocabulary management" />)}
      />
      <Route path="/admin/kanji" element={adminPage(<ComingSoonPage title="Kanji management" />)} />
      <Route
        path="/admin/grammar"
        element={adminPage(<ComingSoonPage title="Grammar management" />)}
      />
      <Route
        path="/admin/exercises"
        element={adminPage(<ComingSoonPage title="Exercise management" />)}
      />
      <Route path="/admin/exams" element={adminPage(<ComingSoonPage title="Exam management" />)} />

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
