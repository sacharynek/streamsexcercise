package pl.javastart.streamsexercise;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;

class PaymentService {

    private PaymentRepository paymentRepository;
    private DateTimeProvider dateTimeProvider;

    PaymentService(PaymentRepository paymentRepository, DateTimeProvider dateTimeProvider) {
        this.paymentRepository = paymentRepository;
        this.dateTimeProvider = dateTimeProvider;
    }

    List<Payment> findPaymentsSortedByDateDesc() {
        throw new RuntimeException("Not implemented");
    }

    List<Payment> findPaymentsForCurrentMonth() {
        return  paymentRepository
                .findAll()
                .stream()
                .filter(a -> dateTimeProvider.zonedDateTimeNow().getYear() == a.getPaymentDate().getYear() && dateTimeProvider.zonedDateTimeNow().getMonth() == a.getPaymentDate().getMonth())
                .collect(Collectors.toList());
    }

    List<Payment> findPaymentsForGivenMonth(YearMonth yearMonth) {
        throw new RuntimeException("Not implemented");
    }

    List<Payment> findPaymentsForGivenLastDays(int days) {


        return paymentRepository
                .findAll()
                .stream()
                .filter(a -> !dateTimeProvider.zonedDateTimeNow().toLocalDateTime().minusDays(days).isAfter(a.getPaymentDate().toLocalDateTime())   )
                .collect(Collectors.toList());
    }

    Set<Payment> findPaymentsWithOnePaymentItem() {
        return paymentRepository.findAll().stream().filter(a -> a.getPaymentItems().size() == 1).collect(Collectors.toSet());
    }

    Set<String> findProductsSoldInCurrentMonth() {
        throw new RuntimeException("Not implemented");
    }

    BigDecimal sumTotalForGivenMonth(YearMonth yearMonth) {
        throw new RuntimeException("Not implemented");
    }

    BigDecimal sumDiscountForGivenMonth(YearMonth yearMonth) {
        throw new RuntimeException("Not implemented");
    }

    List<PaymentItem> getPaymentsForUserWithEmail(String userEmail) {

        return paymentRepository
                .findAll()
                .stream()
                .filter(a -> a.getUser().getEmail().equals(userEmail))
                .flatMap(listContainer -> listContainer.getPaymentItems().stream())
                .collect(Collectors.toList());

    }

    Set<Payment> findPaymentsWithValueOver(int value) {
        return paymentRepository
                .findAll()
                .stream()
                .filter(a -> a.getPaymentItems().stream().mapToInt(x -> x.getFinalPrice().intValueExact()).sum() > value)
                .collect(Collectors.toSet());
    }

}
