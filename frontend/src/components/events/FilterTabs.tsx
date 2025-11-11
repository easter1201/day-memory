interface FilterTabsProps {
  activeFilter: "all" | "upcoming" | "past";
  onFilterChange: (filter: "all" | "upcoming" | "past") => void;
}

export const FilterTabs = ({ activeFilter, onFilterChange }: FilterTabsProps) => {
  const tabs = [
    { id: "all" as const, label: "전체" },
    { id: "upcoming" as const, label: "다가오는 이벤트" },
    { id: "past" as const, label: "지난 이벤트" },
  ];

  return (
    <div className="flex space-x-2 border-b">
      {tabs.map((tab) => (
        <button
          key={tab.id}
          onClick={() => onFilterChange(tab.id)}
          className={`px-4 py-2 font-medium transition-colors ${
            activeFilter === tab.id
              ? "border-b-2 border-primary text-primary"
              : "text-muted-foreground hover:text-foreground"
          }`}
        >
          {tab.label}
        </button>
      ))}
    </div>
  );
};
