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
import LevelManagementPage from '../pages/admin/LevelManagementPage';
import LessonManagementPage from '../pages/admin/LessonManagementPage';
import VocabularyManagementPage from '../pages/admin/VocabularyManagementPage';
import KanjiManagementPage from '../pages/admin/KanjiManagementPage';
import GrammarManagementPage from '../pages/admin/GrammarManagementPage';
import VocabularyListPage from '../pages/vocabulary/VocabularyListPage';
import VocabularyFlashcardPage from '../pages/vocabulary/VocabularyFlashcardPage';
import KanjiListPage from '../pages/kanji/KanjiListPage';
import KanjiFlashcardPage from '../pages/kanji/KanjiFlashcardPage';
import GrammarListPage from '../pages/grammar/GrammarListPage';
import GrammarFlashcardPage from '../pages/grammar/GrammarFlashcardPage';
import BookmarksPage from '../pages/BookmarksPage';
import ExerciseListPage from '../pages/exercises/ExerciseListPage';
import ExerciseAttemptPage from '../pages/exercises/ExerciseAttemptPage';
import ExerciseManagementPage from '../pages/admin/ExerciseManagementPage';
import ExamListPage from '../pages/exams/ExamListPage';
import ExamAttemptPage from '../pages/exams/ExamAttemptPage';
import ExamResultPage from '../pages/exams/ExamResultPage';
import ExamManagementPage from '../pages/admin/ExamManagementPage';

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
      <Route path="/vocabulary" element={userPage(<VocabularyListPage />)} />
      <Route path="/vocabulary/:id" element={userPage(<VocabularyFlashcardPage />)} />
      <Route path="/kanji" element={userPage(<KanjiListPage />)} />
      <Route path="/kanji/:id" element={userPage(<KanjiFlashcardPage />)} />
      <Route path="/grammar" element={userPage(<GrammarListPage />)} />
      <Route path="/grammar/:id" element={userPage(<GrammarFlashcardPage />)} />
      <Route path="/listening" element={userPage(<ComingSoonPage title="Listening" />)} />
      <Route path="/reading" element={userPage(<ComingSoonPage title="Reading" />)} />
      <Route path="/exercises" element={userPage(<ExerciseListPage />)} />
      <Route path="/exercises/:id" element={userPage(<ExerciseAttemptPage />)} />
      <Route path="/exams" element={userPage(<ExamListPage />)} />
      <Route path="/exams/:id" element={userPage(<ExamAttemptPage />)} />
      <Route path="/exams/:id/result" element={userPage(<ExamResultPage />)} />
      <Route path="/progress" element={userPage(<ComingSoonPage title="Progress" />)} />
      <Route path="/bookmarks" element={userPage(<BookmarksPage />)} />
      <Route path="/profile" element={userPage(<ProfilePage />)} />

      {/* Admin (protected + ROLE_ADMIN) - section 27 */}
      <Route path="/admin" element={adminPage(<AdminDashboardPage />)} />
      <Route path="/admin/users" element={adminPage(<ComingSoonPage title="User management" />)} />
      <Route path="/admin/levels" element={adminPage(<LevelManagementPage />)} />
      <Route path="/admin/lessons" element={adminPage(<LessonManagementPage />)} />
      <Route path="/admin/vocabulary" element={adminPage(<VocabularyManagementPage />)} />
      <Route path="/admin/kanji" element={adminPage(<KanjiManagementPage />)} />
      <Route path="/admin/grammar" element={adminPage(<GrammarManagementPage />)} />
      <Route path="/admin/exercises" element={adminPage(<ExerciseManagementPage />)} />
      <Route path="/admin/exams" element={adminPage(<ExamManagementPage />)} />

      <Route path="*" element={<NotFoundPage />} />
    </Routes>
  );
}
