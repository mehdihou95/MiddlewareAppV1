package com.xml.processor.service;

import com.xml.processor.model.Client;
import com.xml.processor.service.interfaces.ClientService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

@Service
public class ClientPerformanceMonitor {

    @Autowired
    private ClientService clientService;

    private final Map<Long, ClientMetrics> clientMetrics = new ConcurrentHashMap<>();

    public void recordRequest(Long clientId, long processingTime) {
        clientMetrics.computeIfAbsent(clientId, k -> new ClientMetrics())
                .recordRequest(processingTime);
    }

    public void recordError(Long clientId) {
        clientMetrics.computeIfAbsent(clientId, k -> new ClientMetrics())
                .recordError();
    }

    @Scheduled(fixedRate = 300000) // Every 5 minutes
    public void generatePerformanceReport() {
        LocalDateTime now = LocalDateTime.now();
        for (Map.Entry<Long, ClientMetrics> entry : clientMetrics.entrySet()) {
            Long clientId = entry.getKey();
            ClientMetrics metrics = entry.getValue();
            
            // Generate report for the last 5 minutes
            PerformanceReport report = metrics.generateReport(now);
            
            // Log or store the report
            logPerformanceReport(clientId, report);
            
            // Reset metrics for the next period
            metrics.reset();
        }
    }

    private void logPerformanceReport(Long clientId, PerformanceReport report) {
        // TODO: Implement actual logging or storage of performance reports
        System.out.printf("Performance Report for Client %d:%n", clientId);
        System.out.printf("Total Requests: %d%n", report.getTotalRequests());
        System.out.printf("Average Processing Time: %.2f ms%n", report.getAverageProcessingTime());
        System.out.printf("Error Rate: %.2f%%%n", report.getErrorRate());
        System.out.printf("Peak Processing Time: %d ms%n", report.getPeakProcessingTime());
    }

    private static class ClientMetrics {
        private final AtomicLong totalRequests = new AtomicLong(0);
        private final AtomicLong totalProcessingTime = new AtomicLong(0);
        private final AtomicLong totalErrors = new AtomicLong(0);
        private final AtomicLong peakProcessingTime = new AtomicLong(0);

        public void recordRequest(long processingTime) {
            totalRequests.incrementAndGet();
            totalProcessingTime.addAndGet(processingTime);
            updatePeakProcessingTime(processingTime);
        }

        public void recordError() {
            totalErrors.incrementAndGet();
        }

        private void updatePeakProcessingTime(long processingTime) {
            long currentPeak;
            do {
                currentPeak = peakProcessingTime.get();
                if (processingTime <= currentPeak) {
                    break;
                }
            } while (!peakProcessingTime.compareAndSet(currentPeak, processingTime));
        }

        public void reset() {
            totalRequests.set(0);
            totalProcessingTime.set(0);
            totalErrors.set(0);
            peakProcessingTime.set(0);
        }

        public PerformanceReport generateReport(LocalDateTime timestamp) {
            long requests = totalRequests.get();
            return new PerformanceReport(
                timestamp,
                requests,
                requests > 0 ? (double) totalProcessingTime.get() / requests : 0.0,
                requests > 0 ? (double) totalErrors.get() / requests * 100 : 0.0,
                peakProcessingTime.get()
            );
        }
    }

    private static class PerformanceReport {
        private final LocalDateTime timestamp;
        private final long totalRequests;
        private final double averageProcessingTime;
        private final double errorRate;
        private final long peakProcessingTime;

        public PerformanceReport(LocalDateTime timestamp, long totalRequests, 
                               double averageProcessingTime, double errorRate, 
                               long peakProcessingTime) {
            this.timestamp = timestamp;
            this.totalRequests = totalRequests;
            this.averageProcessingTime = averageProcessingTime;
            this.errorRate = errorRate;
            this.peakProcessingTime = peakProcessingTime;
        }

        public LocalDateTime getTimestamp() { return timestamp; }
        public long getTotalRequests() { return totalRequests; }
        public double getAverageProcessingTime() { return averageProcessingTime; }
        public double getErrorRate() { return errorRate; }
        public long getPeakProcessingTime() { return peakProcessingTime; }
    }
} 