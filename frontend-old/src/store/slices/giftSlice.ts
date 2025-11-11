import { createSlice, createAsyncThunk, PayloadAction } from '@reduxjs/toolkit';
import { GiftItem, GiftItemRequest } from '../../types';
import { giftApi } from '../../api/giftApi';

interface GiftState {
  gifts: GiftItem[];
  eventGifts: GiftItem[];
  loading: boolean;
  error: string | null;
}

const initialState: GiftState = {
  gifts: [],
  eventGifts: [],
  loading: false,
  error: null,
};

export const fetchGiftItems = createAsyncThunk(
  'gifts/fetchGiftItems',
  async (userId: number) => {
    return await giftApi.getGiftItems(userId);
  }
);

export const fetchGiftItemsByEvent = createAsyncThunk(
  'gifts/fetchGiftItemsByEvent',
  async (eventId: number) => {
    return await giftApi.getGiftItemsByEvent(eventId);
  }
);

export const createGiftItem = createAsyncThunk(
  'gifts/createGiftItem',
  async ({ userId, gift }: { userId: number; gift: GiftItemRequest }) => {
    return await giftApi.createGiftItem(userId, gift);
  }
);

export const updateGiftItem = createAsyncThunk(
  'gifts/updateGiftItem',
  async ({ giftId, gift }: { giftId: number; gift: GiftItemRequest }) => {
    return await giftApi.updateGiftItem(giftId, gift);
  }
);

export const togglePurchaseStatus = createAsyncThunk(
  'gifts/togglePurchaseStatus',
  async (giftId: number) => {
    return await giftApi.togglePurchaseStatus(giftId);
  }
);

export const deleteGiftItem = createAsyncThunk(
  'gifts/deleteGiftItem',
  async (giftId: number) => {
    await giftApi.deleteGiftItem(giftId);
    return giftId;
  }
);

const giftSlice = createSlice({
  name: 'gifts',
  initialState,
  reducers: {
    clearError: (state) => {
      state.error = null;
    },
  },
  extraReducers: (builder) => {
    builder
      // Fetch Gift Items
      .addCase(fetchGiftItems.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchGiftItems.fulfilled, (state, action) => {
        state.loading = false;
        state.gifts = action.payload;
      })
      .addCase(fetchGiftItems.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch gift items';
      })
      // Fetch Gift Items by Event
      .addCase(fetchGiftItemsByEvent.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(fetchGiftItemsByEvent.fulfilled, (state, action) => {
        state.loading = false;
        state.eventGifts = action.payload;
      })
      .addCase(fetchGiftItemsByEvent.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to fetch event gift items';
      })
      // Create Gift Item
      .addCase(createGiftItem.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(createGiftItem.fulfilled, (state, action) => {
        state.loading = false;
        state.gifts.push(action.payload);
      })
      .addCase(createGiftItem.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to create gift item';
      })
      // Update Gift Item
      .addCase(updateGiftItem.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(updateGiftItem.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.gifts.findIndex((g) => g.id === action.payload.id);
        if (index !== -1) {
          state.gifts[index] = action.payload;
        }
      })
      .addCase(updateGiftItem.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to update gift item';
      })
      // Toggle Purchase Status
      .addCase(togglePurchaseStatus.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(togglePurchaseStatus.fulfilled, (state, action) => {
        state.loading = false;
        const index = state.gifts.findIndex((g) => g.id === action.payload.id);
        if (index !== -1) {
          state.gifts[index] = action.payload;
        }
      })
      .addCase(togglePurchaseStatus.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to toggle purchase status';
      })
      // Delete Gift Item
      .addCase(deleteGiftItem.pending, (state) => {
        state.loading = true;
        state.error = null;
      })
      .addCase(deleteGiftItem.fulfilled, (state, action) => {
        state.loading = false;
        state.gifts = state.gifts.filter((g) => g.id !== action.payload);
      })
      .addCase(deleteGiftItem.rejected, (state, action) => {
        state.loading = false;
        state.error = action.error.message || 'Failed to delete gift item';
      });
  },
});

export const { clearError } = giftSlice.actions;
export default giftSlice.reducer;
