const features = [
  {
    icon: (
      <svg
        className="h-12 w-12"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M8 7V3m8 4V3m-9 8h10M5 21h14a2 2 0 002-2V7a2 2 0 00-2-2H5a2 2 0 00-2 2v12a2 2 0 002 2z"
        />
      </svg>
    ),
    title: "이벤트 관리",
    description:
      "생일, 기념일, 명절 등 소중한 날들을 한곳에서 관리하고 캘린더로 한눈에 확인하세요. 자동 리마인더로 더 이상 중요한 날을 놓치지 마세요.",
  },
  {
    icon: (
      <svg
        className="h-12 w-12"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M12 8v13m0-13V6a2 2 0 112 2h-2zm0 0V5.5A2.5 2.5 0 109.5 8H12zm-7 4h14M5 12a2 2 0 110-4h14a2 2 0 110 4M5 12v7a2 2 0 002 2h10a2 2 0 002-2v-7"
        />
      </svg>
    ),
    title: "AI 선물 추천",
    description:
      "받는 사람의 취향과 관계, 예산을 고려한 AI 기반 선물 추천으로 완벽한 선물을 찾아보세요. 더 이상 선물 고민은 그만!",
  },
  {
    icon: (
      <svg
        className="h-12 w-12"
        fill="none"
        stroke="currentColor"
        viewBox="0 0 24 24"
      >
        <path
          strokeLinecap="round"
          strokeLinejoin="round"
          strokeWidth={2}
          d="M15 17h5l-1.405-1.405A2.032 2.032 0 0118 14.158V11a6.002 6.002 0 00-4-5.659V5a2 2 0 10-4 0v.341C7.67 6.165 6 8.388 6 11v3.159c0 .538-.214 1.055-.595 1.436L4 17h5m6 0v1a3 3 0 11-6 0v-1m6 0H9"
        />
      </svg>
    ),
    title: "스마트 리마인더",
    description:
      "이메일과 SMS로 사전에 알림을 받아 여유롭게 준비하세요. 원하는 시간과 날짜를 설정하여 나만의 알림 스케줄을 만들 수 있습니다.",
  },
];

export const FeaturesSection = () => {
  return (
    <section className="py-20 md:py-32">
      <div className="container mx-auto px-4">
        <div className="mx-auto max-w-6xl">
          {/* Section Header */}
          <div className="mb-16 text-center">
            <h2 className="mb-4 text-3xl font-bold md:text-4xl">
              Day Memory의 주요 기능
            </h2>
            <p className="text-lg text-muted-foreground">
              소중한 사람들과의 특별한 순간을 더욱 의미있게 만들어드립니다
            </p>
          </div>

          {/* Features Grid */}
          <div className="grid gap-8 md:grid-cols-3">
            {features.map((feature, index) => (
              <div
                key={index}
                className="rounded-lg border bg-card p-8 shadow-sm transition-shadow hover:shadow-md"
              >
                <div className="mb-4 text-primary">{feature.icon}</div>
                <h3 className="mb-3 text-xl font-semibold">{feature.title}</h3>
                <p className="text-muted-foreground">{feature.description}</p>
              </div>
            ))}
          </div>
        </div>
      </div>
    </section>
  );
};
