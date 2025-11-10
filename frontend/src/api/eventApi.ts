import api from './axios';
import { Event, EventRequest } from '../types';

export const eventApi = {
  getEvents: async (userId: number): Promise<Event[]> => {
    const response = await api.get(`/events?userId=${userId}`);
    return response.data;
  },

  getEvent: async (eventId: number): Promise<Event> => {
    const response = await api.get(`/events/${eventId}`);
    return response.data;
  },

  createEvent: async (userId: number, event: EventRequest): Promise<Event> => {
    const response = await api.post(`/events?userId=${userId}`, event);
    return response.data;
  },

  updateEvent: async (eventId: number, event: EventRequest): Promise<Event> => {
    const response = await api.put(`/events/${eventId}`, event);
    return response.data;
  },

  deleteEvent: async (eventId: number): Promise<void> => {
    await api.delete(`/events/${eventId}`);
  },

  getUpcomingEvents: async (userId: number, days: number = 30): Promise<Event[]> => {
    const response = await api.get(`/events/upcoming?userId=${userId}&days=${days}`);
    return response.data;
  },
};
