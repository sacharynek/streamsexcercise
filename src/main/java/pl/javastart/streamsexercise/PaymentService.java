package pl.javastart.streamsexercise;

import java.math.BigDecimal;
import java.time.YearMonth;
import java.util.Comparator;
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
        return paymentRepository
                .findAll()
                .stream()
                .sorted((a,b) -> (a.getPaymentDate().compareTo(b.getPaymentDate())*-1))
                .collect(Collectors.toList());
    }

    List<Payment> findPaymentsForCurrentMonth() {
        return paymentRepository
                .findAll()
                .stream()
                .filter(a -> dateTimeProvider.zonedDateTimeNow().getYear() == a.getPaymentDate().getYear() && dateTimeProvider.zonedDateTimeNow().getMonth() == a.getPaymentDate().getMonth())
                .collect(Collectors.toList());
    }

    List<Payment> findPaymentsForGivenMonth(YearMonth yearMonth) {

        return paymentRepository
                .findAll()
                .stream()
                .filter(a -> yearMonth.equals(YearMonth.of(a.getPaymentDate().getYear(), a.getPaymentDate().getMonth())))
                .collect(Collectors.toList());

    }

    List<Payment> findPaymentsForGivenLastDays(int days) {

        return paymentRepository
                .findAll()
                .stream()
                .filter(a -> !dateTimeProvider.zonedDateTimeNow().toLocalDateTime().minusDays(days).isAfter(a.getPaymentDate().toLocalDateTime()))
                .collect(Collectors.toList());
    }

    Set<Payment> findPaymentsWithOnePaymentItem() {
        return paymentRepository.findAll().stream().filter(a -> a.getPaymentItems().size() == 1).collect(Collectors.toSet());
    }

    Set<String> findProductsSoldInCurrentMonth() {
        return paymentRepository
                .findAll()
                .stream()
                .filter(a -> dateTimeProvider.zonedDateTimeNow().getMonth().equals(a.getPaymentDate().getMonth()) && dateTimeProvider.zonedDateTimeNow().getYear() == a.getPaymentDate().getYear())
                .flatMap(listContainer -> listContainer.getPaymentItems().stream())
                .map(PaymentItem::getName)
                .collect(Collectors.toSet());
    }

    BigDecimal sumTotalForGivenMonth(YearMonth yearMonth) {
        return paymentRepository
                .findAll()
                .stream()
                .filter(a -> yearMonth.equals(YearMonth.of(a.getPaymentDate().getYear(), a.getPaymentDate().getMonth())))
                .flatMap(listContainer -> listContainer.getPaymentItems().stream())
                .map(PaymentItem::getFinalPrice)
                .reduce(BigDecimal.ZERO, BigDecimal::add);
    }

    BigDecimal sumDiscountForGivenMonth(YearMonth yearMonth) {
        return paymentRepository
                .findAll()
                .stream()
                .filter(a -> yearMonth.equals(YearMonth.of(a.getPaymentDate().getYear(), a.getPaymentDate().getMonth())))
                .flatMap(listContainer -> listContainer.getPaymentItems().stream())
                .map(a -> a.getRegularPrice().subtract(a.getFinalPrice() ))
                .reduce(BigDecimal.ZERO, BigDecimal::add);
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
