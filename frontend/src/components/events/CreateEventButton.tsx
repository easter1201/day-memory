import { useNavigate } from "react-router-dom";
import { Button } from "../ui/Button";

export const CreateEventButton = () => {
  const navigate = useNavigate();

  return (
    <Button
      onClick={() => navigate("/events/new")}
      className="fixed bottom-8 right-8 h-14 w-14 rounded-full shadow-lg"
      size="lg"
    >
      +
    </Button>
  );
};
