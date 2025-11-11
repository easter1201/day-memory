import { useNavigate } from "react-router-dom";
import { Button } from "../ui/Button";

export const QuickActionButtons = () => {
  const navigate = useNavigate();

  const actions = [
    { label: "새 이벤트 추가", path: "/events/new", variant: "default" as const },
    { label: "새 선물 추가", path: "/gifts/new", variant: "secondary" as const },
    { label: "AI 추천 요청", path: "/recommendations", variant: "outline" as const },
  ];

  return (
    <div className="flex flex-wrap gap-4">
      {actions.map((action) => (
        <Button
          key={action.label}
          variant={action.variant}
          onClick={() => navigate(action.path)}
        >
          {action.label}
        </Button>
      ))}
    </div>
  );
};
