package com.yoonji.adminproject.admin.dto.response;

import com.querydsl.core.annotations.QueryProjection;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
public class NewUserStatisticsResponse {
    private String timeUnit;
    private LocalDate startDate;
    private LocalDate endDate;
    private int totalNewUsers;
    private List<PeriodStatistics> statistics;
    private double averageNewUsersPerPeriod;
    private PeakNewUsers maxNewUsers;
    private PeakNewUsers minNewUsers;

    @Builder
    public NewUserStatisticsResponse(String timeUnit, LocalDate startDate, LocalDate endDate, int totalNewUsers, List<PeriodStatistics> statistics, double averageNewUsersPerPeriod, PeakNewUsers maxNewUsers, PeakNewUsers minNewUsers) {
        this.timeUnit = timeUnit;
        this.startDate = startDate;
        this.endDate = endDate;
        this.totalNewUsers = totalNewUsers;
        this.statistics = statistics;
        this.averageNewUsersPerPeriod = averageNewUsersPerPeriod;
        this.maxNewUsers = maxNewUsers;
        this.minNewUsers = minNewUsers;
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class PeriodStatistics {
        private String period;
        private int newUsers;
        private Double growthRate;

        @QueryProjection
        public PeriodStatistics(String period, int newUsers) {
            this.period = period;
            this.newUsers = newUsers;
        }

        @Builder
        public PeriodStatistics(String period, int newUsers, Double growthRate) {
            this.period = period;
            this.newUsers = newUsers;
            this.growthRate = growthRate;
        }
    }

    @Getter @Setter
    @NoArgsConstructor
    public static class PeakNewUsers {
        private String period;
        private int count;

        @Builder
        public PeakNewUsers(String period, int count) {
            this.period = period;
            this.count = count;
        }
    }
}
