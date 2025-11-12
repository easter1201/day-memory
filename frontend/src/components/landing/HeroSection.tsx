import { useNavigate } from "react-router-dom";
import { Button } from "../ui/Button";

export const HeroSection = () => {
  const navigate = useNavigate();

  return (
    <section className="bg-gradient-to-b from-primary/10 to-background py-20 md:py-32">
      <div className="container mx-auto px-4">
        <div className="mx-auto max-w-4xl text-center">
          {/* Main Title */}
          <h1 className="mb-6 text-4xl font-bold tracking-tight md:text-5xl lg:text-6xl">
            소중한 사람들의 특별한 날을
            <br />
            <span className="text-primary">Day Memory</span>와 함께
          </h1>

          {/* Subtitle */}
          <p className="mb-8 text-lg text-muted-foreground md:text-xl">
            생일, 기념일, 명절까지 모든 이벤트를 한곳에서 관리하고
            <br className="hidden md:block" />
            AI가 추천하는 완벽한 선물로 특별함을 선물하세요
          </p>

          {/* CTA Buttons */}
          <div className="flex flex-col gap-4 sm:flex-row sm:justify-center">
            <Button
              size="lg"
              onClick={() => navigate("/signup")}
              className="text-lg"
            >
              무료로 시작하기
            </Button>
            <Button
              size="lg"
              variant="outline"
              onClick={() => navigate("/login")}
              className="text-lg"
            >
              로그인
            </Button>
          </div>

          {/* Feature Highlight */}
          <div className="mt-12 flex flex-wrap items-center justify-center gap-6 text-sm text-muted-foreground">
            <div className="flex items-center gap-2">
              <svg
                className="h-5 w-5 text-green-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M5 13l4 4L19 7"
                />
              </svg>
              <span>무료 사용</span>
            </div>
            <div className="flex items-center gap-2">
              <svg
                className="h-5 w-5 text-green-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M5 13l4 4L19 7"
                />
              </svg>
              <span>AI 선물 추천</span>
            </div>
            <div className="flex items-center gap-2">
              <svg
                className="h-5 w-5 text-green-600"
                fill="none"
                stroke="currentColor"
                viewBox="0 0 24 24"
              >
                <path
                  strokeLinecap="round"
                  strokeLinejoin="round"
                  strokeWidth={2}
                  d="M5 13l4 4L19 7"
                />
              </svg>
              <span>자동 리마인더</span>
            </div>
          </div>
        </div>
      </div>
    </section>
  );
};
