package com.college.bookmyslot.service;

import com.college.bookmyslot.dto.EventBookingRequest;
import com.college.bookmyslot.dto.EventBookingResponse;
import com.college.bookmyslot.model.Club;
import com.college.bookmyslot.model.Event;
import com.college.bookmyslot.model.EventBooking;
import com.college.bookmyslot.model.User;
import com.college.bookmyslot.repository.EventBookingRepository;
import com.college.bookmyslot.repository.EventRepository;
import com.college.bookmyslot.repository.UserRepository;
import com.college.bookmyslot.util.GoogleCalendarUtil;
import com.college.bookmyslot.util.QRCodeUtil;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.UUID;



@Service
public class EventBookingService {

    private final EventRepository eventRepository;
    private final EventBookingRepository eventBookingRepository;
    private final UserRepository userRepository;
    private final EmailService emailService;

    public EventBookingService(
            EventRepository eventRepository,
            EventBookingRepository eventBookingRepository,
            UserRepository userRepository,
            EmailService emailService
    ) {
        this.eventRepository = eventRepository;
        this.eventBookingRepository = eventBookingRepository;
        this.userRepository = userRepository;
        this.emailService = emailService;
    }

    public EventBookingResponse bookEvent(EventBookingRequest request) {

        Event event = eventRepository.findById(request.getEventId())
                .orElseThrow(() -> new RuntimeException("Event not found"));

        User student = userRepository.findById(request.getStudentId())
                .orElseThrow(() -> new RuntimeException("Student not found"));

        if (event.getBookedSlots() >= event.getTotalSlots()) {
            throw new RuntimeException("Event is fully booked");
        }

        eventBookingRepository.findByEventAndStudent(event, student)
                .ifPresent(b -> {
                    throw new RuntimeException("You have already booked this event");
                });

        EventBooking booking = new EventBooking();
        booking.setEvent(event);
        booking.setStudent(student);
        booking.setTicketId(UUID.randomUUID().toString());
        booking.setPaid(event.getEventType() == Event.EventType.PAID);
        booking.setAmountPaid(
                event.getEventType() == Event.EventType.PAID
                        ? event.getTicketPrice()
                        : 0.0
        );

        eventBookingRepository.save(booking);

        event.setBookedSlots(event.getBookedSlots() + 1);
        eventRepository.save(event);


        eventBookingRepository.save(booking);
        event.setBookedSlots(event.getBookedSlots() + 1);
        eventRepository.save(event);


        try {
            emailService.sendEventBookingEmail(
                    student.getEmail(),
                    student.getName(),
                    event.getTitle(),
                    event.getVenue(),
                    event.getEventDate().toString(),
                    event.getStartTime() + " - " + event.getEndTime(),
                    booking.getTicketId()
            );
        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }




        try {
            emailService.sendEventBookingEmail(
                    event.getClub().getEmail(),
                    event.getClub().getName(),
                    event.getTitle(),
                    event.getVenue(),
                    event.getEventDate().toString(),
                    event.getStartTime() + " - " + event.getEndTime(),
                    booking.getTicketId()
            );

        } catch (Exception e) {
            System.out.println("Email failed: " + e.getMessage());
        }
        return mapToResponse(booking);

    }


    public void checkInTicket(String ticketId, Long staffUserId) {

        EventBooking booking = eventBookingRepository.findByTicketId(ticketId)
                .orElseThrow(() -> new RuntimeException("Invalid ticket"));

        User staff = userRepository.findById(staffUserId)
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (staff.getRole() != User.Role.CLUB) {
            throw new RuntimeException("Only club staff can check-in tickets");
        }

        Club eventClub = booking.getEvent().getClub();
        if (staff.getClub() == null ||
                !staff.getClub().getId().equals(eventClub.getId())) {
            throw new RuntimeException("You are not authorized to check-in this event");
        }

        if (booking.isCheckedIn()) {
            throw new RuntimeException("Ticket already checked-in");
        }

        booking.setCheckedIn(true);
        booking.setCheckedInAt(LocalDateTime.now());

        eventBookingRepository.save(booking);
    }



    private EventBookingResponse mapToResponse(EventBooking booking) {



        Event event = booking.getEvent();
        User student = booking.getStudent();

        EventBookingResponse r = new EventBookingResponse();
        r.setTicketId(booking.getTicketId());

        r.setEventTitle(event.getTitle());
        r.setVenue(event.getVenue());
        r.setEventDate(event.getEventDate().toString());
        r.setEventTime(
                event.getStartTime().toString() + " - " + event.getEndTime().toString()
        );

        r.setStudentName(student.getName());
        r.setStudentEmail(student.getEmail());

        r.setPaid(booking.isPaid());
        r.setAmountPaid(booking.getAmountPaid());


        String qrPayload =
                "ticketId=" + booking.getTicketId() +
                        "&eventId=" + event.getId();

        String qrBase64 = QRCodeUtil.generateQRCode(qrPayload);

        r.setQrCodeUrl("data:image/png;base64," + qrBase64);

        String calendarLink = GoogleCalendarUtil.generateEventLink(
                event.getTitle(),
                event.getDescription(),
                event.getVenue(),
                event.getEventDate(),
                event.getStartTime(),
                event.getEndTime()
        );

        r.setGoogleCalendarLink(calendarLink);


        return r;




    }
}
