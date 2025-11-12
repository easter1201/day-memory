import { useSelector } from "react-redux";
import type { RootState } from "../../store";

export const WelcomeBanner = () => {
  const user = useSelector((state: RootState) => state.auth.user);
  const currentHour = new Date().getHours();

  const getGreeting = () => {
    if (currentHour < 12) return "좋은 아침입니다";
    if (currentHour < 18) return "좋은 오후입니다";
    return "좋은 저녁입니다";
  };

  return (
    <div className="rounded-lg bg-gradient-to-r from-primary to-primary/80 p-6 text-primary-foreground">
      <h1 className="text-2xl font-bold">
        {getGreeting()}, {user?.name || "사용자"}님!
      </h1>
      <p className="mt-2 text-primary-foreground/90">
        오늘도 소중한 사람들의 특별한 날을 기억해보세요.
      </p>
    </div>
  );
};
