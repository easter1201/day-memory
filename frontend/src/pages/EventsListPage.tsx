import { useState } from "react";
import { PageLayout } from "../components/layout/PageLayout";
import { FilterTabs } from "../components/events/FilterTabs";
import { SearchBar } from "../components/events/SearchBar";
import { EventCardGrid } from "../components/events/EventCardGrid";
import { Pagination } from "../components/events/Pagination";
import { CreateEventButton } from "../components/events/CreateEventButton";
import { useGetEventsQuery } from "../store/services/eventsApi";

export const EventsListPage = () => {
  const [filter, setFilter] = useState<"all" | "upcoming" | "past">("all");
  const [search, setSearch] = useState("");
  const [page, setPage] = useState(1);
  const pageSize = 12;

  const { data, isLoading, error } = useGetEventsQuery({
    page: 0,
    size: 1000, // Get all events for client-side filtering
  });

  // Client-side filtering
  const filteredEvents = data?.content.filter(event => {
    // Search filter
    if (search && !event.title.toLowerCase().includes(search.toLowerCase())) {
      return false;
    }

    // Date filter
    const today = new Date();
    today.setHours(0, 0, 0, 0);
    const eventDate = new Date(event.eventDate);
    eventDate.setHours(0, 0, 0, 0);

    if (filter === "upcoming") {
      return eventDate >= today;
    } else if (filter === "past") {
      return eventDate < today;
    }

    return true; // "all" filter
  }) || [];

  // Pagination
  const totalPages = Math.ceil(filteredEvents.length / pageSize);
  const paginatedEvents = filteredEvents.slice((page - 1) * pageSize, page * pageSize);

  const handleFilterChange = (newFilter: "all" | "upcoming" | "past") => {
    setFilter(newFilter);
    setPage(1);
  };

  const handleSearch = (query: string) => {
    setSearch(query);
    setPage(1);
  };

  const handlePageChange = (newPage: number) => {
    setPage(newPage);
  };

  if (isLoading) {
    return (
      <PageLayout>
        <div className="flex h-full items-center justify-center">
          <p className="text-muted-foreground">로딩 중...</p>
        </div>
      </PageLayout>
    );
  }

  if (error || !data) {
    return (
      <PageLayout>
        <div className="flex h-full items-center justify-center">
          <p className="text-red-500">이벤트를 불러오는데 실패했습니다.</p>
        </div>
      </PageLayout>
    );
  }

  return (
    <PageLayout>
      <div className="space-y-6">
        <div className="flex items-center justify-between">
          <h1 className="text-2xl font-bold">이벤트 관리</h1>
        </div>

        <FilterTabs activeFilter={filter} onFilterChange={handleFilterChange} />

        <SearchBar onSearch={handleSearch} />

        <EventCardGrid events={paginatedEvents} />

        {totalPages > 1 && (
          <Pagination
            currentPage={page}
            totalPages={totalPages}
            onPageChange={handlePageChange}
          />
        )}

        <CreateEventButton />
      </div>
    </PageLayout>
  );
};
