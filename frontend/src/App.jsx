import React from 'react';
import { Routes, Route, Navigate, useLocation } from 'react-router-dom';
import Layout from './components/Layout';
import StudentRegistration from './pages/StudentRegistration';
import Dashboard from './pages/Dashboard';
import Subjects from './pages/Subjects';
import Exams from './pages/Exams';
import Availability from './pages/Availability';
import Schedule from './pages/Schedule';

function App() {
  const location = useLocation();
  const studentId = localStorage.getItem('studentId');
  const isPublicPage = location.pathname === '/';

  if (!studentId && !isPublicPage) {
    return <Navigate to="/" replace />;
  }

  return (
    <Routes>
      <Route path="/" element={<StudentRegistration />} />
      <Route
        path="/*"
        element={
          <Layout>
            <Routes>
              <Route path="/dashboard" element={<Dashboard />} />
              <Route path="/subjects" element={<Subjects />} />
              <Route path="/exams" element={<Exams />} />
              <Route path="/availability" element={<Availability />} />
              <Route path="/schedule" element={<Schedule />} />
            </Routes>
          </Layout>
        }
      />
    </Routes>
  );
}

export default App;
