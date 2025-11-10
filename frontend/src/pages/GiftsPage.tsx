import React, { useEffect } from 'react';
import { useAppDispatch, useAppSelector } from '../store/hooks';
import { fetchGiftItems, togglePurchaseStatus } from '../store/slices/giftSlice';

const GiftsPage: React.FC = () => {
  const dispatch = useAppDispatch();
  const { gifts, loading } = useAppSelector((state) => state.gifts);

  useEffect(() => {
    // TODO: Get actual userId from auth context
    dispatch(fetchGiftItems(1));
  }, [dispatch]);

  const handleTogglePurchase = (giftId: number) => {
    dispatch(togglePurchaseStatus(giftId));
  };

  if (loading) {
    return <div>Loading...</div>;
  }

  return (
    <div style={{ padding: '20px' }}>
      <h1>My Gift Ideas</h1>
      {gifts.length === 0 ? (
        <p>No gift ideas yet. Add your first gift!</p>
      ) : (
        <div>
          {gifts.map((gift) => (
            <div
              key={gift.id}
              style={{
                border: '1px solid #ccc',
                padding: '15px',
                marginBottom: '10px',
                borderRadius: '5px',
                backgroundColor: gift.isPurchased ? '#e8f5e9' : '#fff',
              }}
            >
              <h3>{gift.name}</h3>
              <p>{gift.description}</p>
              {gift.price && (
                <p>
                  <strong>Price:</strong> ${gift.price}
                </p>
              )}
              <p>
                <strong>Category:</strong> {gift.category}
              </p>
              {gift.url && (
                <p>
                  <a href={gift.url} target="_blank" rel="noopener noreferrer">
                    View Product
                  </a>
                </p>
              )}
              <button
                onClick={() => handleTogglePurchase(gift.id)}
                style={{
                  padding: '8px 16px',
                  backgroundColor: gift.isPurchased ? '#ff9800' : '#4caf50',
                  color: 'white',
                  border: 'none',
                  borderRadius: '4px',
                  cursor: 'pointer',
                }}
              >
                {gift.isPurchased ? 'Mark as Not Purchased' : 'Mark as Purchased'}
              </button>
            </div>
          ))}
        </div>
      )}
    </div>
  );
};

export default GiftsPage;
