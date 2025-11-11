import React, { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { fetchEvents } from '../store/slices/eventSlice';

const EventsPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { events, loading } = useAppSelector((state) => state.events);

  useEffect(() => {
    // TODO: Get actual userId from auth context
    dispatch(fetchEvents(1));
  }, [dispatch]);

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div style={{ padding: '20px' }}>
      <h1>My Events</h1>
      {events.length === 0 ? (
        <p>No events yet. Create your first event!</p>
      ) : (
        <div>
          {events.map((event) => (
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
              <p>
                <strong>Recurring:</strong> {event.isRecurring ? 'Yes' : 'No'}
              </p>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default EventsPage;
