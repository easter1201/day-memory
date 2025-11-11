import api from './axios';
import { GiftItem, GiftItemRequest } from '../types';

export const giftApi = {
  getGiftItems: async (userId: number): Promise<GiftItem[]> => {
    const response = await api.get(`/gifts?userId=${userId}`);
    return response.data;
  },

  getGiftItemsByEvent: async (eventId: number): Promise<GiftItem[]> => {
    const response = await api.get(`/gifts/event/${eventId}`);
    return response.data;
  },

  createGiftItem: async (userId: number, gift: GiftItemRequest): Promise<GiftItem> => {
    const response = await api.post(`/gifts?userId=${userId}`, gift);
    return response.data;
  },

  updateGiftItem: async (giftId: number, gift: GiftItemRequest): Promise<GiftItem> => {
    const response = await api.put(`/gifts/${giftId}`, gift);
    return response.data;
  },

  togglePurchaseStatus: async (giftId: number): Promise<GiftItem> => {
    const response = await api.patch(`/gifts/${giftId}/purchase`);
    return response.data;
  },

  deleteGiftItem: async (giftId: number): Promise<void> => {
    await api.delete(`/gifts/${giftId}`);
  },
};
