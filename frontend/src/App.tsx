import { ConfigProvider } from 'antd';
import { BrowserRouter } from 'react-router-dom';
import { AuthProvider } from './contexts/AuthContext';
import { AppRoutes } from './routes/AppRoutes';

function App() {
  return (
    <ConfigProvider theme={{ token: { colorPrimary: '#c0392b' } }}>
      <BrowserRouter>
        <AuthProvider>
          <AppRoutes />
        </AuthProvider>
      </BrowserRouter>
    </ConfigProvider>
  );
}

export default App;
