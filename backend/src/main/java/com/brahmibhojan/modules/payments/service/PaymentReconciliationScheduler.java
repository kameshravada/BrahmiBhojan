package com.brahmibhojan.modules.payments.service;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
@Slf4j
public class PaymentReconciliationScheduler {

    private final PaymentService paymentService;

    @Scheduled(cron = "${payment.reconciliation.cron:0 */5 * * * *}")
    public void reconcileStalePayments() {
        int reconciledCount = paymentService.reconcileStaleCreatedTransactions();
        if (reconciledCount > 0) {
            log.warn("payment reconciliation marked {} stale transaction(s) as failed", reconciledCount);
        }
    }
}

