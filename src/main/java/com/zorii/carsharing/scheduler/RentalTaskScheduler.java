package com.zorii.carsharing.scheduler;

import com.zorii.carsharing.model.Rental;
import com.zorii.carsharing.model.User;
import com.zorii.carsharing.repository.RentalRepository;
import com.zorii.carsharing.repository.UserRepository;
import com.zorii.carsharing.service.NotificationService;
import java.time.LocalDate;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class RentalTaskScheduler {

  private final RentalRepository rentalRepository;
  private final NotificationService notificationService;
  private final UserRepository userRepository;

  @Scheduled(cron = "0 0 8 * * ?") // Runs daily at 8 AM
  public void checkOverdueRentals() {
    LocalDate today = LocalDate.now();

    List<Rental> overdueRentals = findOverdueRentals(today);

    if (overdueRentals.isEmpty()) {
      notifyAdmins("No rentals overdue today");
    } else {
      for (Rental rental : overdueRentals) {
        String message = buildOverdueRentalMessage(rental);
        notifyUser(rental, message);
        notifyAdmins(message);
      }
    }
  }

  private List<Rental> findOverdueRentals(LocalDate today) {
    return rentalRepository.findByReturnDateBeforeAndActualReturnDateIsNull(today);
  }

  private String buildOverdueRentalMessage(Rental rental) {
    return String.format(
        """
            Overdue Rental Alert!
            Car: %s
            User: %s
            Expected Return Date: %s""",
        rental.getCar().getModel(), rental.getUser().getEmail(), rental.getReturnDate());
  }

  private void notifyUser(Rental rental, String message) {
    if (rental.getUser().getTelegramChatId() != null) {
      notificationService.sendNotification(message, rental.getUser().getTelegramChatId());
    }
  }

  private void notifyAdmins(String message) {
    List<User> admins = userRepository.findByRole(User.Role.MANAGER);
    for (User admin : admins) {
      if (admin.getTelegramChatId() != null) {
        notificationService.sendNotification(message, admin.getTelegramChatId());
      }
    }
  }
}
