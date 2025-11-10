import React from 'react';
import { BrowserRouter as Router, Routes, Route, Link } from 'react-router-dom';
import { Provider } from 'react-redux';
import { store } from './store';
import Dashboard from './pages/Dashboard';
import EventsPage from './pages/EventsPage';
import GiftsPage from './pages/GiftsPage';
import './App.css';

function App() {
  return (
    <Provider store={store}>
      <Router>
        <div className="App">
          <nav style={{
            padding: '20px',
            backgroundColor: '#282c34',
            color: 'white',
            marginBottom: '20px'
          }}>
            <Link to="/" style={{ margin: '0 15px', color: 'white' }}>Dashboard</Link>
            <Link to="/events" style={{ margin: '0 15px', color: 'white' }}>Events</Link>
            <Link to="/gifts" style={{ margin: '0 15px', color: 'white' }}>Gifts</Link>
          </nav>

          <Routes>
            <Route path="/" element={<Dashboard />} />
            <Route path="/events" element={<EventsPage />} />
            <Route path="/gifts" element={<GiftsPage />} />
          </Routes>
        </div>
      </Router>
    </Provider>
  );
}

export default App;
