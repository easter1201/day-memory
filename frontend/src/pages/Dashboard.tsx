import React, { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { fetchUpcomingEvents } from '../store/slices/eventSlice';

const Dashboard: React.FC = () => {
  const dispatch = useAppDispatch();
  const { upcomingEvents, loading } = useAppSelector((state) => state.events);

  useEffect(() => {
    // TODO: Get actual userId from auth context
    dispatch(fetchUpcomingEvents({ userId: 1, days: 30 }));
  }, [dispatch]);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div style={{ padding: '20px' }}>
      <h1>Dashboard</h1>
      <h2>Upcoming Events (Next 30 Days)</h2>
      {upcomingEvents.length === 0 ? (
        <p>No upcoming events</p>
      ) : (
        <div>
          {upcomingEvents.map((event) => (
            <div
              key={event.id}
              style={{
                border: '1px solid #ccc',
                padding: '15px',
                marginBottom: '10px',
                borderRadius: '5px',
              }}
            >
              <h3>{event.title}</h3>
              <p>{event.description}</p>
              <p>
                <strong>Date:</strong> {event.eventDate}
              </p>
              <p>
                <strong>D-Day:</strong>{' '}
                {event.dDay > 0
                  ? `${event.dDay} days left`
                  : event.dDay === 0
                  ? 'Today!'
                  : `${Math.abs(event.dDay)} days ago`}
              </p>
              <p>
                <strong>Type:</strong> {event.eventType}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default Dashboard;
