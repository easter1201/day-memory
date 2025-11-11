export const calculateDDay = (eventDate: string): string => {
  const today = new Date();
  today.setHours(0, 0, 0, 0);

  const target = new Date(eventDate);
  target.setHours(0, 0, 0, 0);

  const diffTime = target.getTime() - today.getTime();
  const diffDays = Math.ceil(diffTime / (1000 * 60 * 60 * 24));

  if (diffDays === 0) return "D-Day";
  if (diffDays > 0) return `D-${diffDays}`;
  return `D+${Math.abs(diffDays)}`;
};

export const getEventTypeBadgeColor = (eventType: string): string => {
  const colors: Record<string, string> = {
    BIRTHDAY: "bg-blue-100 text-blue-800",
    ANNIVERSARY: "bg-pink-100 text-pink-800",
    HOLIDAY: "bg-green-100 text-green-800",
    OTHER: "bg-gray-100 text-gray-800",
  };
  return colors[eventType] || colors.OTHER;
};

export const getEventTypeLabel = (eventType: string): string => {
  const labels: Record<string, string> = {
    BIRTHDAY: "생일",
    ANNIVERSARY: "기념일",
    HOLIDAY: "명절",
    OTHER: "기타",
  };
  return labels[eventType] || eventType;
};

export const getRelationshipLabel = (relationship?: string): string => {
  if (!relationship) return "-";

  const labels: Record<string, string> = {
    FAMILY: "가족",
    FRIEND: "친구",
    COLLEAGUE: "동료",
    ACQUAINTANCE: "지인",
    OTHER: "기타",
  };
  return labels[relationship] || relationship;
};

export const formatDate = (dateString: string): string => {
  const date = new Date(dateString);
  return date.toLocaleDateString('ko-KR', {
    year: 'numeric',
    month: 'long',
    day: 'numeric',
  });
};
